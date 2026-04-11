package com.sanguiwara.calculator;

import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.MatchupAttacker;
import com.sanguiwara.baserecords.MatchupDefender;
import com.sanguiwara.baserecords.Matchups;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.defense.*;
import org.junit.jupiter.api.Test;
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

    // BadgeCatalog.PLAYMAKER_ID is package-private; keep the stable ID here for tests.
    private static final long PLAYMAKER_BADGE_ID = 5L;

    @ParameterizedTest(name = "getPercentageFromScore anchors - score={0} -> expected={1}")
    @CsvSource({
            "-20.0, 0.05",
            "20.0, 0.70"
    })
    void getPercentageFromScore_shouldMatchAnchorPoints(double score, double expected) {
        AssistCalculator calc = new AssistCalculator(null);
        assertEquals(expected, calc.getPercentageFromScore(score), 1e-12);
    }

    @ParameterizedTest(name = "getPercentageFromScore clamp - score={0} -> expected={1}")
    @CsvSource({
            "-999.0, 0.05",
            "999.0, 0.70"
    })
    void getPercentageFromScore_shouldClampOutsideRange(double score, double expected) {
        AssistCalculator calc = new AssistCalculator(null);
        assertEquals(expected, calc.getPercentageFromScore(score), 1e-12);
    }

    @ParameterizedTest(name = "[{index}] def={0} off={1} vs defTeam={2} pm={3} badges={4}")
    @MethodSource("assistProbabilityMatrixCases")
    void calculateAssistProbability_matrix_shouldPrint(
            DefenseType defenseType,
            TeamStrength offenseStrength,
            TeamStrength defenseStrength,
            int playmakerCount,
            BadgeMode badgeMode
    ) {
        AssistEnv env = newAssistEnv();

        GamePlan offense = makeTeamGamePlan(
                "OFF_" + offenseStrength.name() + "_PM" + playmakerCount + "_" + badgeMode.name(),
                offenseStrength.rating,
                playmakerCount,
                badgeMode,
                40
        );
        GamePlan defense = makeTeamGamePlan(
                "DEF_" + defenseStrength.name(),
                defenseStrength.rating,
                0,
                BadgeMode.NONE,
                40
        );
        defense.setDefenseType(defenseType);
        wireMatchupsForManToMan(offense, defense);

        DefensiveScheme scheme = env.resolver.resolve(defenseType);
        double teamPlaymakingScore = scheme.getOffensiveTeamPlaymakingScore(offense, defense);
        double assistProb = env.calc.calculateAssistProbability(offense, defense);

        assertFalse(Double.isNaN(teamPlaymakingScore), "teamPlaymakingScore should not be NaN");
        assertFalse(Double.isNaN(assistProb), "assistProb should not be NaN");
        assertTrue(assistProb >= AssistCalculator.MIN_ASSIST_PROBABILITY && assistProb <= AssistCalculator.MAX_ASSIST_PROBABILITY,
                "assistProb should be clamped in [0.05..0.70], got=" + assistProb);
    }

    @Test
    void calculateAssistProbability_matrixSummary_shouldPrintReadableTables() {
        // This test is intentionally "printy": it produces a compact summary of the matrix so it's readable
        // when tuning constants. It also acts as a smoke test (values must stay within clamp bounds).
        for (DefenseType defenseType : testedDefenseTypes()) {
            for (BadgeMode badgeMode : BadgeMode.values()) {
                printSummaryTable(defenseType, badgeMode);
            }
        }
    }

    private static void printSummaryTable(DefenseType defenseType, BadgeMode badgeMode) {
        AssistEnv env = newAssistEnv();

        System.out.println();
        DefensiveScheme scheme = env.resolver.resolve(defenseType);
        String schemeName = scheme == null ? "null" : scheme.getClass().getSimpleName();
        System.out.printf(Locale.ROOT, "=== Assist Probability | defense=%s | scheme=%s | badges=%s ===%n", defenseType, schemeName, badgeMode);
        System.out.println("Cell format: p0/p1/p2 where pN is assistProbability with N playmakers on offense.");
        System.out.println();

        List<TeamStrength> strengths = List.of(TeamStrength.values());

        // Header
        System.out.printf(Locale.ROOT, "%-12s", "OFF\\DEF");
        for (TeamStrength defStrength : strengths) {
            System.out.printf(Locale.ROOT, " | %-14s", strengthLabel(defStrength));
        }
        System.out.println();

        // Rows
        for (TeamStrength offStrength : strengths) {
            System.out.printf(Locale.ROOT, "%-12s", strengthLabel(offStrength));
            for (TeamStrength defStrength : strengths) {
                GamePlan defense = makeTeamGamePlan(
                        "DEF_" + defStrength.name(),
                        defStrength.rating,
                        0,
                        BadgeMode.NONE,
                        40
                );
                defense.setDefenseType(defenseType);

                double p0 = assistProbFor(env, offStrength, 0, badgeMode, defense);
                double p1 = assistProbFor(env, offStrength, 1, badgeMode, defense);
                double p2 = assistProbFor(env, offStrength, 2, badgeMode, defense);

                assertTrue(p0 >= AssistCalculator.MIN_ASSIST_PROBABILITY && p0 <= AssistCalculator.MAX_ASSIST_PROBABILITY);
                assertTrue(p1 >= AssistCalculator.MIN_ASSIST_PROBABILITY && p1 <= AssistCalculator.MAX_ASSIST_PROBABILITY);
                assertTrue(p2 >= AssistCalculator.MIN_ASSIST_PROBABILITY && p2 <= AssistCalculator.MAX_ASSIST_PROBABILITY);

                String cell = String.format(Locale.ROOT, "%.3f/%.3f/%.3f", p0, p1, p2);
                System.out.printf(Locale.ROOT, " | %-14s", cell);
            }
            System.out.println();
        }
    }

    private static String strengthLabel(TeamStrength s) {
        return switch (s) {
            case VERY_WEAK -> "VW(15)";
            case WEAK -> "W(30)";
            case NORMAL -> "N(50)";
            case STRONG -> "S(70)";
            case VERY_STRONG -> "VS(90)";
        };
    }

    @Test
    void calculateAssistProbability_shouldIncreaseWithMorePlaymakers_forSameMatchup() {
        // Property-style check: for a fixed matchup, adding 1-2 playmakers should not decrease assistProbability.
        // This is intentionally broad (not tied to exact numeric outputs).
        for (DefenseType defenseType : testedDefenseTypes()) {
            for (TeamStrength offenseStrength : TeamStrength.values()) {
                for (TeamStrength defenseStrength : TeamStrength.values()) {
                    for (BadgeMode badgeMode : BadgeMode.values()) {
                        AssistEnv env = newAssistEnv();

                        GamePlan defense = makeTeamGamePlan(
                                "DEF_" + defenseStrength.name(),
                                defenseStrength.rating,
                                0,
                                BadgeMode.NONE,
                                40
                        );
                        defense.setDefenseType(defenseType);

                        double p0 = assistProbFor(env, offenseStrength, 0, badgeMode, defense);
                        double p1 = assistProbFor(env, offenseStrength, 1, badgeMode, defense);
                        double p2 = assistProbFor(env, offenseStrength, 2, badgeMode, defense);

                        assertTrue(p0 <= p1 + 1e-12,
                                "Expected p0 <= p1 for def=" + defenseType + " off=" + offenseStrength + " defTeam=" + defenseStrength + " badges=" + badgeMode +
                                        " but got p0=" + p0 + " p1=" + p1);
                        assertTrue(p1 <= p2 + 1e-12,
                                "Expected p1 <= p2 for def=" + defenseType + " off=" + offenseStrength + " defTeam=" + defenseStrength + " badges=" + badgeMode +
                                        " but got p1=" + p1 + " p2=" + p2);
                    }
                }
            }
        }
    }

    private static double assistProbFor(AssistEnv env, TeamStrength offenseStrength, int playmakerCount, BadgeMode badgeMode, GamePlan defense) {
        GamePlan offense = makeTeamGamePlan(
                "OFF_" + offenseStrength.name() + "_PM" + playmakerCount + "_" + badgeMode.name(),
                offenseStrength.rating,
                playmakerCount,
                badgeMode,
                40
        );
        wireMatchupsForManToMan(offense, defense);
        return env.calc.calculateAssistProbability(offense, defense);
    }

    @Test
    void calculateAssistProbability_manToMan_playmakerBadge_shouldIncreaseProbability() {
        AssistEnv env = newAssistEnv();

        GamePlan defense = makeTeamGamePlan(
                "DEF_STRONG",
                TeamStrength.STRONG.rating,
                0,
                BadgeMode.NONE,
                40
        );
        defense.setDefenseType(DefenseType.MAN_TO_MAN);

        DefensiveScheme scheme = env.resolver.resolve(DefenseType.MAN_TO_MAN);

        GamePlan offenseNoBadge = makeTeamGamePlan(
                "OFF_NORMAL_PM1_NONE",
                TeamStrength.NORMAL.rating,
                1,
                BadgeMode.NONE,
                40
        );
        wireMatchupsForManToMan(offenseNoBadge, defense);
        double scoreNoBadge = scheme.getOffensiveTeamPlaymakingScore(offenseNoBadge, defense);
        double pNoBadge = env.calc.calculateAssistProbability(offenseNoBadge, defense);

        GamePlan offenseWithBadge = makeTeamGamePlan(
                "OFF_NORMAL_PM1_PLAYMAKER",
                TeamStrength.NORMAL.rating,
                1,
                BadgeMode.PLAYMAKER,
                40
        );
        wireMatchupsForManToMan(offenseWithBadge, defense);
        double scoreWithBadge = scheme.getOffensiveTeamPlaymakingScore(offenseWithBadge, defense);
        double pWithBadge = env.calc.calculateAssistProbability(offenseWithBadge, defense);

        assertTrue(scoreWithBadge > scoreNoBadge + 1e-12,
                "Expected playmaker badge to increase teamPlaymakingScore in MAN_TO_MAN, but got noBadge=" + scoreNoBadge + " withBadge=" + scoreWithBadge);
        assertTrue(pWithBadge > pNoBadge + 1e-12,
                "Expected playmaker badge to increase assist probability in MAN_TO_MAN, but got noBadge=" + pNoBadge + " withBadge=" + pWithBadge);
    }

    private enum BadgeMode {
        NONE,
        PLAYMAKER
    }

    private enum TeamStrength {
        VERY_WEAK(15),
        WEAK(30),
        NORMAL(50),
        STRONG(70),
        VERY_STRONG(90);

        final int rating;

        TeamStrength(int rating) {
            this.rating = rating;
        }
    }

    private static final class AssistEnv {
        private final DefenseSchemeResolver resolver;
        private final AssistCalculator calc;

        private AssistEnv(DefenseSchemeResolver resolver, AssistCalculator calc) {
            this.resolver = resolver;
            this.calc = calc;
        }
    }

    private static AssistEnv newAssistEnv() {
        BadgeEngine badgeEngine = new BadgeEngine();
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );
        DefenseSchemeResolver resolver = new DefenseSchemeResolver(schemes);
        AssistCalculator calc = new AssistCalculator(resolver);
        return new AssistEnv(resolver, calc);
    }

    private static List<DefenseType> testedDefenseTypes() {
        return List.of(
                DefenseType.MAN_TO_MAN,
                DefenseType.ZONE_2_3,
                DefenseType.ZONE_2_1_2,
                DefenseType.ZONE_3_2
        );
    }

    private static GamePlan makeTeamGamePlan(
            String teamName,
            int baseRatingEverywhere,
            int playmakerCount,
            BadgeMode badgeMode,
            int minutesPerPlayer
    ) {
        GamePlan plan = new GamePlan(UUID.nameUUIDFromBytes((teamName + "_GP").getBytes(StandardCharsets.UTF_8)), null, null);

        // 5 players * 40 minutes = 200 (matches internal TEAM minutes constants).
        List<InGamePlayer> players = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            boolean isPlaymaker = i <= playmakerCount;
            InGamePlayer igp = makePlayer(teamName + "_P" + i, baseRatingEverywhere, minutesPerPlayer, isPlaymaker);
            if (isPlaymaker && badgeMode == BadgeMode.PLAYMAKER) {
                // Apply a real badge from BadgeCatalog: multiplies PLAYMAKING_CONTRIBUTION for ASSIST context.
                // Note: in RegularMan2ManScheme, this affects assist weights but not the teamPlaymakingScore (advantage-based).
                igp.getPlayer().getBadgeIds().add(PLAYMAKER_BADGE_ID);
            }
            players.add(igp);
        }

        plan.setActivePlayers(players);
        return plan;
    }

    static Stream<Arguments> assistProbabilityMatrixCases() {
        List<TeamStrength> strengths = List.of(TeamStrength.values());
        List<DefenseType> defenseTypes = testedDefenseTypes();
        List<Integer> playmakerCounts = List.of(0, 1, 2);
        List<BadgeMode> badgeModes = List.of(BadgeMode.values());

        List<Arguments> args = new ArrayList<>();
        for (DefenseType defenseType : defenseTypes) {
            for (TeamStrength offenseStrength : strengths) {
                for (TeamStrength defenseStrength : strengths) {
                    for (int playmakerCount : playmakerCounts) {
                        for (BadgeMode badgeMode : badgeModes) {
                            args.add(Arguments.of(defenseType, offenseStrength, defenseStrength, playmakerCount, badgeMode));
                        }
                    }
                }
            }
        }
        return args.stream();
    }

    private static void wireMatchupsForManToMan(GamePlan offense, GamePlan defense) {
        // Man-to-man schemes require defense.matchups keyed by defensive player (defender -> attacker).
        // For zone schemes this is ignored, but wiring it doesn't hurt and keeps test uniform.
        Matchups matchups = Matchups.empty();
        List<InGamePlayer> off = offense.getActivePlayers();
        List<InGamePlayer> def = defense.getActivePlayers();
        int n = Math.min(off.size(), def.size());
        for (int i = 0; i < n; i++) {
            matchups.assign(
                    new MatchupDefender(def.get(i).getPlayer()),
                    new MatchupAttacker(off.get(i).getPlayer())
            );
        }
        defense.setMatchups(matchups);
    }

    private static InGamePlayer makePlayer(String name, int ratingEverywhere, int minutesPlayed, boolean isPlaymaker) {
        UUID id = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        int playmakerRating = 90;
        int passingSkills = isPlaymaker ? playmakerRating : ratingEverywhere;
        int ballhandling = isPlaymaker ? playmakerRating : ratingEverywhere;
        int basketballIqOff = isPlaymaker ? playmakerRating : ratingEverywhere;
        int coachability = isPlaymaker ? playmakerRating : ratingEverywhere;
        int speed = isPlaymaker ? playmakerRating : ratingEverywhere;

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
                .speed(speed)
                .ballhandling(ballhandling)
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
                .basketballIqOff(basketballIqOff)
                .basketballIqDef(ratingEverywhere)
                .passingSkills(passingSkills)
                .iq(ratingEverywhere)
                .endurance(ratingEverywhere)
                .solidite(ratingEverywhere)
                .potentielSkill(ratingEverywhere)
                .potentielPhysique(ratingEverywhere)
                .coachability(coachability)
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
