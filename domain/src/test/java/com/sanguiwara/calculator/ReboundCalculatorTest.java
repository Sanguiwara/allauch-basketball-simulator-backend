package com.sanguiwara.calculator;

import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReboundCalculatorTest {

    private static final Logger log = LoggerFactory.getLogger(ReboundCalculatorTest.class);
    private final BadgeEngine badgeEngine = new BadgeEngine();

    /**
     * Random déterministe + logs : on voit exactement quelles valeurs sortent et à quel moment.
     */
    static class DeterministicRandom extends Random {
        private final List<Double> values;
        private int idx = 0;
        private final String label;

        DeterministicRandom(String label, List<Double> values) {
            this.label = label;
            this.values = new ArrayList<>(values);
        }

        @Override
        public double nextDouble() {
            double v = (idx >= values.size()) ? 0.0 : values.get(idx);
            log.debug("[{}] Random#nextDouble() -> {} (idx={}/{})",
                    label, String.format("%.4f", v), idx + 1, values.size());
            idx++;
            return v;
        }
    }

    // ---------- Helpers Mockito (stats 0..100) -------------------------------

    private static Player mockPlayer(
            String name,
            int size, int weight,
            int agressivite,
            int agressiviteRebond,
            int timingRebond,
            int physique,
            int iq,
            int endurance
    ) {
        Player p = mock(Player.class);
        when(p.getName()).thenReturn(name);

        when(p.getSize()).thenReturn(size);
        when(p.getWeight()).thenReturn(weight);
        when(p.getAgressivite()).thenReturn(agressivite);
        when(p.getAgressiviteRebond()).thenReturn(agressiviteRebond);
        when(p.getTimingRebond()).thenReturn(timingRebond);
        when(p.getPhysique()).thenReturn(physique);
        when(p.getIq()).thenReturn(iq);
        when(p.getEndurance()).thenReturn(endurance);

        return p;
    }

    private static InGamePlayer mockInGamePlayer(Player player, int minutesPlayed, double reboundContribution, double reboundWeight) {
        InGamePlayer igp = mock(InGamePlayer.class);

        when(igp.getPlayer()).thenReturn(player);
        when(igp.getMinutesPlayed()).thenReturn(minutesPlayed);

        when(igp.getReboundContribution()).thenReturn(reboundContribution);
        when(igp.getReboundWeight()).thenReturn(reboundWeight);

        return igp;
    }

    private static List<Double> concat(List<Double> a, List<Double> b) {
        ArrayList<Double> out = new ArrayList<>(a.size() + b.size());
        out.addAll(a);
        out.addAll(b);
        return out;
    }




    // ------------------- TESTS -----------------------------------------------

    @Test
    void evaluateOffensiveReboundForTeam_shouldAddOffensiveRebounds_toRebounderWhenReboundHappens() {
        log.info("=== TEST: credit rebonds quand ils arrivent (random forcé) ===");

        // 10 tirs, 10 picks. On force toujours rebond (0.0 < p) et on force pick du 1er.
        List<Double> shotRolls = List.of(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
        List<Double> pickRolls = List.of(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
        Random random = new DeterministicRandom("FORCED_ALL_REB", concat(shotRolls, pickRolls));

        ReboundCalculator calculator = new ReboundCalculator(random, badgeEngine);

        GamePlan home = mock(GamePlan.class);
        GamePlan visitor = mock(GamePlan.class);
        when(home.getTotalShotNumber()).thenReturn(10);

        Player pA = mockPlayer("A", 100,100,100,100,100,100,100,100);
        InGamePlayer a = mockInGamePlayer(pA, 200, 1.0, 1.0);

        Player pB = mockPlayer("B", 50,50,50,50,50,50,50,50);
        InGamePlayer b = mockInGamePlayer(pB, 200, 1.0, 0.0);

        when(home.getActivePlayers()).thenReturn(List.of(a, b));
        when(visitor.getActivePlayers()).thenReturn(List.of()); // avantage max => p plafonnée à 0.40

        log.info("Setup: shots={}, homePlayers=2 (A weight=1.0, B weight=0.0), visitorPlayers=0",
                home.getTotalShotNumber());

        int totalShots = calculator.evaluateOffensiveReboundForTeam(home, visitor);

        log.info("Assert: totalShots={}", totalShots);
        //assertEquals(10, totalShots);

        log.info("Verify: A doit prendre 10 rebonds offensifs, B 0");
        verify(a, times(10)).addOffensiveRebound();
        verify(b, never()).addOffensiveRebound();
    }

    @Test
    void testReboundProbability() {
        Player pA = mockPlayer("A", 99,99,99,99,99,99,99,99);
        InGamePlayer a = mockInGamePlayer(pA, 20, 0, 0);
        Player pB = mockPlayer("A", 99,99,99,99,99,99,99,99);
        InGamePlayer b = mockInGamePlayer(pB, 20, 0, 0);
        Player pC = mockPlayer("A", 99,99,99,99,99,99,99,99);
        InGamePlayer c = mockInGamePlayer(pC, 20, 0, 0);
        Player pD = mockPlayer("A", 99,99,99,99,99,99,99,99);
        InGamePlayer d = mockInGamePlayer(pD, 20, 0, 0);
        Player pE = mockPlayer("A", 99,99,99,99,99,99,99,99);
        InGamePlayer e = mockInGamePlayer(pE, 20, 0, 0);

        Player paA = mockPlayer("A", 20,20,20,20,20,20,20,20);
        InGamePlayer aa = mockInGamePlayer(paA, 20, 0, 0);
        Player paB = mockPlayer("A", 20,20,20,20,20,20,20,20);
        InGamePlayer ab = mockInGamePlayer(paB, 20, 0, 0);
        Player paC = mockPlayer("A", 20,20,20,20,20,20,20,20);
        InGamePlayer ac = mockInGamePlayer(paC, 20, 0, 0);
        Player paD = mockPlayer("A", 20,20,20,20,20,20,20,20);
        InGamePlayer ad = mockInGamePlayer(paD, 20, 0, 0);
        Player paE = mockPlayer("A", 20,20,20,20,20,20,20,20);
        InGamePlayer ae = mockInGamePlayer(paE, 20, 0, 0);

        GamePlan home = mock(GamePlan.class);
        GamePlan visitor = mock(GamePlan.class);
        when(home.getTotalShotNumber()).thenReturn(100);
        when(visitor.getTotalShotNumber()).thenReturn(100);

        when(home.getActivePlayers()).thenReturn(List.of(a,b,c,d,e));
        when(visitor.getActivePlayers()).thenReturn(List.of(aa,ab,ac,ad,ae));
        ReboundCalculator calculator = new ReboundCalculator(new Random(), badgeEngine);
        int totalShotsDom = calculator.evaluateOffensiveReboundForTeam(home, visitor);
        int totalShotsNul = calculator.evaluateOffensiveReboundForTeam(visitor, home);

        log.info("totalShots={}", totalShotsDom);
        log.info("totalShots={}", totalShotsNul);

        assertTrue(totalShotsDom > totalShotsNul);
    }







}
