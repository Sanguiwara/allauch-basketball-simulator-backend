package service;

import com.sanguiwara.baserecords.*;
import com.sanguiwara.calculator.*;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.defense.*;
import com.sanguiwara.factory.PlayerGenerator;
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
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class GameSimulatorTest {

    private static final long SEED = 1L;
    private static Random random;
    private static PlayerGenerator playerFactory;

    @BeforeAll
    static void setUp() {
        random = new Random();
        playerFactory = new PlayerGenerator(random);
    }

    private record GamePlans(GamePlan home, GamePlan away) {}

    private static GameSimulator getGameCalculator() {
        BadgeEngine badgeEngine = new BadgeEngine();
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );
        DefenseSchemeResolver defenseSchemeResolver = new DefenseSchemeResolver(schemes);
        AssistCalculator assistCalculator = new AssistCalculator(defenseSchemeResolver);
        ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator =
                new ShotSimulator<>( random, new ThreePointSpecification(random, badgeEngine), defenseSchemeResolver);
        ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator =
                new ShotSimulator<>( random, new TwoPointSpecification(random, badgeEngine), defenseSchemeResolver);
        ShotSimulator<DriveEvent, DriveResult> driveSimulator =
                new ShotSimulator<>( random, new DriveSpecification(random, badgeEngine), defenseSchemeResolver);
        ReboundCalculator reboundCalculator = new ReboundCalculator(random, badgeEngine);
        BlockCalculator blockCalculator = new BlockCalculator(badgeEngine);
        StealSimulator stealSimulator = new StealSimulator(random, badgeEngine);

        return new GameSimulator(threePointSimulator, twoPointSimulator, driveSimulator, assistCalculator, reboundCalculator, blockCalculator, stealSimulator);
    }

    private static GamePlans makePlans(PlayerGenerator factory) {
        Map<Position, InGamePlayer> homePos = createPositionMap(factory);
        Map<Position, InGamePlayer> awayPos = createPositionMap(factory);

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
                hP.setMinutesPlayed(40);
                aP.setMinutesPlayed(40);
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

        homePlan.setDefenseType(DefenseType.ZONE_2_1_2);
        awayPlan.setDefenseType(DefenseType.ZONE_2_1_2);
        return new GamePlans(homePlan, awayPlan);
    }

    private static Map<Position, InGamePlayer> createPositionMap(PlayerGenerator factory) {
        Map<Position, InGamePlayer> pos = new EnumMap<>(Position.class);
        pos.put(Position.PG, new InGamePlayer(factory.generatePlayer( ),null));
        pos.put(Position.SG, new InGamePlayer(factory.generatePlayer( ),null));
        pos.put(Position.SF, new InGamePlayer(factory.generatePlayer(),null));
        pos.put(Position.PF, new InGamePlayer(factory.generatePlayer( ),null));
        pos.put(Position.C, new InGamePlayer(factory.generatePlayer( ),null));
        return pos;
    }

    @Test
    void calculateGame_shouldDistributeShotsWith10Players_seeded_usageAndMinutes() {
        // Requirement: run 50 matches and evolve the seed from 1 to 50 (inclusive).
        // Also silence noisy simulation logs: only final score + winner should be printed.
        org.slf4j.Logger rootSlf4j = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        Level previousRootLevel = null;
        boolean changedRootLevel = false;
        if (rootSlf4j instanceof Logger rootLogback) {
            previousRootLevel = rootLogback.getLevel();
            rootLogback.setLevel(Level.ERROR);
            changedRootLevel = true;
        }

        try {
            for (long seed = 1; seed <= 50; seed++) {
                Random matchRandom = new Random(seed);
                GameSimulator simulator = createGameCalculator(matchRandom);

            // HOME: 10 joueurs, dont 2 "80 partout" avec gros usage + minutes,
            //       et 8 "50 partout" avec faible usage + faible minutes.
            List<InGamePlayer> homePlayers = new ArrayList<>();
            // Minutes sum to 200: 2*36 + 8*16 = 200.
            for (int i = 1; i <= 2; i++) {
                homePlayers.add(makePlayer("HOME_80_" + i, 80, 36, 27));
            }
            for (int i = 1; i <= 8; i++) {
                homePlayers.add(makePlayer("HOME_50_" + i, 50, 16, 6));
            }

            // AWAY: 10 joueurs, 2 "80 partout" et 8 "50 partout",
            //       mais tous le meme usage et tous 20 minutes.
            List<InGamePlayer> awayPlayers = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                awayPlayers.add(makePlayer("AWAY_80_" + i, 80, 20, 10));
            }
            for (int i = 1; i <= 8; i++) {
                awayPlayers.add(makePlayer("AWAY_50_" + i, 50, 20, 10));
            }

            GamePlan homePlan = new GamePlan(UUID.fromString("00000000-0000-0000-0000-000000000001"), null, null);
            GamePlan awayPlan = new GamePlan(UUID.fromString("00000000-0000-0000-0000-000000000002"), null, null);

            homePlan.setActivePlayers(homePlayers);
            awayPlan.setActivePlayers(awayPlayers);

            // Keep it simple/deterministic: same attempt shares, same defense types, fixed possessions.
            homePlan.setTotalShotNumber(90);
            awayPlan.setTotalShotNumber(90);

            homePlan.setDriveAttemptShare(1.0 / 3.0);
            homePlan.setMidRangeAttemptShare(1.0 / 3.0);
            homePlan.setThreePointAttemptShare(1.0 / 3.0);

            awayPlan.setDriveAttemptShare(1.0 / 3.0);
            awayPlan.setMidRangeAttemptShare(1.0 / 3.0);
            awayPlan.setThreePointAttemptShare(1.0 / 3.0);

            homePlan.setDefenseType(DefenseType.ZONE_2_3);
            awayPlan.setDefenseType(DefenseType.ZONE_2_3);

            GameResult result = simulator.calculateGame(homePlan, awayPlan);
            assertNotNull(result);

            assertEquals(10, homePlan.getActivePlayers().size());
            assertEquals(10, awayPlan.getActivePlayers().size());

            assertEquals(2, homePlan.getActivePlayers().stream().filter(p -> p.getPlayer().getTir3Pts() == 80).count());
            assertEquals(8, homePlan.getActivePlayers().stream().filter(p -> p.getPlayer().getTir3Pts() == 50).count());
            assertEquals(2, awayPlan.getActivePlayers().stream().filter(p -> p.getPlayer().getTir3Pts() == 80).count());
            assertEquals(8, awayPlan.getActivePlayers().stream().filter(p -> p.getPlayer().getTir3Pts() == 50).count());

            assertTrue(awayPlan.getActivePlayers().stream().allMatch(p -> p.getMinutesPlayed() == 20));
            assertTrue(awayPlan.getActivePlayers().stream().allMatch(p -> p.getUsageShoot() == 10));
            assertTrue(awayPlan.getActivePlayers().stream().allMatch(p -> p.getUsageDrive() == 10));
            assertTrue(awayPlan.getActivePlayers().stream().allMatch(p -> p.getUsagePost() == 10));

            int homeTotal = calculateScoreForTeamTotalPoints(result.homeScore());
            int awayTotal = calculateScoreForTeamTotalPoints(result.awayScore());
            String winner = homeTotal > awayTotal ? "HOME" : (awayTotal > homeTotal ? "AWAY" : "TIE");

            // Only print final score + winner per match.
            System.out.printf("seed=%d FINAL SCORE: HOME %d - %d AWAY | winner=%s%n", seed, homeTotal, awayTotal, winner);
            }
        } finally {
            if (changedRootLevel && rootSlf4j instanceof Logger rootLogback) {
                rootLogback.setLevel(previousRootLevel); // may be null (inherit)
            }
        }
    }

    private static GameSimulator createGameCalculator(Random random) {
        BadgeEngine badgeEngine = new BadgeEngine();
        List<DefensiveScheme> schemes = List.of(
                new RegularMan2ManScheme(badgeEngine),
                new Zone23Scheme(badgeEngine),
                new Zone212Scheme(badgeEngine),
                new Zone32Scheme(badgeEngine)
        );
        DefenseSchemeResolver defenseSchemeResolver = new DefenseSchemeResolver(schemes);
        AssistCalculator assistCalculator = new AssistCalculator(defenseSchemeResolver);
        ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator =
                new ShotSimulator<>(random, new ThreePointSpecification(random, badgeEngine), defenseSchemeResolver);
        ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator =
                new ShotSimulator<>(random, new TwoPointSpecification(random, badgeEngine), defenseSchemeResolver);
        ShotSimulator<DriveEvent, DriveResult> driveSimulator =
                new ShotSimulator<>(random, new DriveSpecification(random, badgeEngine), defenseSchemeResolver);
        ReboundCalculator reboundCalculator = new ReboundCalculator(random, badgeEngine);
        BlockCalculator blockCalculator = new BlockCalculator(badgeEngine);
        StealSimulator stealSimulator = new StealSimulator(random, badgeEngine);

        return new GameSimulator(threePointSimulator, twoPointSimulator, driveSimulator, assistCalculator, reboundCalculator, blockCalculator, stealSimulator);
    }

    private static InGamePlayer makePlayer(String name, int ratingEverywhere, int minutes, int usageEverywhere) {
        UUID id = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        Player player = Player.builder()
                .teamsID(new HashSet<>())
                .clubID(null)
                .badgeIds(new HashSet<>())
                .id(id)
                .name(name)
                .birthDate(0)
                .injured(false)
                // Shooting / finishing
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
                // Defense / rebound
                .defExterieur(ratingEverywhere)
                .defPoste(ratingEverywhere)
                .protectionCercle(ratingEverywhere)
                .timingRebond(ratingEverywhere)
                .agressiviteRebond(ratingEverywhere)
                .steal(ratingEverywhere)
                .timingBlock(ratingEverywhere)
                // Physical / mental
                .physique(ratingEverywhere)
                .basketballIqOff(ratingEverywhere)
                .basketballIqDef(ratingEverywhere)
                .passingSkills(ratingEverywhere)
                .iq(ratingEverywhere)
                .endurance(ratingEverywhere)
                .solidite(ratingEverywhere)
                // Potential
                .potentielSkill(ratingEverywhere)
                .potentielPhysique(ratingEverywhere)
                // Attitude
                .coachability(ratingEverywhere)
                .ego(ratingEverywhere)
                .softSkills(ratingEverywhere)
                .leadership(ratingEverywhere)
                .morale(ratingEverywhere)
                .build();

        InGamePlayer inGamePlayer = new InGamePlayer(player, null);
        inGamePlayer.setMinutesPlayed(minutes);
        inGamePlayer.setUsageShoot(usageEverywhere);
        inGamePlayer.setUsageDrive(usageEverywhere);
        inGamePlayer.setUsagePost(usageEverywhere);
        return inGamePlayer;
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
                    player.getName(),
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
