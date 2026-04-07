package com.sanguiwara.game;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.LeagueSeason;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.repository.GameRepository;
import com.sanguiwara.repository.GameTimeEventRepository;
import com.sanguiwara.repository.LeagueSeasonRepository;
import com.sanguiwara.repository.TeamRepository;
import com.sanguiwara.service.GamePlanService;
import com.sanguiwara.timeevent.EventManager;
import com.sanguiwara.timeevent.GameTimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameSchedulingService {

    private final TeamRepository teamRepository;
    private final LeagueSeasonRepository leagueSeasonRepository;
    private final GamePlanService gamePlanService;
    private final GameRepository gameRepository;
    private final GameExecutor gameExecutor;
    private final EventManager eventManager;
    private final GameTimeEventRepository gameTimeEventRepository;

    /**
     * Creates and schedules a single game between 2 existing teams.
     *
     * What it creates (same building blocks as {@code SeasonInitializer} round creation):
     * - 2 {@link GamePlan} (home + away), including {@code InGamePlayer} entries (via {@link GamePlanService})
     * - 1 {@link Game}
     * - 1 {@link GameTimeEvent} persisted and scheduled in {@link EventManager}
     */
    public Game scheduleGame(UUID homeTeamId, UUID awayTeamId, Instant executeAt, UUID leagueSeasonId) {
        Objects.requireNonNull(homeTeamId, "homeTeamId");
        Objects.requireNonNull(awayTeamId, "awayTeamId");
        Objects.requireNonNull(executeAt, "executeAt");
        Objects.requireNonNull(leagueSeasonId, "leagueSeasonId");

        if (homeTeamId.equals(awayTeamId)) {
            throw new IllegalArgumentException("homeTeamId and awayTeamId must be different");
        }

        Team homeTeam = teamRepository.findById(homeTeamId)
                .orElseThrow(() -> new NoSuchElementException("Home team not found: " + homeTeamId));
        Team awayTeam = teamRepository.findById(awayTeamId)
                .orElseThrow(() -> new NoSuchElementException("Away team not found: " + awayTeamId));

        LeagueSeason leagueSeason = leagueSeasonRepository.findAll().stream()
                .filter(ls -> leagueSeasonId.equals(ls.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("LeagueSeason not found: " + leagueSeasonId));

        GamePlan homeGamePlan = gamePlanService.generateGamePlan(homeTeam, awayTeam);
        GamePlan awayGamePlan = gamePlanService.generateGamePlan(awayTeam, homeTeam);

        Game game = new Game(null, homeGamePlan, awayGamePlan, leagueSeason, executeAt);
        game = gameRepository.save(game);

        GameTimeEvent gameTimeEvent = new GameTimeEvent(null, executeAt, game.getId(), gameExecutor);
        gameTimeEvent = gameTimeEventRepository.save(gameTimeEvent);
        eventManager.schedule(gameTimeEvent);

        return game;
    }
}
