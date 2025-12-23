package service;

import ingame.GamePlan;
import ingame.InGamePlayer;
import baserecords.Player;
import event.DriveEvent;
import result.DriveResult;
import simulator.DriveSimulator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DriveSimulatorTest {

    private static GamePlan makePlan(PlayerRandomFactory factory, String prefix, long startId) {
        List<InGamePlayer> players = new ArrayList<>();
        players.add(new InGamePlayer(factory.random(startId + 0, prefix + "_PG"), 30, 30, 20));
        players.add(new InGamePlayer(factory.random(startId + 1, prefix + "_SG"), 20, 30, 15));
        players.add(new InGamePlayer(factory.random(startId + 2, prefix + "_SF"), 20, 25, 15));
        players.add(new InGamePlayer(factory.random(startId + 3, prefix + "_PF"), 15, 20, 30));
        players.add(new InGamePlayer(factory.random(startId + 4, prefix + "_C"), 10, 15, 30));
        return new GamePlan(null, null, players, null);
    }

    @Test
    void simulateDrivesForMatchup_shouldProduceEventsAndPrint() {
        long seed = 20241221L;
        PlayerRandomFactory factory = new PlayerRandomFactory(seed);
        GamePlan offense = makePlan(factory, "HOME", 1);
        GamePlan defense = makePlan(factory, "AWAY", 100);

        InGamePlayer attacker = offense.getActivePlayers().get(0);
        InGamePlayer defender = defense.getActivePlayers().get(0);

        DriveSimulator sim = new DriveSimulator(new Random(seed));
        DriveResult res = sim.simulateDrivesForMatchup(
                offense,
                attacker,
                defender,
                0.30 // assistedDrivePercentage

        );

        assertNotNull(res);
        assertTrue(res.attempts() >= 0);
        assertTrue(res.made() >= 0 && res.made() <= res.attempts());
        assertEquals(res.attempts(), res.events().size());

        double pct = res.attempts() == 0 ? 0.0 : (100.0 * res.made() / res.attempts());
        long assisted = res.events().stream().filter(DriveEvent::assisted).count();
        System.out.println("[DRIVE MATCHUP] attempts=" + res.attempts() + ", made=" + res.made() + ", pct=" + String.format("%.1f", pct) + ", assisted=" + assisted);
        for (DriveEvent e : res.events()) {
            System.out.println(e);
        }
    }

    @Test
    void simulateDriveEvents_shouldHonorAssist() {
        long seed = 424242L;
        PlayerRandomFactory f = new PlayerRandomFactory(seed);
        List<InGamePlayer> players = new ArrayList<>();
        players.add(new InGamePlayer(f.random(1, "A"), 30, 30, 10));
        players.add(new InGamePlayer(f.random(2, "B"), 20, 20, 10));
        players.add(new InGamePlayer(f.random(3, "C"), 20, 20, 10));

        InGamePlayer attacker = players.get(0);
        players.get(1).setAssistWeight(8.0);
        players.get(2).setAssistWeight(2.0);

        DriveSimulator sim = new DriveSimulator(new Random(seed));
        DriveResult res = sim.simulateDriveEvents(
                attacker,
                players,
                12,
                0.75,
                0.05,
                5.0
        );

        assertEquals(12, res.attempts());
        assertTrue(res.made() >= 0 && res.made() <= 12);
        long assisted = res.events().stream().filter(DriveEvent::assisted).count();
        System.out.println("[DRIVE simulateDriveEvents] attempts=12, made=" + res.made() + ", assisted=" + assisted);
        assertTrue(assisted > 0);
    }

    @Test
    void computeAdvantageDrive_shouldReturnReasonableRange() {
        DriveSimulator sim = new DriveSimulator(new Random(7));
        PlayerRandomFactory f = new PlayerRandomFactory(7L);
        Player off = f.random(1, "OFF");
        Player def = f.random(2, "DEF");
        double adv = sim.computeAdvantageDrive(off, def);
        System.out.println("[DRIVE advantage]=" + adv);
        assertTrue(adv >= -100 && adv <= 100);
    }

    @Test
    void computeDriveSuccessPct_shouldReactToAdvantageAndAssist() {
        DriveSimulator sim = new DriveSimulator(new Random(8));
        Player p = new PlayerRandomFactory(8L).random(1, "P");
        double base = sim.computeDriveSuccessPct(p, 0.0, 0.0);
        double withAssist = sim.computeDriveSuccessPct(p, 0.0, 0.05);
        double withAdv = sim.computeDriveSuccessPct(p, 25.0, 0.0);
        System.out.println("[DRIVE pct] base=" + base + ", assist=" + withAssist + ", adv25=" + withAdv);
        assertTrue(withAssist >= base);
        assertTrue(withAdv >= base);
        assertTrue(base >= 0.05 && base <= 0.95);
    }

    @Test
    void sampleDriveAttempts_shouldBeClampedAndResponsive() {
        DriveSimulator sim = new DriveSimulator(new Random(9));
        int low = sim.sampleDriveAttempts(10, 10, 0, 30);
        int high = sim.sampleDriveAttempts(30, 100, 0, 30);
        System.out.println("[DRIVE attempts] low=" + low + ", high=" + high);
        assertTrue(low >= 0 && low <= 30);
        assertTrue(high >= 0 && high <= 30);
    }

    @Test
    void privateMethods_pickAssisterWeighted_and_clamp_viaReflection() throws Exception {
        long seed = 10L;
        DriveSimulator sim = new DriveSimulator(new Random(seed));

        // clamp(double)
        Method clampD = DriveSimulator.class.getDeclaredMethod("clamp", double.class, double.class, double.class);
        clampD.setAccessible(true);
        double clampedD = (double) clampD.invoke(null, -1.0, 0.0, 1.0);
        System.out.println("[DRIVE clampD] -> " + clampedD);
        assertEquals(0.0, clampedD, 1e-9);

        // clamp(int)
        Method clampI = DriveSimulator.class.getDeclaredMethod("clamp", int.class, int.class, int.class);
        clampI.setAccessible(true);
        int clampedI = (int) clampI.invoke(null, 50, 0, 30);
        System.out.println("[DRIVE clampI] -> " + clampedI);
        assertEquals(30, clampedI);

        // pickAssisterWeighted
        PlayerRandomFactory f = new PlayerRandomFactory(seed);
        InGamePlayer attacker = new InGamePlayer(f.random(1, "S"), 30, 30, 10);
        InGamePlayer p1 = new InGamePlayer(f.random(2, "P1"), 20, 20, 10);
        InGamePlayer p2 = new InGamePlayer(f.random(3, "P2"), 20, 20, 10);
        p1.setAssistWeight(10.0);
        p2.setAssistWeight(1.0);
        List<InGamePlayer> passers = List.of(attacker, p1, p2);

        Method pick = DriveSimulator.class.getDeclaredMethod("pickAssisterWeighted", List.class, InGamePlayer.class);
        pick.setAccessible(true);
        InGamePlayer chosen = (InGamePlayer) pick.invoke(sim, passers, attacker);
        System.out.println("[DRIVE pickAssisterWeighted] chosen=" + (chosen == null ? "null" : chosen.getPlayer().name()));
        assertNotNull(chosen);
        assertNotEquals(attacker, chosen);
    }
}
