package com.sanguiwara.executor;

import com.sanguiwara.PostGameManager;
import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.calculator.GameSimulator;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.repository.GameRepository;
import com.sanguiwara.repository.PlayerProgressionRepository;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.result.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameExecutor {

    private final GameSimulator gameSimulator;
    private final GameRepository gameRepository;
    private final PostGameManager postGameManager;
    private final PlayerProgressionRepository playerProgressionRepository;
    private final PlayerRepository playerRepository;


    @Transactional
    public void executeGame(UUID gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        Game game = optionalGame.orElseThrow();
        game.getHomeGamePlan().recalculateInGamePlayerScores();
        game.getAwayGamePlan().recalculateInGamePlayerScores();
        game = gameRepository.save(game);
        log.debug("Recalculated and saved in-game player scores before execution for gameId={}", gameId);
        GameResult boxScore = gameSimulator.calculateGame(game.getHomeGamePlan(), game.getAwayGamePlan());
        game.setGameResult(boxScore);
        BoxScore homeStats = boxScore.homeScore();
        BoxScore awayStats = boxScore.awayScore();
        printGame(game, homeStats, awayStats);
        List<PlayerProgression> progressions = postGameManager.applyPostGameEffectsAndReturnsPlayersProgression(game);
        List<InGamePlayer> playersFromGame =
                Stream.concat(
                        game.getHomeGamePlan().getActivePlayers().stream(),
                        game.getAwayGamePlan().getActivePlayers().stream()
                ).toList();
        playersFromGame.forEach(player -> playerRepository.save(player.getPlayer()));
        gameRepository.save(game);
        playerProgressionRepository.saveAll(progressions);
    }



    private void printGame(Game game, BoxScore homeStats, BoxScore awayStats) {
        System.out.println("============================================================");
        System.out.println("                 MATCH ANALYSIS & BOXSCORE                  ");
        System.out.println("============================================================");

        printPlayerAdvantages("HOME TEAM", game.getHomeGamePlan());
        printTeamBoxScore("HOME STATS", homeStats);
        printPlayerStats("HOME TEAM INDIVIDUAL STATS", game.getHomeGamePlan().getActivePlayers());

        System.out.println();

        printPlayerAdvantages("AWAY TEAM", game.getAwayGamePlan());
        printTeamBoxScore("AWAY STATS", awayStats);
        printPlayerStats("AWAY TEAM INDIVIDUAL STATS", game.getAwayGamePlan().getActivePlayers());


        int homeTotal = calculateScoreForTeamTotalPoints(homeStats);
        int awayTotal = calculateScoreForTeamTotalPoints(awayStats);

        System.out.println("============================================================");
        System.out.printf("   FINAL SCORE: HOME %d - %d AWAY   %n", homeTotal, awayTotal);
        System.out.println("============================================================");
    }

    // Calcul des deux phases du match



private void printPlayerAdvantages(String teamLabel, GamePlan plan) {
    System.out.println("--- ADVANTAGES: " + teamLabel + " ---");
    System.out.printf("%-10s | %-12s | %-12s | %-12s%n", "Pos", "Drive Adv", "2pt Adv", "3pt Adv");
    System.out.println("------------------------------------------------------------");

    plan.getActivePlayers().forEach((player) -> System.out.printf("%-10s ",
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
