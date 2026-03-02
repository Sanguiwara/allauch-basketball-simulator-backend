package calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.calculator.ShotSimulator;
import com.sanguiwara.calculator.spec.ShotSpec;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.defense.*;
import com.sanguiwara.gameevent.ShotEvent;
import com.sanguiwara.result.ShotResult;
import com.sanguiwara.type.ShotType;
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
    private record TestShotEvent(UUID shooterId, int index, boolean assisted, UUID assisterId, double successPct, boolean made, double advantageMatchup, boolean blocked) implements ShotEvent {
        @Override
        public ShotType shotType() {
            return ShotType.TWO_POINT;
        }
    }
    private record TestShotResult(int attempts, int made, List<TestShotEvent> events) implements ShotResult<TestShotEvent> {}

    private static Player p(String name) {
        return p(name, 50);
    }

    private static Player p(String name, int morale) {
        int v = 50;
        return Player.builder()
                .id(UUID.randomUUID())
                .name(name)
                .birthDate(1990)
                .injured(false)
                .tir3Pts(v)
                .tir2Pts(v)
                .lancerFranc(v)
                .floater(v)
                .finitionAuCercle(v)
                .speed(v)
                .ballhandling(v)
                .size(v)
                .weight(v)
                .agressivite(v)
                .defExterieur(v)
                .defPoste(v)
                .protectionCercle(v)
                .timingRebond(v)
                .agressiviteRebond(v)
                .steal(v)
                .timingBlock(v)
                .physique(v)
                .basketballIqOff(v)
                .basketballIqDef(v)
                .passingSkills(v)
                .iq(v)
                .endurance(v)
                .solidite(v)
                .potentielSkill(v)
                .potentielPhysique(v)
                .coachability(v)
                .ego(v)
                .softSkills(v)
                .leadership(v)
                .morale(morale)
                .build();
    }

    private static class FakeShotSpec implements ShotSpec<TestShotEvent, TestShotResult> {
        private final int attempts;
        private final double pct;

        FakeShotSpec(int attempts, double pct) {
            this.attempts = attempts;
            this.pct = pct;
        }
        //TODO Ajouter des tests unitaires pour les 3 ShootSpec

        @Override public int getAttempts(InGamePlayer shooter) { return attempts; }

        @Override
        public void distributeShotAttempts(GamePlan plan) {

        }

        @Override public double computePct(InGamePlayer shooter, double advantage, boolean isAssistedShot) { return pct; }
        @Override public TestShotEvent create(InGamePlayer shooter, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage, boolean blocked) {
            return new TestShotEvent(shooter.getPlayer().getId(), shotNumber, assisted, assisterId, pct, made, advantage, blocked);
        }
        @Override public TestShotResult createResult(int attempts, int made, List<TestShotEvent> events) { return new TestShotResult(attempts, made, events); }
        @Override public TestShotResult empty() { return new TestShotResult(0,0, List.of()); }
        @Override public TestShotResult combine(TestShotResult a, TestShotResult b) {
            List<TestShotEvent> ev = new ArrayList<>(a.events().size()+b.events().size());
            ev.addAll(a.events());
            ev.addAll(b.events());
            return new TestShotResult(a.attempts()+b.attempts(), a.made()+b.made(), Collections.unmodifiableList(ev));
        }

        @Override
        public double getPlayerScoreForAShot(Player attacker) {
            return 0;
        }

        @Override
        public double getDefensiveScoreForAShot(Player defender) {
            return 0;
        }

        @Override
        public ShotType getShotType() {
            return null;
        }

        @Override
        public double getBlockProbabilityCoefficient() {
            return 1;
        }
    }

    @Test
    void pickAssister_shouldRespectAssistProbability_andWeights() {
        BadgeEngine badgeEngine = new BadgeEngine();
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );
        DefenseSchemeResolver defenseSchemeResolver = new DefenseSchemeResolver(schemes);

        ShotSimulator<TestShotEvent, TestShotResult> sim = new ShotSimulator<>(rng, new FakeShotSpec(1, 1.0),defenseSchemeResolver);

        InGamePlayer shooter = new InGamePlayer(p("SHOOTER"),null);
        InGamePlayer p1 = new InGamePlayer(p("P1"),null);
        InGamePlayer p2 = new InGamePlayer(p("P2"),null);
        p1.setAssistWeight(1.0);
        p2.setAssistWeight(3.0);
        List<InGamePlayer> all = List.of( p1, p2);

        // With 0 probability -> no assist
        InGamePlayer none = sim.pickAssister( all, 0.0);
        log.info("Assister with 0.0 prob: {}", none);
        assertNull(none);

        // With 1 probability -> always assisted and not the shooter
        InGamePlayer a = sim.pickAssister( all, 1.0);
        log.info("Assister with 1.0 prob (one pick): {}", a != null ? a.getPlayer().getName() : null);
        assertNotNull(a);
        assertNotEquals(shooter, a);

        // Statistical preference for higher weight
        int countP1 = 0, countP2 = 0;
        for (int i = 0; i < 2000; i++) {
            InGamePlayer as = sim.pickAssister( all, 1.0);
            if (as == p1) countP1++; else if (as == p2) countP2++;
        }
        log.info("Assister distribution over 2000 picks -> p1: {}, p2: {}", countP1, countP2);
        assertTrue(countP2 > countP1, "Heavier assistWeight should be picked more often");
    }




    @Test
    void getTotalShotContribution_aggregatesAcrossPlayers() {
        BadgeEngine badgeEngine = new BadgeEngine();
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );
        DefenseSchemeResolver defenseSchemeResolver = new DefenseSchemeResolver(schemes);
        ShotSimulator<TestShotEvent, TestShotResult> sim = new ShotSimulator<>(rng, new FakeShotSpec(5, 1.0), defenseSchemeResolver);

        InGamePlayer off1 = new InGamePlayer(p("O1"),null);
        InGamePlayer off2 = new InGamePlayer(p("O2"),null);
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

        TestShotResult total = sim.getTotalShotContribution(home, defense, 1.0, 0.1);
        // Each of the two players attempts 5 -> total 10, all made due to pct=1.0
        log.info("Aggregated result -> attempts: {}, made: {}, events: {}", total.attempts(), total.made(), total.events().size());
        assertEquals(10, total.attempts());
        assertEquals(10, total.made());
        assertEquals(10, total.events().size());
        assertTrue(off1.getAssists() == 5 && off2.getAssists() == 5, "Assists should be recorded for both players");
    }

    @Test
    void simulateShots_appliesMoraleBonus_toShotPct() {
        BadgeEngine badgeEngine = new BadgeEngine();
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );
        DefenseSchemeResolver defenseSchemeResolver = new DefenseSchemeResolver(schemes);

        // Base pct is 0.50; morale 99 => +0.20 => 0.70 (before clamping).
        ShotSimulator<TestShotEvent, TestShotResult> sim = new ShotSimulator<>(rng, new FakeShotSpec(1, 0.50), defenseSchemeResolver);

        InGamePlayer off1 = new InGamePlayer(p("O1", 99), null);
        GamePlan home = new GamePlan(null, null, null);
        home.setActivePlayers(List.of(off1));

        Player d1 = p("D1");
        Map<Player, Player> matchups = new HashMap<>();
        matchups.put(off1.getPlayer(), d1);
        GamePlan defense = new GamePlan(null, null, null);
        defense.setMatchups(matchups);

        TestShotResult total = sim.getTotalShotContribution(home, defense, 0.0, 0.0);
        assertEquals(1, total.attempts());
        assertEquals(1, total.events().size());
        assertEquals(0.70, total.events().getFirst().successPct(), 1e-9);
    }
}
