package calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.calculator.ShotSimulator;
import com.sanguiwara.calculator.spec.ShotSpec;
import com.sanguiwara.gameevent.ShotEvent;
import com.sanguiwara.result.ShotResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ShotSimulatorTest {

    private static final long SEED = 42L;
    private Random rng;

    @BeforeEach
    void init() {
        rng = new Random(SEED);
    }

    // Minimal event/result types for testing generic ShotSimulator
    private record TestShotEvent(UUID shooterId, int index, boolean assisted, UUID assisterId, double successPct, boolean made, double advantageMatchup) implements ShotEvent {}
    private record TestShotResult(int attempts, int made, List<TestShotEvent> events) implements ShotResult<TestShotEvent> {}

    private static Player p(String name) {
        int v = 50;
        return new Player(UUID.randomUUID(), name, 1990,
                v, v, v, v, v, v, v, v, v, v,
                v, v, v, v, v, v,
                v, v, v, v, v, v,
                v,
                v, v,
                v, v, v, v);
    }

    private static class FakeShotSpec implements ShotSpec<TestShotEvent, TestShotResult> {
        private final int attempts;
        private final double pct;

        FakeShotSpec(int attempts, double pct) {
            this.attempts = attempts;
            this.pct = pct;
        }
        //TODO Ajouter des tests unitaires pour les 3 ShootSpec

        @Override public int sampleAttempts(InGamePlayer shooter) { return attempts; }
        @Override public double computePct(InGamePlayer shooter, double advantage, boolean isAssistedShot) { return pct; }
        @Override public double evaluateMatchupAdvantage(Player attacker, Player defender) { return 0.0; }
        @Override public TestShotEvent create(InGamePlayer shooter, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage) {
            return new TestShotEvent(shooter.getPlayer().id(), shotNumber, assisted, assisterId, pct, made, advantage);
        }
        @Override public TestShotResult createResult(int attempts, int made, List<TestShotEvent> events) { return new TestShotResult(attempts, made, events); }
        @Override public TestShotResult empty() { return new TestShotResult(0,0, List.of()); }
        @Override public TestShotResult combine(TestShotResult a, TestShotResult b) {
            List<TestShotEvent> ev = new ArrayList<>(a.events().size()+b.events().size());
            ev.addAll(a.events());
            ev.addAll(b.events());
            return new TestShotResult(a.attempts()+b.attempts(), a.made()+b.made(), Collections.unmodifiableList(ev));
        }
    }

    @Test
    void getAssister_shouldRespectAssistProbability_andWeights() {
        ShotSimulator<TestShotEvent, TestShotResult> sim = new ShotSimulator<>(rng, new FakeShotSpec(1, 1.0));

        InGamePlayer shooter = new InGamePlayer(p("SHOOTER"), 20, 10, 10);
        InGamePlayer p1 = new InGamePlayer(p("P1"), 10, 10, 10);
        InGamePlayer p2 = new InGamePlayer(p("P2"), 10, 10, 10);
        p1.setAssistWeight(1.0);
        p2.setAssistWeight(3.0);
        List<InGamePlayer> all = List.of(shooter, p1, p2);

        // With 0 probability -> no assist
        InGamePlayer none = sim.getAssister(shooter, all, 0.0);
        log.info("Assister with 0.0 prob: {}", none);
        assertNull(none);

        // With 1 probability -> always assisted and not the shooter
        InGamePlayer a = sim.getAssister(shooter, all, 1.0);
        log.info("Assister with 1.0 prob (one pick): {}", a != null ? a.getPlayer().name() : null);
        assertNotNull(a);
        assertNotEquals(shooter, a);

        // Statistical preference for higher weight
        int countP1 = 0, countP2 = 0;
        for (int i = 0; i < 2000; i++) {
            InGamePlayer as = sim.getAssister(shooter, all, 1.0);
            if (as == p1) countP1++; else if (as == p2) countP2++;
        }
        log.info("Assister distribution over 2000 picks -> p1: {}, p2: {}", countP1, countP2);
        assertTrue(countP2 > countP1, "Heavier assistWeight should be picked more often");
    }

    @Test
    void simulateShots_allUnassisted_allMade_andNoAssistsRecorded() {
        // pct=1.0 ensures all made
        ShotSimulator<TestShotEvent, TestShotResult> sim = new ShotSimulator<>(rng, new FakeShotSpec(10, 1.0));

        InGamePlayer shooter = new InGamePlayer(p("SHOOTER"), 20, 10, 10);
        InGamePlayer other = new InGamePlayer(p("OTHER"), 10, 10, 10);
        other.setAssistWeight(1.0);

        TestShotResult res = sim.simulateShots(shooter, List.of(shooter, other), 0.0, 0.0);

        assertEquals(10, res.attempts());
        assertEquals(10, res.made());
        long assistedCount = res.events().stream().filter(TestShotEvent::assisted).count();
        log.info("Unassisted scenario -> attempts: {}, made: {}, assisted events: {}", res.attempts(), res.made(), assistedCount);
        assertEquals(0, assistedCount);
        assertEquals(0, shooter.getAssists());
        assertEquals(0, other.getAssists());
    }

    @Test
    void simulateShots_allAssisted_allMade_andAssistsIncremented() {
        ShotSimulator<TestShotEvent, TestShotResult> sim = new ShotSimulator<>(rng, new FakeShotSpec(8, 1.0));
        InGamePlayer shooter = new InGamePlayer(p("SHOOTER"), 20, 10, 10);
        InGamePlayer passer = new InGamePlayer(p("P1"), 10, 10, 10);
        passer.setAssistWeight(1.0);

        TestShotResult res = sim.simulateShots(shooter, List.of(shooter, passer), 1.0, 0.0);

        assertEquals(8, res.attempts());
        assertEquals(8, res.made());
        long assistedCount = res.events().stream().filter(TestShotEvent::assisted).count();
        log.info("All assisted scenario -> attempts: {}, made: {}, assisted events: {}, passer assists: {}",
                res.attempts(), res.made(), assistedCount, passer.getAssists());
        assertEquals(8, assistedCount);
        assertEquals(8, passer.getAssists());
    }

    @Test
    void getTotalShotContribution_aggregatesAcrossPlayers() {
        ShotSimulator<TestShotEvent, TestShotResult> sim = new ShotSimulator<>(rng, new FakeShotSpec(5, 1.0));

        InGamePlayer off1 = new InGamePlayer(p("O1"), 20, 10, 10);
        InGamePlayer off2 = new InGamePlayer(p("O2"), 20, 10, 10);
        off1.setAssistWeight(0.5);
        off2.setAssistWeight(0.5);

        GamePlan home = new GamePlan(null, null, null);
        home.setActivePlayers(List.of(off1, off2));

        // Build defense with matchups for both attackers
        Player d1 = p("D1");
        Player d2 = p("D2");
        Map<Player, Player> matchups = new HashMap<>();
        matchups.put(off1.getPlayer(), d1);
        matchups.put(off2.getPlayer(), d2);

        GamePlan defense = new GamePlan(null, null, null);
        defense.setMatchups(matchups);

        TestShotResult total = sim.getTotalShotContribution(home, defense, 1.0);
        // Each of the two players attempts 5 -> total 10, all made due to pct=1.0
        log.info("Aggregated result -> attempts: {}, made: {}, events: {}", total.attempts(), total.made(), total.events().size());
        assertEquals(10, total.attempts());
        assertEquals(10, total.made());
        assertEquals(10, total.events().size());
        assertTrue(off1.getAssists() == 5 && off2.getAssists() == 5, "Assists should be recorded for both players");
    }
}
