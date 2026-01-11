package service;

import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.service.simulator.DriveSimulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest

class DriveSimulatorTest {

    private static final long SEED = 123456789L;
    private Random random;
    private PlayerFactory playerFactory;

    @BeforeEach
    void setUp() {
        random = new Random(SEED);
        playerFactory = new PlayerFactory(random);
    }


    private static List<InGamePlayer> makePlan(PlayerFactory factory, String prefix, long startId) {
        List<InGamePlayer> players = new ArrayList<>();
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_PG"), 30, 30, 20));
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_SG"), 20, 30, 15));
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_SF"), 20, 25, 15));
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_PF"), 15, 20, 30));
        players.add(new InGamePlayer(factory.generatePlayer( prefix + "_C"), 10, 15, 30));

        return players;
    }


    @Test
    void simulateDrivesForMatchup_shouldProduceEventsAndPrint() {

        List<InGamePlayer> offense = makePlan(playerFactory, "HOME", 1);
        List<InGamePlayer> defense = makePlan(playerFactory, "AWAY", 100);

        InGamePlayer attacker = offense.getFirst();
        Player defender = defense.getFirst().getPlayer();

        DriveSimulator sim = new DriveSimulator(random);
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
        List<InGamePlayer> players = new ArrayList<>();
        players.add(new InGamePlayer(playerFactory.generatePlayer( "A"), 30, 30, 10));
        players.add(new InGamePlayer(playerFactory.generatePlayer( "B"), 20, 20, 10));
        players.add(new InGamePlayer(playerFactory.generatePlayer( "C"), 20, 20, 10));

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
        DriveSimulator sim = new DriveSimulator(random);
        Player off = playerFactory.generatePlayer( "OFF");
        Player def = playerFactory.generatePlayer( "DEF");
        double adv = sim.computeAdvantageDrive(off, def);
        System.out.println("[DRIVE advantage]=" + adv);
        assertTrue(adv >= -100 && adv <= 100);
    }

    @Test
    void computeDriveSuccessPct_shouldReactToAdvantageAndAssist() {
        DriveSimulator sim = new DriveSimulator(new Random(8));
        Player p = playerFactory.generatePlayer( "P");
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
        InGamePlayer attacker = new InGamePlayer(playerFactory.generatePlayer( "S"), 30, 30, 10);
        InGamePlayer p1 = new InGamePlayer(playerFactory.generatePlayer( "P1"), 20, 20, 10);
        InGamePlayer p2 = new InGamePlayer(playerFactory.generatePlayer( "P2"), 20, 20, 10);
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
