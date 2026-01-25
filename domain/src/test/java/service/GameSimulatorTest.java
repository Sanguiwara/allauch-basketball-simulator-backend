package service;

import com.sanguiwara.calculator.*;
import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Position;
import com.sanguiwara.result.*;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.calculator.spec.ThreePointSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.sanguiwara.calculator.spec.DriveSpecification;
import com.sanguiwara.calculator.spec.TwoPointSpecification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class GameSimulatorTest {

    private static final long SEED = 123456789L;
    private static Random random;
    private static PlayerFactory playerFactory;

    @BeforeAll
    static void setUp() {
        random = new Random();
        playerFactory = new PlayerFactory(random);
    }

    private record GamePlans(GamePlan home, GamePlan away) {}

    private static GameSimulator getGameCalculator() {
        PlaymakingCalculator playmakingCalculator = new PlaymakingCalculator();
        ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator =
                new ShotSimulator<>( random, new ThreePointSpecification(random));
        ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator =
                new ShotSimulator<>( random, new TwoPointSpecification(random));
        ShotSimulator<DriveEvent, DriveResult> driveSimulator =
                new ShotSimulator<>( random, new DriveSpecification(random));
        ReboundCalculator reboundCalculator = new ReboundCalculator(random);
        BlockCalculator blockCalculator = new BlockCalculator();
        StealSimulator stealSimulator = new StealSimulator(random);

        return new GameSimulator(threePointSimulator, twoPointSimulator, driveSimulator, playmakingCalculator, reboundCalculator, blockCalculator, stealSimulator);
    }

    private static GamePlans makePlans(PlayerFactory factory) {
        Map<Position, InGamePlayer> homePos = createPositionMap(factory, "HOME");
        Map<Position, InGamePlayer> awayPos = createPositionMap(factory, "AWAY");

        GamePlan homePlan = new GamePlan(null, null, null);
        GamePlan awayPlan = new GamePlan(null, null, null); //A changer

        homePlan.setPositions(homePos);
        awayPlan.setPositions(awayPos);

        homePlan.setActivePlayers(new ArrayList<>(homePos.values()));
        awayPlan.setActivePlayers(new ArrayList<>(awayPos.values()));
        Map<Player, Player> homeMatchups = new HashMap<>();
        Map<Player, Player> awayMatchups = new HashMap<>();

        for (Position pos : Position.values()) {
            InGamePlayer hP = homePos.get(pos);
            InGamePlayer aP = awayPos.get(pos);
            if (hP != null && aP != null) {
                homeMatchups.put(aP.getPlayer(), hP.getPlayer());
                awayMatchups.put(hP.getPlayer(), aP.getPlayer());
            }
        }

        homePlan.setMatchups(homeMatchups);
        awayPlan.setMatchups(awayMatchups);

        homePlan.setTotalShotNumber(70);
        awayPlan.setTotalShotNumber(70);

        homePlan.setDriveAttemptShare(0.33);
        awayPlan.setDriveAttemptShare(0.33);

        homePlan.setMidRangeAttemptShare(0.33);
        awayPlan.setMidRangeAttemptShare(0.33);

        homePlan.setThreePointAttemptShare(0.33);
        awayPlan.setThreePointAttemptShare(0.33);

//TODO A changer pour utiliser la GamePlanFactory, pour les matchups, a voir
        return new GamePlans(homePlan, awayPlan);
    }

    private static Map<Position, InGamePlayer> createPositionMap(PlayerFactory factory, String prefix) {
        Map<Position, InGamePlayer> pos = new EnumMap<>(Position.class);
        pos.put(Position.PG, new InGamePlayer(factory.generatePlayer( prefix + "_PG"), 30, 15, 15));
        pos.put(Position.SG, new InGamePlayer(factory.generatePlayer( prefix + "_SG"), 15, 15, 15));
        pos.put(Position.SF, new InGamePlayer(factory.generatePlayer(prefix + "_SF"), 15, 15, 15));
        pos.put(Position.PF, new InGamePlayer(factory.generatePlayer( prefix + "_PF"), 15, 15, 15));
        pos.put(Position.C, new InGamePlayer(factory.generatePlayer( prefix + "_C"), 15, 15, 15));
        return pos;
    }





    @Test
    void calculate_ScoreForTeam_shouldPrintDetailedThreePointTimeline_singleMatch() {

        GameSimulator calc = getGameCalculator();

        GamePlans plans = makePlans(playerFactory);
        GamePlan home = plans.home();
        GamePlan away = plans.away();


        BoxScore res = calc.calculateScoreForTeam(home, away, 0.2);
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
        System.out.println("seed = " + SEED);
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
    void calculate_ScoreForTeam_shouldPrintDetailedTwoPointTimeline_singleMatch() {

        GameSimulator calc = getGameCalculator();
        GamePlans plans = makePlans(playerFactory);

        GamePlan home = plans.home();
        GamePlan away = plans.away();


        BoxScore res = calc.calculateScoreForTeam(home, away, 0.2);
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
        System.out.println("seed = " + SEED);
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
    void calculate_ScoreForTeam_shouldPrintDetailedDriveTimeline_singleMatch() {
        GameSimulator calc = getGameCalculator();
        GamePlans plans = makePlans(playerFactory);

        GamePlan home = plans.home();
        GamePlan away = plans.away();



        BoxScore res = calc.calculateScoreForTeam(home, away, 0.2);
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
        System.out.println("seed = " + SEED);
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


    @Test
    void calculate_ScoreForTeam_shouldPrintFullMatchBoxScore_WithPlayerAdvantages() {
        GameSimulator calc = getGameCalculator();


        GamePlans plans = makePlans(playerFactory);

        GameResult gameResult = calc.calculateGame(plans.home,plans.away);

        // Calcul des deux phases du match
        BoxScore homeStats = gameResult.homeScore();
        BoxScore awayStats = gameResult.awayScore();

        System.out.println("============================================================");
        System.out.println("                 MATCH ANALYSIS & BOXSCORE                  ");
        System.out.println("============================================================");

        printPlayerAdvantages("HOME TEAM", plans.home());
        printTeamBoxScore("HOME STATS", homeStats);
        printPlayerStats("HOME TEAM INDIVIDUAL STATS", plans.home().getActivePlayers());

        System.out.println();

        printPlayerAdvantages("AWAY TEAM", plans.away());
        printTeamBoxScore("AWAY STATS", awayStats);
        printPlayerStats("AWAY TEAM INDIVIDUAL STATS", plans.away().getActivePlayers());


        int homeTotal = calculateScoreForTeamTotalPoints(homeStats);
        int awayTotal = calculateScoreForTeamTotalPoints(awayStats);

        System.out.println("============================================================");
        System.out.printf("   FINAL SCORE: HOME %d - %d AWAY   %n", homeTotal, awayTotal);
        System.out.println("============================================================");
    }

    private void printPlayerAdvantages(String teamLabel, GamePlan plan) {
        System.out.println("--- ADVANTAGES: " + teamLabel + " ---");
        System.out.printf("%-10s | %-12s | %-12s | %-12s%n", "Pos", "Drive Adv", "2pt Adv", "3pt Adv");
        System.out.println("------------------------------------------------------------");

        plan.getActivePlayers().forEach(( player) -> System.out.printf("%-10s ",
                player.getPlaymakingContribution()));
        System.out.println();
    }

    private void printPlayerStats(String label, List<InGamePlayer> players) {
        System.out.println("[" + label + "]");
        System.out.printf("%-20s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-5s | %-6s | %-6s | %-6s%n",
                "Player", "Pts", "Ast", "Blk", "Stl", "OR", "DR", "FGM", "FGA", "FG%", "3PM", "3PA");
        System.out.println("-----------------------------------------------------------------------------------------------------");

        for (InGamePlayer p : players) {
            Player player = p.getPlayer();
            int fgm = p.getFgm();
            int fga = p.getFga();
            double fgPct = fga == 0 ? 0.0 : (100.0 * fgm / fga);

            int threePointMade = p.getThreePointMade();
            int threePointAttempt = p.getThreePointAttempt();

            int twoPointMade = p.getTwoPointMade();
            int twoPointAttempts = p.getTwoPointAttempts();
            double twoPct = twoPointAttempts == 0 ? 0.0 : (100.0 * twoPointMade / twoPointAttempts);

            System.out.printf("%-20s | %-5d | %-5d | %-5d | %-5d | %-5d | %-5d | %-5d | %-5d | %5.1f%% | %-5d | %-5d%n",
                    player.name(),
                    p.getPoints(),
                    p.getAssists(),
                    p.getBlocks(),
                    p.getSteals(),
                    p.getOffensiveRebounds(),
                    p.getDefensiveRebounds(),
                    fgm,
                    fga,
                    fgPct,
                    threePointMade,
                    threePointAttempt);

            // Ligne supplémentaire avec détails 2PT et Drive
            System.out.printf("  → 2PT: %d/%d (%.1f%%) | Drive: %d/%d (%.1f%%)%n",
                    twoPointMade,
                    twoPointAttempts,
                    twoPct,
                    p.getDriveMade(),
                    p.getDriveAttempts(),
                    p.getDriveAttempts() == 0 ? 0.0 : (100.0 * p.getDriveMade() / p.getDriveAttempts()));
        }
        System.out.println();
    }

    private void printTeamBoxScore(String label, BoxScore stats) {
        DriveResult d = stats.driveResult();
        TwoPointShootingResult tp = stats.twoPointShootingResult();
        ThreePointShootingResult tps = stats.threePointShootingResult();

        System.out.println("[" + label + "]");
        System.out.printf("  DRIVES:  %d/%d (%.1f%%) | Fouls: %d%n",
                d.made(), d.attempts(), d.fgPct() * 100, d.foulsDrawn());

        double tpPct = tp.attempts() == 0 ? 0 : (100.0 * tp.made() / tp.attempts());
        System.out.printf("  2PTS:    %d/%d (%.1f%%) | Assisted: %d%n",
                tp.made(), tp.attempts(), tpPct, tp.events().stream().filter(TwoPointShotEvent::assisted).count());

        double tpsPct = tps.attempts() == 0 ? 0 : (100.0 * tps.made() / tps.attempts());
        System.out.printf("  3PTS:    %d/%d (%.1f%%) | Assisted: %d%n",
                tps.made(), tps.attempts(), tpsPct, tps.events().stream().filter(ThreePointShotEvent::assisted).count());
    }

    private int calculateScoreForTeamTotalPoints(BoxScore stats) {
        return (stats.driveResult().made() * 2)
                + (stats.twoPointShootingResult().made() * 2)
                + (stats.threePointShootingResult().made() * 3);
    }

}
