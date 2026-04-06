package com.sanguiwara.initializer;

import com.sanguiwara.baserecords.*;
import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.executor.TrainingExecutor;
import com.sanguiwara.factory.PlayerGenerator;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.repository.*;
import com.sanguiwara.service.GamePlanService;
import com.sanguiwara.service.PlayerService;
import com.sanguiwara.timeevent.EventManager;
import com.sanguiwara.timeevent.GameTimeEvent;
import com.sanguiwara.timeevent.TimeEvent;
import com.sanguiwara.timeevent.TrainingTimeEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SeasonInitializerTest {

    @Test
    void createGamesForSeason_schedulesTrainingsAndGamesEvery10Minutes() {
        PlayerGenerator playerGenerator = mock(PlayerGenerator.class);
        TeamFactory teamFactory = mock(TeamFactory.class);
        GamePlanService gamePlanService = mock(GamePlanService.class);
        TeamRepository teamRepository = mock(TeamRepository.class);
        PlayerService playerService = mock(PlayerService.class);
        TeamSeasonRepository teamForSeasonRepository = mock(TeamSeasonRepository.class);
        LeagueSeasonRepository leagueSeasonRepository = mock(LeagueSeasonRepository.class);
        LeagueRepository leagueRepository = mock(LeagueRepository.class);
        GameRepository gameRepository = mock(GameRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);
        GameExecutor gameExecutor = mock(GameExecutor.class);
        TrainingExecutor trainingExecutor = mock(TrainingExecutor.class);
        GameTimeEventRepository gameTimeEventRepository = mock(GameTimeEventRepository.class);
        TrainingRepository trainingRepository = mock(TrainingRepository.class);
        TrainingTimeEventRepository trainingTimeEventRepository = mock(TrainingTimeEventRepository.class);

        EventManager eventManager = new EventManager(gameTimeEventRepository, trainingTimeEventRepository);

        SeasonInitializer initializer = new SeasonInitializer(
                playerGenerator,
                teamFactory,
                gamePlanService,
                teamRepository,
                playerService,
                teamForSeasonRepository,
                leagueSeasonRepository,
                leagueRepository,
                gameRepository,
                clubRepository,
                gameExecutor,
                trainingExecutor,
                eventManager,
                gameTimeEventRepository,
                trainingRepository,
                trainingTimeEventRepository
        );

        List<TrainingType> capturedTrainingTypes = new ArrayList<>();

        when(gamePlanService.generateGamePlan(any(Team.class), any(Team.class))).thenAnswer(invocation -> {
            Team owner = invocation.getArgument(0);
            Team opponent = invocation.getArgument(1);
            return new GamePlan(UUID.randomUUID(), owner, opponent);
        });

        when(trainingRepository.save(any(Training.class))).thenAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            capturedTrainingTypes.add(training.getTrainingType());
            return new Training(UUID.randomUUID(), training.getExecuteAt(), training.getTeam(), training.getTrainingType());
        });

        when(trainingTimeEventRepository.save(any(TrainingTimeEvent.class))).thenAnswer(invocation -> {
            TrainingTimeEvent event = invocation.getArgument(0);
            return new TrainingTimeEvent(UUID.randomUUID(), event.getExecuteAt(), event.getTrainingId(), trainingExecutor);
        });

        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> {
            Game game = invocation.getArgument(0);
            return new Game(UUID.randomUUID(), game.getHomeGamePlan(), game.getAwayGamePlan(), game.getLeagueSeason(), game.getExecuteAt());
        });

        when(gameTimeEventRepository.save(any(GameTimeEvent.class))).thenAnswer(invocation -> {
            GameTimeEvent event = invocation.getArgument(0);
            return new GameTimeEvent(UUID.randomUUID(), event.getExecuteAt(), event.getGameId(), gameExecutor);
        });

        Instant start = Instant.parse("2026-03-03T00:00:00Z");
        LeagueSeason leagueSeason = new LeagueSeason(UUID.randomUUID(), new League(null, AgeCategory.SENIOR, Gender.MALE, 1), 2024);
        for (int i = 0; i < 8; i++) {
            Team team = new Team(UUID.randomUUID(), AgeCategory.SENIOR, Gender.MALE, "T" + i);
            leagueSeason.getTeamSeasons().add(new TeamSeason(UUID.randomUUID(), team, leagueSeason.getId(), leagueSeason.getYear()));
        }

        initializer.createGamesForSeason(start, leagueSeason);

        List<TimeEvent> scheduled = eventManager.listAllOrdered();
        List<TrainingTimeEvent> trainings = scheduled.stream()
                .filter(e -> e instanceof TrainingTimeEvent)
                .map(e -> (TrainingTimeEvent) e)
                .toList();
        List<GameTimeEvent> games = scheduled.stream()
                .filter(e -> e instanceof GameTimeEvent)
                .map(e -> (GameTimeEvent) e)
                .toList();

        int teams = leagueSeason.getTeamSeasons().size();
        int rounds = (teams - 1) * 2;

        assertThat(trainings).hasSize(rounds * teams);
        assertThat(games).hasSize(rounds * (teams / 2));
        assertThat(capturedTrainingTypes).hasSize(rounds * teams);
        assertThat(capturedTrainingTypes).doesNotContainNull();
        assertThat(capturedTrainingTypes).anyMatch(t -> t != TrainingType.PHYSICAL);

        List<Instant> trainingInstants = trainings.stream()
                .map(TrainingTimeEvent::getExecuteAt)
                .distinct()
                .sorted()
                .toList();
        List<Instant> gameInstants = games.stream()
                .map(GameTimeEvent::getExecuteAt)
                .distinct()
                .sorted()
                .toList();

        assertThat(trainingInstants).hasSize(rounds);
        assertThat(gameInstants).hasSize(rounds);

        for (int r = 0; r < rounds; r++) {
            Instant expectedTrainingAt = start.plus(r * 5L, ChronoUnit.MINUTES);
            Instant expectedGameAt = expectedTrainingAt.plus(2, ChronoUnit.MINUTES);

            assertThat(trainingInstants.get(r)).isEqualTo(expectedTrainingAt);
            assertThat(gameInstants.get(r)).isEqualTo(expectedGameAt);

            long trainingsAtThisInstant = trainings.stream().filter(t -> t.getExecuteAt().equals(expectedTrainingAt)).count();
            long gamesAtThisInstant = games.stream().filter(g -> g.getExecuteAt().equals(expectedGameAt)).count();
            assertThat(trainingsAtThisInstant).isEqualTo(teams);
            assertThat(gamesAtThisInstant).isEqualTo(teams / 2);
        }

        // Sanity: consecutive rounds are 10 minutes apart.
        for (int r = 0; r < rounds - 1; r++) {
            assertThat(ChronoUnit.MINUTES.between(trainingInstants.get(r), trainingInstants.get(r + 1))).isEqualTo(5);
            assertThat(ChronoUnit.MINUTES.between(gameInstants.get(r), gameInstants.get(r + 1))).isEqualTo(5);
        }
    }
}
