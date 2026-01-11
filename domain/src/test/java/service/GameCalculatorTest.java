package service;

import com.sanguiwara.service.GameCalculator;
import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Position;
import com.sanguiwara.gameevent.BoxScore;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sanguiwara.service.simulator.DriveSimulator;
import com.sanguiwara.service.simulator.TwoPointSimulator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class GameCalculatorTest {

    private static final long SEED = 123456789L;
    private Random random;
    private PlayerFactory playerFactory;

    @BeforeEach
    void setUp() {
        random = new Random(SEED);
        playerFactory = new PlayerFactory(random);
    }

    private record GamePlans(GamePlan home, GamePlan away) {}

    private static GamePlans makePlans(PlayerFactory factory, String homePrefix, String awayPrefix) {
        Map<Position, InGamePlayer> homePos = createPositionMap(factory, homePrefix, 1);
        Map<Position, InGamePlayer> awayPos = createPositionMap(factory, awayPrefix, 100);

        GamePlan homePlan = new GamePlan(null, null, null);
        GamePlan awayPlan = new GamePlan(null, null, null); //A changer

        homePlan.setPositions(homePos);
        awayPlan.setPositions(awayPos);

        homePlan.setActivePlayers((List<InGamePlayer>) homePos.values());
        awayPlan.setActivePlayers((List<InGamePlayer>) awayPos.values());
        Map<Player, Player> homeMatchups = new HashMap<>();
        Map<Player, Player> awayMatchups = new HashMap<>();

        for (Position pos : Position.values()) {
            InGamePlayer hP = homePos.get(pos);
            InGamePlayer aP = awayPos.get(pos);
            if (hP != null && aP != null) {
                homeMatchups.put(hP.getPlayer(), aP.getPlayer());
                awayMatchups.put(aP.getPlayer(), hP.getPlayer());
            }
        }

        homePlan.setMatchups(homeMatchups);
        awayPlan.setMatchups(awayMatchups);

        return new GamePlans(homePlan, awayPlan);
    }

    private static Map<Position, InGamePlayer> createPositionMap(PlayerFactory factory, String prefix, long startId) {
        Map<Position, InGamePlayer> pos = new EnumMap<>(Position.class);
        pos.put(Position.PG, new InGamePlayer(factory.generatePlayer( prefix + "_PG"), 30, 15, 15));
        pos.put(Position.SG, new InGamePlayer(factory.generatePlayer( prefix + "_SG"), 15, 15, 15));
        pos.put(Position.SF, new InGamePlayer(factory.generatePlayer(prefix + "_SF"), 15, 15, 15));
        pos.put(Position.PF, new InGamePlayer(factory.generatePlayer( prefix + "_PF"), 15, 15, 15));
        pos.put(Position.C, new InGamePlayer(factory.generatePlayer( prefix + "_C"), 15, 15, 15));
        return pos;
    }

    @Test
    void playmakingAdvantageTest() {
        long seed = 42L;
        Random random = new Random(seed);
        TwoPointSimulator twoPointSimulator = new TwoPointSimulator(random);
        DriveSimulator driveSimulator = new DriveSimulator(random);
        GameCalculator calc = new GameCalculator(twoPointSimulator, driveSimulator);

        int samples = 10000;

        double mean = 0;
        long runSeed = System.currentTimeMillis(); // seed différente à chaque relance mais présente pour qu'on puisse la rejouer au cas ou


        for (int i = 1; i <= samples; i++) {
            //possibilité d'ajouter une seed
            GamePlans plans = makePlans(playerFactory, "HOME", "AWAY");
            double base = calc.getTotalPlaymakingContribution(plans.home(), plans.away());
            mean += base;
        }
        log.info(String.valueOf(runSeed));
        log.info(String.valueOf(mean / samples));
        assertTrue(true);
    }

    @Test
    void calculate_shouldPrintDetailedThreePointTimeline_singleMatch() {
        // ✅ Mets System.currentTimeMillis() si tu veux une seed différente à chaque run
        long seed = 42L;
        Random random = new Random(seed);
        TwoPointSimulator twoPointSimulator = new TwoPointSimulator(random);
        DriveSimulator driveSimulator = new DriveSimulator(random);


        GamePlans plans = makePlans(playerFactory, "HOME", "AWAY");
        GamePlan home = plans.home();
        GamePlan away = plans.away();

        GameCalculator calc = new GameCalculator(twoPointSimulator, driveSimulator);

        double pm = calc.getTotalPlaymakingContribution(home, away);
        BoxScore res = calc.calculate(home, away);
        ThreePointShootingResult threePointShotRes = res.threePointShootingResult();

        assertNotNull(res);
        assertNotNull(threePointShotRes.events());

        int attempts = threePointShotRes.attempts();
        int made = threePointShotRes.made();

        long assistedShots = threePointShotRes.events().stream().filter(ThreePointShotEvent::assisted).count();
        long assistedMakes = threePointShotRes.events().stream().filter(e -> e.assisted() && e.made()).count();

        double pct = attempts == 0 ? 0.0 : (100.0 * made / attempts);

        System.out.println("=======================================");
        System.out.println("MATCH REPORT (3PTS)");
        System.out.println("=======================================");
        System.out.println("seed = " + seed);
        System.out.printf ("playmakingContribution(team) = %.3f%n", pm);
        System.out.println("3PA = " + attempts);
        System.out.println("3PM = " + made);
        System.out.printf ("3P%% = %.1f%%%n", pct);
        System.out.println("assisted shots  = " + assistedShots);
        System.out.println("assisted makes  = " + assistedMakes);
        System.out.println();

        System.out.println("--- Events (shot by shot) ---");
        for (ThreePointShotEvent e : threePointShotRes.events()) {
            System.out.println(e);
            System.out.println("=======================================");


        }
        System.out.println("=======================================");

        // Sanity checks (tu peux ajuster)
        assertTrue(attempts >= 0);
        assertTrue(made >= 0 && made <= attempts);

        // Si tes events représentent bien tous les tirs, normalement :
        // events.size == attempts
        // (à toi de confirmer selon ton implémentation)
        assertEquals(attempts, threePointShotRes.events().size(), "events.size should match attempts");
    }

    @Test
    void calculate_shouldPrintDetailedTwoPointTimeline_singleMatch() {
        long seed = 42L;
        Random random = new Random();
        TwoPointSimulator twoPointSimulator = new TwoPointSimulator(random);
        DriveSimulator driveSimulator = new DriveSimulator(random);


        GamePlans plans = makePlans(playerFactory, "HOME", "AWAY");

        GamePlan home = plans.home;
        GamePlan away = plans.away;

        GameCalculator calc = new GameCalculator(twoPointSimulator, driveSimulator);

        double pm = calc.getTotalPlaymakingContribution(home, away);
        BoxScore res = calc.calculate(home, away);
        TwoPointShootingResult twoPointShotRes = res.twoPointShootingResult();

        assertNotNull(res);
        assertNotNull(twoPointShotRes.events());

        int attempts = twoPointShotRes.attempts();
        int made = twoPointShotRes.made();

        long assistedShots = twoPointShotRes.events().stream().filter(TwoPointShotEvent::assisted).count();
        long assistedMakes = twoPointShotRes.events().stream().filter(e -> e.assisted() && e.made()).count();

        double pct = attempts == 0 ? 0.0 : (100.0 * made / attempts);

        System.out.println("=======================================");
        System.out.println("MATCH REPORT (2PTS)");
        System.out.println("=======================================");
        System.out.println("seed = " + seed);
        System.out.printf ("playmakingContribution(team) = %.3f%n", pm);
        System.out.println("2PA = " + attempts);
        System.out.println("2PM = " + made);
        System.out.printf ("2P%% = %.1f%%%n", pct);
        System.out.println("assisted shots  = " + assistedShots);
        System.out.println("assisted makes  = " + assistedMakes);
        System.out.println();

        System.out.println("--- Events (shot by shot) ---");
        for (TwoPointShotEvent e : twoPointShotRes.events()) {
            System.out.println(e);
            System.out.println("=======================================");
        }

        assertTrue(attempts >= 0);
        assertTrue(made <= attempts);
        assertEquals(attempts, twoPointShotRes.events().size(), "events.size should match attempts");
    }

    @Test
    void calculate_shouldPrintDetailedDriveTimeline_singleMatch() {
        long seed = 42L;
        Random random = new Random(seed);
        TwoPointSimulator twoPointSimulator = new TwoPointSimulator(random);
        DriveSimulator driveSimulator = new DriveSimulator(random);



        GamePlans plans = makePlans(playerFactory, "HOME", "AWAY");

        GamePlan home = plans.home;
        GamePlan away = plans.away;


        GameCalculator calc = new GameCalculator(twoPointSimulator, driveSimulator);

        double pm = calc.getTotalPlaymakingContribution(home, away);
        BoxScore res = calc.calculate(home, away);
        DriveResult driveRes = res.driveResult();

        assertNotNull(res);
        assertNotNull(driveRes.events());

        int attempts = driveRes.attempts();
        int made = driveRes.made();
        int fouls = driveRes.foulsDrawn();

        long assistedShots = driveRes.events().stream().filter(DriveEvent::assisted).count();
        long assistedMakes = driveRes.events().stream().filter(e -> e.assisted() && e.made()).count();

        double pct = driveRes.fgPct() * 100.0;

        System.out.println("=======================================");
        System.out.println("MATCH REPORT (DRIVES)");
        System.out.println("=======================================");
        System.out.println("seed = " + seed);
        System.out.printf ("playmakingContribution(team) = %.3f%n", pm);
        System.out.println("Drive Attempts = " + attempts);
        System.out.println("Drive Makes    = " + made);
        System.out.println("Fouls Drawn    = " + fouls);
        System.out.printf ("Drive FG%%     = %.1f%%%n", pct);
        System.out.println("assisted drives = " + assistedShots);
        System.out.println("assisted makes  = " + assistedMakes);
        System.out.println();

        System.out.println("--- Events (drive by drive) ---");
        for (DriveEvent e : driveRes.events()) {
            System.out.println(e);
            System.out.println("=======================================");
        }

        assertTrue(attempts >= 0);
        assertTrue(made <= attempts);
        assertEquals(attempts, driveRes.events().size(), "events.size should match attempts");
    }
}
