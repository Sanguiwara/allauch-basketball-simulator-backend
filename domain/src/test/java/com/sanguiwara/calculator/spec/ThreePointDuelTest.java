package com.sanguiwara.calculator.spec;

import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.baserecords.*;
import com.sanguiwara.defense.RegularMan2ManScheme;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class ThreePointDuelTest {

    private static final long SHOT_SIM_SEED = 123L;

    @Test
    void computePct_regularMan2Man_individualMatchup_noBadges_logsPctAnd100Shots() {
        BadgeEngine badgeEngine = new BadgeEngine();
        ThreePointSpecification spec = new ThreePointSpecification(new Random(0L), badgeEngine);
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(badgeEngine);

        // If you want to tweak the duel, edit these:
        int attackerRatingEverywhere = 95;
        int defenderRatingEverywhere = 84;

        InGamePlayer attacker = inGamePlayer("ATT", attackerRatingEverywhere, 40, b -> {
            // These stats are used by ThreePointSpecification.getPlayerScoreForAShot (weights sum to 1.0).
            b.tir3Pts(attackerRatingEverywhere);
            b.speed(attackerRatingEverywhere);
            b.size(attackerRatingEverywhere);
            b.endurance(attackerRatingEverywhere);
            b.basketballIqOff(attackerRatingEverywhere);
        });
        InGamePlayer defender = inGamePlayer("DEF", defenderRatingEverywhere, 40, b -> {
            // These stats are used by ThreePointSpecification.getDefensiveScoreForAShot (weights sum to 1.0).
            b.defExterieur(defenderRatingEverywhere);
            b.speed(defenderRatingEverywhere);
            b.size(defenderRatingEverywhere);
            b.endurance(defenderRatingEverywhere);
            b.basketballIqDef(defenderRatingEverywhere);
        });

        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensePlan.setActivePlayers(List.of(defender));
        defensePlan.setMatchups(Matchups.of(Map.of(
                new MatchupDefender(defender.getPlayer()), new MatchupAttacker(attacker.getPlayer())
        )));

        double advantage = scheme.calculateAdvantageForAPlayer(attacker, defensePlan, spec);
        double pct = spec.computePct(attacker, advantage, false);

        double offScore = spec.getPlayerScoreForAShot(attacker.getPlayer());
        double defScore = spec.getDefensiveScoreForAShot(defender.getPlayer());
        log.info("3PT duel (NO BADGES, indiv matchup): offScore={}, defScore={}, advantage={}, pct={}",
                offScore, defScore, advantage, pct);

        int made = simulateShots(100, pct, new Random(SHOT_SIM_SEED));
        log.info("3PT duel (NO BADGES): made={}/100 (pct={})", made, pct);

        // Regression guard for the specific scenario above.
        assertEquals(0.17295959595959587, pct, 1e-12); // clamped to min
        assertTrue(made >= 0 && made <= 100);
    }

    @Test
    void computePct_regularMan2Man_individualMatchup_withAutoPrecision3ptsPlatine_logsPctAnd100Shots() {
        BadgeEngine badgeEngine = new BadgeEngine();
        ThreePointSpecification spec = new ThreePointSpecification(new Random(0L), badgeEngine);
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(badgeEngine);

        // If you want to tweak the duel, edit these:
        int attackerRatingEverywhere = 99;
        int defenderRatingEverywhere = 84;

        InGamePlayer attacker = inGamePlayer("ATT", attackerRatingEverywhere, 40, b -> {
            b.tir3Pts(attackerRatingEverywhere);
            b.speed(attackerRatingEverywhere);
            b.size(attackerRatingEverywhere);
            b.endurance(attackerRatingEverywhere);
            b.basketballIqOff(attackerRatingEverywhere);
        });
        // Trigger auto-skill badge sync through the custom setter (Platine >= 90 => +0.060 on 3PT SHOT_PCT).
        attacker.getPlayer().setTir3Pts(attackerRatingEverywhere);

        InGamePlayer defender = inGamePlayer("DEF", defenderRatingEverywhere, 40, b -> {
            b.defExterieur(defenderRatingEverywhere);
            b.speed(defenderRatingEverywhere);
            b.size(defenderRatingEverywhere);
            b.endurance(defenderRatingEverywhere);
            b.basketballIqDef(defenderRatingEverywhere);
        });

        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensePlan.setActivePlayers(List.of(defender));
        defensePlan.setMatchups(Matchups.of(Map.of(
                new MatchupDefender(defender.getPlayer()), new MatchupAttacker(attacker.getPlayer())
        )));

        double advantage = scheme.calculateAdvantageForAPlayer(attacker, defensePlan, spec);
        double pct = spec.computePct(attacker, advantage, false);

        double offScore = spec.getPlayerScoreForAShot(attacker.getPlayer());
        double defScore = spec.getDefensiveScoreForAShot(defender.getPlayer());
        log.info("3PT duel (AUTO 3PT PLATINE, indiv matchup): offScore={}, defScore={}, advantage={}, pct={}",
                offScore, defScore, advantage, pct);

        int made = simulateShots(100, pct, new Random(SHOT_SIM_SEED));
        log.info("3PT duel (AUTO 3PT PLATINE): made={}/100 (pct={})", made, pct);

        // Regression guard for the specific scenario above:
        // basePct = 99/99 * 0.10 = 0.10
        // advantagePct = 15/50 * 0.35 = 0.105
        // auto 3PT Platine badge adds +0.060 on SHOT_PCT => total = 0.265
        assertEquals(0.265, pct, 1e-12);
        assertTrue(made >= 0 && made <= 100);
    }

    @Test
    void computePct_regularMan2Man_withoutMatchup_usesTeamAverageDefense_logsPctAnd100Shots() {
        BadgeEngine badgeEngine = new BadgeEngine();
        ThreePointSpecification spec = new ThreePointSpecification(new Random(0L), badgeEngine);
        RegularMan2ManScheme scheme = new RegularMan2ManScheme(badgeEngine);

        int attackerRatingEverywhere = 95;
        int defenderRatingEverywhere = 84;

        InGamePlayer attacker = inGamePlayer("ATT", attackerRatingEverywhere, 40, b -> {
            b.tir3Pts(attackerRatingEverywhere);
            b.speed(attackerRatingEverywhere);
            b.size(attackerRatingEverywhere);
            b.endurance(attackerRatingEverywhere);
            b.basketballIqOff(attackerRatingEverywhere);
        });

        // No matchup: RegularMan2ManScheme falls back to getAverageTeamDefensiveScore(...) with a *0.75 degradation.
        InGamePlayer defender = inGamePlayer("DEF", defenderRatingEverywhere, 200, b -> {
            b.defExterieur(defenderRatingEverywhere);
            b.speed(defenderRatingEverywhere);
            b.size(defenderRatingEverywhere);
            b.endurance(defenderRatingEverywhere);
            b.basketballIqDef(defenderRatingEverywhere);
        });

        GamePlan defensePlan = new GamePlan(UUID.randomUUID(), null, null);
        defensePlan.setActivePlayers(List.of(defender));
        defensePlan.setMatchups(Matchups.empty()); // intentionally empty

        double advantage = scheme.calculateAdvantageForAPlayer(attacker, defensePlan, spec);
        double pct = spec.computePct(attacker, advantage, false);

        double offScore = spec.getPlayerScoreForAShot(attacker.getPlayer());
        double teamDefScore = spec.getDefensiveScoreForAShot(defender.getPlayer()) * 0.75; // see scheme code path
        log.info("3PT duel (NO MATCHUP -> team avg defense): offScore={}, teamDefScore(raw*0.75)={}, advantage={}, pct={}",
                offScore, teamDefScore, advantage, pct);

        int made = simulateShots(100, pct, new Random(SHOT_SIM_SEED));
        log.info("3PT duel (NO MATCHUP): made={}/100 (pct={})", made, pct);

        assertTrue(pct >= 0.075 && pct <= 0.95);
        assertTrue(made >= 0 && made <= 100);
    }

    private static int simulateShots(int attempts, double pct, Random random) {
        int made = 0;
        for (int i = 0; i < attempts; i++) {
            if (random.nextDouble() < pct) {
                made++;
            }
        }
        return made;
    }

    private static InGamePlayer inGamePlayer(String name, int baselineRating, int minutesPlayed, Consumer<Player.PlayerBuilder> overrides) {
        UUID id = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));

        // Baseline: set everything to baselineRating, then override only what matters for the scenario.
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

        // Defensive/offensive auto-badges are synced only via custom setters; by default, tests run with no badges
        // unless you explicitly call a setter (ex: setTir3Pts) in the test.
        if (player.getBadgeIds() == null) {
            player.setBadgeIds(new HashSet<>());
        } else if (!(player.getBadgeIds() instanceof HashSet)) {
            player.setBadgeIds(new HashSet<>(player.getBadgeIds()));
        }

        InGamePlayer p = new InGamePlayer(player, UUID.randomUUID());
        p.setMinutesPlayed(minutesPlayed);
        return p;
    }
}
