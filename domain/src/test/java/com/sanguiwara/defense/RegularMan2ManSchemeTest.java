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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegularMan2ManSchemeTest {

    @Test
    void getAverageTeamDefensiveScore_shouldBeMinutesWeightedAverage() throws Exception {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());

        GamePlan defensivePlan = new GamePlan(UUID.nameUUIDFromBytes("DEF_GP".getBytes(StandardCharsets.UTF_8)), null, null);
        defensivePlan.setActivePlayers(List.of(
                makePlayer("DEF_P1", 0, 100),
                makePlayer("DEF_P2", 99, 100)
        ));

        Method m = RegularMan2ManScheme.class.getDeclaredMethod("getAverageTeamDefensiveScore", GamePlan.class, ShotSpec.class);
        m.setAccessible(true);

        ShotSpec<?, ?> spec = testSpec();
        double avg = (double) m.invoke(scheme, defensivePlan, spec);
        // Minutes-weighted avg over TOTAL_MINUTES_FOR_TEAM (200) with *0.75 factor in code:
        // (0*(100/200) + 99*(100/200)) * 0.75 = 37.125
        assertEquals(37.125, avg, 1e-12);
    }

    @Test
    void calculateAdvantageForAPlayer_whenAttackerPlaysMoreThanDefender_shouldUseTeamAverageForExtraMinutes() {
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(new BadgeEngine());
        ShotSpec<?, ?> spec = testSpec();

        InGamePlayer attacker = makePlayer("ATTACKER", 90, 40);
        InGamePlayer defender1 = makePlayer("DEF_1", 20, 20);
        InGamePlayer defender2 = makePlayer("DEF_2", 80, 180);

        GamePlan defensivePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensivePlan.setActivePlayers(List.of(defender1, defender2));
        defensivePlan.setMatchups(Map.of(attacker.getPlayer(), defender1.getPlayer()));

        double advantage = scheme.calculateAdvantageForAPlayer(attacker, defensivePlan, spec);

        // defenderScore = 20*1.3 = 26
        // avgTeamDefensiveScore = (20/200*20 + 180/200*80) * 0.75 = 74 * 0.75 = 55.5
        // effectiveDefense over attacker minutes: (26*20 + 55.5*20) / 40 = 40.75
        // advantage = 90 - 40.75 = 49.25 (no clamp hit)
        assertEquals(49.25, advantage, 1e-12);
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
