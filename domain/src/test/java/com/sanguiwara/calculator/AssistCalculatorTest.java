package com.sanguiwara.calculator;

import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.defense.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistCalculatorTest {

    @ParameterizedTest(name = "getPercentageFromScore anchors - score={0} -> expected={1}")
    @CsvSource({
            "-50.0, 0.15",
            "50.0, 0.50"
    })
    void getPercentageFromScore_shouldMatchAnchorPoints(double score, double expected) {
        AssistCalculator calc = new AssistCalculator(null);
        assertEquals(expected, calc.getPercentageFromScore(score), 1e-12);
    }

    @ParameterizedTest(name = "getPercentageFromScore clamp - score={0} -> expected={1}")
    @CsvSource({
            "-999.0, 0.15",
            "999.0, 0.50"
    })
    void getPercentageFromScore_shouldClampOutsideRange(double score, double expected) {
        AssistCalculator calc = new AssistCalculator(null);
        assertEquals(expected, calc.getPercentageFromScore(score), 1e-12);
    }

    @ParameterizedTest(name = "[{index}] defense={0} offense={1} defenseTeam={2}")
    @MethodSource("calculateAssistProbabilityCases")
    void calculateAssistProbability_shouldPrintCase_forTeamStrengthsAndDefenseScheme(
            DefenseType defenseType,
            TeamStrength offenseStrength,
            TeamStrength defenseStrength
    ) {
        BadgeEngine badgeEngine = new BadgeEngine();
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );
        DefenseSchemeResolver resolver = new DefenseSchemeResolver(schemes);
        AssistCalculator calc = new AssistCalculator(resolver);

        GamePlan offense = makeTeamGamePlan("OFF_" + offenseStrength.name(), offenseStrength.rating, DefenseType.ZONE_2_3);
        GamePlan defense = makeTeamGamePlan("DEF_" + defenseStrength.name(), defenseStrength.rating, defenseType);
        wireMatchupsForManToMan(offense, defense);

        DefensiveScheme scheme = resolver.resolve(defenseType);
        double teamPlaymakingScore = scheme.getOffensiveTeamPlaymakingScore(offense, defense);
        double assistProb = calc.calculateAssistProbability(offense, defense);

        assertFalse(Double.isNaN(teamPlaymakingScore), "teamPlaymakingScore should not be NaN");
        assertFalse(Double.isNaN(assistProb), "assistProb should not be NaN");
        assertTrue(assistProb >= 0.15 && assistProb <= 0.50,
                "assistProb should be clamped in [0.15..0.50], got=" + assistProb);

        System.out.printf(
                "defense=%s offense=%s(%d) vs defenseTeam=%s(%d) | teamPlaymakingScore=%8.3f -> assistProb=%.3f%n",
                defenseType,
                offenseStrength.name(),
                offenseStrength.rating,
                defenseStrength.name(),
                defenseStrength.rating,
                teamPlaymakingScore,
                assistProb
        );
    }

    private enum TeamStrength {
        WEAK(30),
        AVERAGE(60),
        STRONG(90);

        final int rating;

        TeamStrength(int rating) {
            this.rating = rating;
        }
    }

    private static GamePlan makeTeamGamePlan(String teamName, int ratingEverywhere, DefenseType defenseType) {
        GamePlan plan = new GamePlan(UUID.nameUUIDFromBytes((teamName + "_GP").getBytes(StandardCharsets.UTF_8)), null, null);
        plan.setDefenseType(defenseType);

        // 5 players * 40 minutes = 200 (matches internal TEAM minutes constants).
        List<InGamePlayer> players = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            players.add(makePlayer(teamName + "_P" + i, ratingEverywhere, 40));
        }
        plan.setActivePlayers(players);
        return plan;
    }

    static Stream<Arguments> calculateAssistProbabilityCases() {
        List<TeamStrength> strengths = List.of(TeamStrength.WEAK, TeamStrength.AVERAGE, TeamStrength.STRONG);
        List<DefenseType> defenseTypes = List.of(
                DefenseType.MAN_TO_MAN,
                DefenseType.ZONE_2_3,
                DefenseType.ZONE_2_1_2,
                DefenseType.ZONE_3_2
        );

        List<Arguments> args = new ArrayList<>();
        for (DefenseType defenseType : defenseTypes) {
            for (TeamStrength offenseStrength : strengths) {
                for (TeamStrength defenseStrength : strengths) {
                    args.add(Arguments.of(defenseType, offenseStrength, defenseStrength));
                }
            }
        }
        return args.stream();
    }

    private static void wireMatchupsForManToMan(GamePlan offense, GamePlan defense) {
        // Man-to-man schemes require defense.matchups keyed by offensive Player.
        // For zone schemes this is ignored, but wiring it doesn't hurt and keeps test uniform.
        Map<Player, Player> matchups = new HashMap<>();
        List<InGamePlayer> off = offense.getActivePlayers();
        List<InGamePlayer> def = defense.getActivePlayers();
        int n = Math.min(off.size(), def.size());
        for (int i = 0; i < n; i++) {
            matchups.put(off.get(i).getPlayer(), def.get(i).getPlayer());
        }
        defense.setMatchups(matchups);
    }

    private static InGamePlayer makePlayer(String name, int ratingEverywhere, int minutesPlayed) {
        UUID id = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        Player player = Player.builder()
                .teamsID(new HashSet<>())
                .clubID(null)
                .badgeIds(new HashSet<>())
                .id(id)
                .name(name)
                .birthDate(0)
                .injured(false)
                .tir3Pts(ratingEverywhere)
                .tir2Pts(ratingEverywhere)
                .lancerFranc(ratingEverywhere)
                .floater(ratingEverywhere)
                .finitionAuCercle(ratingEverywhere)
                .speed(ratingEverywhere)
                .ballhandling(ratingEverywhere)
                .size(ratingEverywhere)
                .weight(ratingEverywhere)
                .agressivite(ratingEverywhere)
                .defExterieur(ratingEverywhere)
                .defPoste(ratingEverywhere)
                .protectionCercle(ratingEverywhere)
                .timingRebond(ratingEverywhere)
                .agressiviteRebond(ratingEverywhere)
                .steal(ratingEverywhere)
                .timingBlock(ratingEverywhere)
                .physique(ratingEverywhere)
                .basketballIqOff(ratingEverywhere)
                .basketballIqDef(ratingEverywhere)
                .passingSkills(ratingEverywhere)
                .iq(ratingEverywhere)
                .endurance(ratingEverywhere)
                .solidite(ratingEverywhere)
                .potentielSkill(ratingEverywhere)
                .potentielPhysique(ratingEverywhere)
                .coachability(ratingEverywhere)
                .ego(ratingEverywhere)
                .softSkills(ratingEverywhere)
                .leadership(ratingEverywhere)
                .morale(ratingEverywhere)
                .build();

        InGamePlayer inGamePlayer = new InGamePlayer(player, null);
        inGamePlayer.setMinutesPlayed(minutesPlayed);
        return inGamePlayer;
    }
}
