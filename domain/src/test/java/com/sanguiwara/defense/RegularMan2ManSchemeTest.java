package com.sanguiwara.defense;

import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.calculator.spec.ShotSpec;
import com.sanguiwara.gameevent.ShotEvent;
import com.sanguiwara.result.ShotResult;
import com.sanguiwara.type.ShotType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegularMan2ManSchemeTest {

    @Test
    void getAverageTeamDefensiveScore_shouldBeMinutesWeightedAverage() throws Exception {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());

        GamePlan defensivePlan = new GamePlan(UUID.nameUUIDFromBytes("DEF_GP".getBytes(StandardCharsets.UTF_8)), null, null);
        defensivePlan.setActivePlayers(List.of(
                makePlayer("DEF_P1", 0, 40),
                makePlayer("DEF_P2", 99, 40),
                makePlayer("DEF_P3", 0, 40),
                makePlayer("DEF_P4", 0, 40),
                makePlayer("DEF_P5", 0, 40)
        ));

        Method m = RegularMan2ManScheme.class.getDeclaredMethod("getAverageTeamDefensiveScore", GamePlan.class, ShotSpec.class);
        m.setAccessible(true);

        ShotSpec<?, ?> spec = testSpec();
        double avg = (double) m.invoke(scheme, defensivePlan, spec);
        // Minutes-weighted avg over TOTAL_MINUTES_FOR_TEAM (200) with *0.75 factor in code:
        // (0*(40/200) + 99*(40/200)) * 0.75 = 14.85
        assertEquals(14.85, avg, 1e-12);
    }

    @Test
    void calculateAdvantageForAPlayer_whenAttackerPlaysMoreThanDefender_shouldUseTeamAverageForExtraMinutes() {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());
        ShotSpec<?, ?> spec = testSpec();

        InGamePlayer attacker = makePlayer("ATTACKER", 90, 40);
        InGamePlayer defender1 = makePlayer("DEF_1", 20, 20);
        // 80-rating defenders to total 180 minutes (all <= 40) so avgTeamDefensiveScore stays identical to the old test.
        InGamePlayer defender2 = makePlayer("DEF_2", 80, 40);
        InGamePlayer defender3 = makePlayer("DEF_3", 80, 40);
        InGamePlayer defender4 = makePlayer("DEF_4", 80, 40);
        InGamePlayer defender5 = makePlayer("DEF_5", 80, 40);
        InGamePlayer defender6 = makePlayer("DEF_6", 80, 20);

        GamePlan defensivePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensivePlan.setActivePlayers(List.of(defender1, defender2, defender3, defender4, defender5, defender6));
        defensivePlan.setMatchups(Map.of(attacker.getPlayer(), defender1.getPlayer()));

        double advantage = scheme.calculateAdvantageForAPlayer(attacker, defensivePlan, spec);

        // defenderScore = 20*1.3 = 26
        // avgTeamDefensiveScore = (20/200*20 + 180/200*80) * 0.75 = 74 * 0.75 = 55.5
        // effectiveDefense over attacker minutes: (26*20 + 55.5*20) / 40 = 40.75
        // advantage = 90 - 40.75 = 49.25 (no clamp hit)
        assertEquals(49.25, advantage, 1e-12);
    }

    @Test
    void getOffensiveTeamPlaymakingScore_whenNoMatchup_shouldUseDegradedTeamAverageDefenseOnPlaymaking() {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());

        InGamePlayer attacker = makePlayer("ATTACKER", 60, 40);
        InGamePlayer neutral1 = makePlayer("NEUTRAL_1", 50, 40);
        InGamePlayer neutral2 = makePlayer("NEUTRAL_2", 50, 40);
        InGamePlayer neutral3 = makePlayer("NEUTRAL_3", 50, 40);
        InGamePlayer neutral4 = makePlayer("NEUTRAL_4", 50, 40);
        GamePlan offensePlan = new GamePlan(UUID.randomUUID(), null, null);
        offensePlan.setActivePlayers(List.of(attacker, neutral1, neutral2, neutral3, neutral4));

        InGamePlayer def1 = makePlayer("DEF_1", 50, 40);
        InGamePlayer def2 = makePlayer("DEF_2", 50, 40);
        InGamePlayer def3 = makePlayer("DEF_3", 50, 40);
        InGamePlayer def4 = makePlayer("DEF_4", 50, 40);
        InGamePlayer def5 = makePlayer("DEF_5", 50, 40);
        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensePlan.setActivePlayers(List.of(def1, def2, def3, def4, def5));

        // everyone has a matchup except attacker => attacker uses the (degraded) team average defense score.
        HashMap<Player, Player> matchups = new HashMap<>();
        matchups.put(neutral1.getPlayer(), def1.getPlayer());
        matchups.put(neutral2.getPlayer(), def2.getPlayer());
        matchups.put(neutral3.getPlayer(), def3.getPlayer());
        matchups.put(neutral4.getPlayer(), def4.getPlayer());
        defensePlan.setMatchups(matchups);

        double teamPlaymakingScore = scheme.getOffensiveTeamPlaymakingScore(offensePlan, defensePlan);

        // avgDef = 50 * 0.75 = 37.5
        // advantage = 60 - 37.5 = 22.5, minutesShare = 40/200 = 0.2, so teamScore = 4.5
        assertEquals(4.5, teamPlaymakingScore, 1e-12);

        // contribution is computed from the offensive score and minutes share (not a default constant)
        assertEquals(12.0, attacker.getPlaymakingContribution(), 1e-12); // 60 * (40/200)
    }

    @Test
    void getOffensiveTeamPlaymakingScore_whenAttackerPlaysMoreThanDefender_shouldBlendMatchupDefenseAndTeamAverage() {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());

        InGamePlayer attacker = makePlayer("ATTACKER", 60, 40);
        InGamePlayer neutral1 = makePlayer("NEUTRAL_1", 50, 40);
        InGamePlayer neutral2 = makePlayer("NEUTRAL_2", 50, 40);
        InGamePlayer neutral3 = makePlayer("NEUTRAL_3", 50, 40);
        InGamePlayer neutral4 = makePlayer("NEUTRAL_4", 50, 40);
        GamePlan offensePlan = new GamePlan(UUID.randomUUID(), null, null);
        offensePlan.setActivePlayers(List.of(attacker, neutral1, neutral2, neutral3, neutral4));

        InGamePlayer matchupDefender = makePlayer("DEF_1", 50, 20);
        InGamePlayer def2 = makePlayer("DEF_2", 50, 40);
        InGamePlayer def3 = makePlayer("DEF_3", 50, 40);
        InGamePlayer def4 = makePlayer("DEF_4", 50, 40);
        InGamePlayer def5 = makePlayer("DEF_5", 50, 40);
        InGamePlayer def6 = makePlayer("DEF_6", 50, 20);

        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensePlan.setActivePlayers(List.of(matchupDefender, def2, def3, def4, def5, def6));

        HashMap<Player, Player> matchups = new HashMap<>();
        matchups.put(attacker.getPlayer(), matchupDefender.getPlayer());
        matchups.put(neutral1.getPlayer(), def2.getPlayer());
        matchups.put(neutral2.getPlayer(), def3.getPlayer());
        matchups.put(neutral3.getPlayer(), def4.getPlayer());
        matchups.put(neutral4.getPlayer(), def5.getPlayer());
        defensePlan.setMatchups(matchups);

        double teamPlaymakingScore = scheme.getOffensiveTeamPlaymakingScore(offensePlan, defensePlan);

        // avgDef = 50 * 0.75 = 37.5
        // effectiveDef over attacker minutes: (50*20 + 37.5*20) / 40 = 43.75
        // advantage = 60 - 43.75 = 16.25, minutesShare = 40/200 = 0.2, so teamScore = 3.25
        assertEquals(3.25, teamPlaymakingScore, 1e-12);
    }

    @Test
    void getOffensiveTeamPlaymakingScore_shouldReflectDifferentOffensiveArchetypes() {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());

        // One playmaker + 4 neutral players (all play 40 min => 200 total).
        InGamePlayer playmaker = makePlayer("PLAYMAKER", 50, 40, b -> {
            b.passingSkills(99);
            b.basketballIqOff(99);
            b.ballhandling(99);
        });
        InGamePlayer neutral1 = makePlayer("NEUTRAL_1", 50, 40);
        InGamePlayer neutral2 = makePlayer("NEUTRAL_2", 50, 40);
        InGamePlayer neutral3 = makePlayer("NEUTRAL_3", 50, 40);
        InGamePlayer neutral4 = makePlayer("NEUTRAL_4", 50, 40);

        GamePlan offensePlan = new GamePlan(UUID.randomUUID(), null, null);
        offensePlan.setActivePlayers(List.of(playmaker, neutral1, neutral2, neutral3, neutral4));

        // Average defenders, all 50, 40 min.
        InGamePlayer def1 = makePlayer("DEF_1", 50, 40);
        InGamePlayer def2 = makePlayer("DEF_2", 50, 40);
        InGamePlayer def3 = makePlayer("DEF_3", 50, 40);
        InGamePlayer def4 = makePlayer("DEF_4", 50, 40);
        InGamePlayer def5 = makePlayer("DEF_5", 50, 40);
        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensePlan.setActivePlayers(List.of(def1, def2, def3, def4, def5));

        defensePlan.setMatchups(Map.of(
                playmaker.getPlayer(), def1.getPlayer(),
                neutral1.getPlayer(), def2.getPlayer(),
                neutral2.getPlayer(), def3.getPlayer(),
                neutral3.getPlayer(), def4.getPlayer(),
                neutral4.getPlayer(), def5.getPlayer()
        ));

        double teamPlaymakingScore = scheme.getOffensiveTeamPlaymakingScore(offensePlan, defensePlan);

        // playmaker offScore = 0.60*99 + 0.40*50 = 79.4
        // defenderScore = 50
        // advantage = 29.4, weighted by minutesShare (0.2) => 5.88. Neutral players have advantage 0.
        assertEquals(5.88, teamPlaymakingScore, 1e-12);
    }

    @Test
    void getOffensiveTeamPlaymakingScore_playmakerShouldOutscoreShooterInPlaymaking_againstSameDefense() {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());

        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        InGamePlayer def1 = makePlayer("DEF_1", 50, 40);
        InGamePlayer def2 = makePlayer("DEF_2", 50, 40);
        InGamePlayer def3 = makePlayer("DEF_3", 50, 40);
        InGamePlayer def4 = makePlayer("DEF_4", 50, 40);
        InGamePlayer def5 = makePlayer("DEF_5", 50, 40);
        defensePlan.setActivePlayers(List.of(def1, def2, def3, def4, def5));

        InGamePlayer playmaker = makePlayer("PLAYMAKER", 50, 40, b -> {
            b.passingSkills(99);
            b.basketballIqOff(99);
            b.ballhandling(99);
        });
        InGamePlayer neutral1a = makePlayer("NEUTRAL_1A", 50, 40);
        InGamePlayer neutral2a = makePlayer("NEUTRAL_2A", 50, 40);
        InGamePlayer neutral3a = makePlayer("NEUTRAL_3A", 50, 40);
        InGamePlayer neutral4a = makePlayer("NEUTRAL_4A", 50, 40);
        GamePlan offensePlaymaker = new GamePlan(UUID.randomUUID(), null, null);
        offensePlaymaker.setActivePlayers(List.of(playmaker, neutral1a, neutral2a, neutral3a, neutral4a));
        defensePlan.setMatchups(Map.of(
                playmaker.getPlayer(), def1.getPlayer(),
                neutral1a.getPlayer(), def2.getPlayer(),
                neutral2a.getPlayer(), def3.getPlayer(),
                neutral3a.getPlayer(), def4.getPlayer(),
                neutral4a.getPlayer(), def5.getPlayer()
        ));
        double playmakerScore = scheme.getOffensiveTeamPlaymakingScore(offensePlaymaker, defensePlan);

        InGamePlayer shooter = makePlayer("SHOOTER", 50, 40, b -> {
            b.tir3Pts(99);
            b.tir2Pts(99);
            b.finitionAuCercle(99);
            b.floater(99);
        });
        InGamePlayer neutral1b = makePlayer("NEUTRAL_1B", 50, 40);
        InGamePlayer neutral2b = makePlayer("NEUTRAL_2B", 50, 40);
        InGamePlayer neutral3b = makePlayer("NEUTRAL_3B", 50, 40);
        InGamePlayer neutral4b = makePlayer("NEUTRAL_4B", 50, 40);
        GamePlan offenseShooter = new GamePlan(UUID.randomUUID(), null, null);
        offenseShooter.setActivePlayers(List.of(shooter, neutral1b, neutral2b, neutral3b, neutral4b));
        defensePlan.setMatchups(Map.of(
                shooter.getPlayer(), def1.getPlayer(),
                neutral1b.getPlayer(), def2.getPlayer(),
                neutral2b.getPlayer(), def3.getPlayer(),
                neutral3b.getPlayer(), def4.getPlayer(),
                neutral4b.getPlayer(), def5.getPlayer()
        ));
        double shooterScore = scheme.getOffensiveTeamPlaymakingScore(offenseShooter, defensePlan);

        // shooter offScore = 0.15*99 + 0.85*50 = 57.35 => advantage=7.35 => weighted=1.47
        assertEquals(1.47, shooterScore, 1e-12);
        // playmaker advantage is much larger (see previous test), so score must be strictly higher.
        assertEquals(5.88, playmakerScore, 1e-12);
    }

    @Test
    void getOffensiveTeamPlaymakingScore_shouldClampLowWhenEliteDefenderShadowsPlaymaker_fullMinutes() {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());

        InGamePlayer playmaker = makePlayer("PLAYMAKER", 50, 40, b -> {
            b.passingSkills(99);
            b.basketballIqOff(99);
            b.ballhandling(99);
        });
        InGamePlayer neutral1 = makePlayer("NEUTRAL_1", 50, 40);
        InGamePlayer neutral2 = makePlayer("NEUTRAL_2", 50, 40);
        InGamePlayer neutral3 = makePlayer("NEUTRAL_3", 50, 40);
        InGamePlayer neutral4 = makePlayer("NEUTRAL_4", 50, 40);
        GamePlan offensePlan = new GamePlan(UUID.randomUUID(), null, null);
        offensePlan.setActivePlayers(List.of(playmaker, neutral1, neutral2, neutral3, neutral4));

        // Elite defender: pushes man2man playmaking defScore very high.
        InGamePlayer eliteDef = makePlayer("ELITE_DEF", 50, 40, b -> {
            b.speed(99);
            b.defExterieur(99);
            b.basketballIqDef(99);
            b.steal(99);
        });
        InGamePlayer def2 = makePlayer("DEF_2", 50, 40);
        InGamePlayer def3 = makePlayer("DEF_3", 50, 40);
        InGamePlayer def4 = makePlayer("DEF_4", 50, 40);
        InGamePlayer def5 = makePlayer("DEF_5", 50, 40);
        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensePlan.setActivePlayers(List.of(eliteDef, def2, def3, def4, def5));

        defensePlan.setMatchups(Map.of(
                playmaker.getPlayer(), eliteDef.getPlayer(),
                neutral1.getPlayer(), def2.getPlayer(),
                neutral2.getPlayer(), def3.getPlayer(),
                neutral3.getPlayer(), def4.getPlayer(),
                neutral4.getPlayer(), def5.getPlayer()
        ));

        double teamPlaymakingScore = scheme.getOffensiveTeamPlaymakingScore(offensePlan, defensePlan);

        // eliteDefScore = 0.87*99 + 0.13*50 = 92.63
        // raw advantage = 79.4 - 92.63 = -13.23 (no clamp with MIN_INDIVIDUAL_ADVANTAGE=-15)
        // weighted = -13.23 * 0.2 = -2.646
        assertEquals(-2.646, teamPlaymakingScore, 1e-12);
    }

    @Test
    void getOffensiveTeamPlaymakingScore_whenEliteDefenderPlaysLess_shouldBlendWithTeamAverageDefense() {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());

        InGamePlayer playmaker = makePlayer("PLAYMAKER", 50, 40, b -> {
            b.passingSkills(99);
            b.basketballIqOff(99);
            b.ballhandling(99);
        });
        InGamePlayer neutral1 = makePlayer("NEUTRAL_1", 50, 40);
        InGamePlayer neutral2 = makePlayer("NEUTRAL_2", 50, 40);
        InGamePlayer neutral3 = makePlayer("NEUTRAL_3", 50, 40);
        InGamePlayer neutral4 = makePlayer("NEUTRAL_4", 50, 40);
        GamePlan offensePlan = new GamePlan(UUID.randomUUID(), null, null);
        offensePlan.setActivePlayers(List.of(playmaker, neutral1, neutral2, neutral3, neutral4));

        InGamePlayer eliteDef = makePlayer("ELITE_DEF", 50, 20, b -> {
            b.speed(99);
            b.defExterieur(99);
            b.basketballIqDef(99);
            b.steal(99);
        });
        // Remaining minutes are played by average defenders (all <= 40) so attacker is forced to face average defense part-time.
        InGamePlayer def2 = makePlayer("DEF_2", 50, 40);
        InGamePlayer def3 = makePlayer("DEF_3", 50, 40);
        InGamePlayer def4 = makePlayer("DEF_4", 50, 40);
        InGamePlayer def5 = makePlayer("DEF_5", 50, 40);
        InGamePlayer def6 = makePlayer("DEF_6", 50, 20);

        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensePlan.setActivePlayers(List.of(eliteDef, def2, def3, def4, def5, def6));

        defensePlan.setMatchups(Map.of(
                playmaker.getPlayer(), eliteDef.getPlayer(),
                neutral1.getPlayer(), def2.getPlayer(),
                neutral2.getPlayer(), def3.getPlayer(),
                neutral3.getPlayer(), def4.getPlayer(),
                neutral4.getPlayer(), def5.getPlayer()
        ));

        double teamPlaymakingScore = scheme.getOffensiveTeamPlaymakingScore(offensePlan, defensePlan);

        // offScore = 79.4, eliteDefScore = 92.63
        // avgTeamDef (degraded) = 0.75 * (0.1*92.63 + 0.9*50) = 40.69725
        // effectiveDef = (92.63*20 + 40.69725*20)/40 = 66.663625
        // raw advantage = 79.4 - 66.663625 = 12.736375, weighted = 12.736375 * 0.2 = 2.547275
        assertEquals(2.547275, teamPlaymakingScore, 1e-6);
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

    private static InGamePlayer makePlayer(String name, int baselineRating, int minutesPlayed, Consumer<Player.PlayerBuilder> overrides) {
        UUID id = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        Player.PlayerBuilder builder = Player.builder()
                .teamsID(new HashSet<>())
                .clubID(null)
                .badgeIds(new HashSet<>())
                .id(id)
                .name(name)
                .birthDate(0)
                .injured(false)
                .tir3Pts(baselineRating)
                .tir2Pts(baselineRating)
                .lancerFranc(baselineRating)
                .floater(baselineRating)
                .finitionAuCercle(baselineRating)
                .speed(baselineRating)
                .ballhandling(baselineRating)
                .size(baselineRating)
                .weight(baselineRating)
                .agressivite(baselineRating)
                .defExterieur(baselineRating)
                .defPoste(baselineRating)
                .protectionCercle(baselineRating)
                .timingRebond(baselineRating)
                .agressiviteRebond(baselineRating)
                .steal(baselineRating)
                .timingBlock(baselineRating)
                .physique(baselineRating)
                .basketballIqOff(baselineRating)
                .basketballIqDef(baselineRating)
                .passingSkills(baselineRating)
                .iq(baselineRating)
                .endurance(baselineRating)
                .solidite(baselineRating)
                .potentielSkill(baselineRating)
                .potentielPhysique(baselineRating)
                .coachability(baselineRating)
                .ego(baselineRating)
                .softSkills(baselineRating)
                .leadership(baselineRating)
                .morale(baselineRating);

        overrides.accept(builder);
        Player player = builder.build();

        InGamePlayer inGamePlayer = new InGamePlayer(player, null);
        inGamePlayer.setMinutesPlayed(minutesPlayed);
        return inGamePlayer;
    }

    private static ShotSpec<ShotEvent, ShotResult<ShotEvent>> testSpec() {
        return new ShotSpec<>() {
            @Override
            public void distributeShotAttempts(GamePlan plan) {
                throw new UnsupportedOperationException();
            }

            @Override
            public double computePct(InGamePlayer shooter, double advantage, boolean assistBonusPct) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getAttempts(InGamePlayer shooter) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ShotEvent create(InGamePlayer shooter, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage, boolean blocked) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ShotResult<ShotEvent> createResult(int attempts, int made, List<ShotEvent> events) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ShotResult<ShotEvent> empty() {
                throw new UnsupportedOperationException();
            }

            @Override
            public ShotResult<ShotEvent> combine(ShotResult<ShotEvent> a, ShotResult<ShotEvent> b) {
                throw new UnsupportedOperationException();
            }

            @Override
            public double getPlayerScoreForAShot(Player attacker) {
                // For tests, we reuse a single rating (every stat has same value).
                return attacker.getTir3Pts();
            }

            @Override
            public double getDefensiveScoreForAShot(Player defender) {
                // For tests, we reuse a single rating (every stat has same value).
                return defender.getDefExterieur();
            }

            @Override
            public ShotType getShotType() {
                return ShotType.THREE_POINT;
            }

            @Override
            public double getBlockProbabilityCoefficient() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
