package com.sanguiwara.initializer;

import com.sanguiwara.baserecords.*;
import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.factory.GamePlanFactory;
import com.sanguiwara.factory.PlayerFactory;
import com.sanguiwara.factory.TeamFactory;
import com.sanguiwara.repository.*;
import com.sanguiwara.timeevent.EventManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeasonInitializerTest {

    @Mock private PlayerFactory playerFactory;
    @Mock private TeamFactory teamFactory;
    @Mock private GamePlanFactory gamePlanFactory;
    @Mock private TeamRepository teamRepository;
    @Mock private PlayerRepository playerRepository;
    @Mock private TeamSeasonRepository teamSeasonRepository;
    @Mock private LeagueSeasonRepository leagueSeasonRepository;
    @Mock private GamePlanRepository gamePlanRepository;
    @Mock private LeagueRepository leagueRepository;
    @Mock private GameRepository gameRepository;
    @Mock private GameExecutor gameExecutor;
    @Mock private EventManager eventManager;
    @Mock private GameTimeEventRepository gameTimeEventRepository;

    private SeasonInitializer seasonInitializer;

    @BeforeEach
    void setUp() {
        seasonInitializer = new SeasonInitializer(
                playerFactory,
                teamFactory,
                gamePlanFactory,
                teamRepository,
                playerRepository,
                teamSeasonRepository,
                leagueSeasonRepository,
                gamePlanRepository,
                leagueRepository,
                gameRepository,
                gameExecutor,
                eventManager,
                gameTimeEventRepository
        );

        // Default: repositories return their input entities on save
        when(leagueSeasonRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(leagueRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(playerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(teamRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        // void method -> use doAnswer
        doAnswer(_ -> null).when(teamSeasonRepository).save(any());
        when(gamePlanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(gamePlanRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));
        when(gameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(gameTimeEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Player factory returns a fresh dummy player each time
        when(playerFactory.generatePlayer(anyString())).thenAnswer(inv -> dummyPlayer());

        // Team factory copies provided players onto the created team
        when(teamFactory.generateTeam(any(AgeCategory.class), any(Gender.class), anyList())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            List<Player> players = (List<Player>) inv.getArgument(2);
            Team team = new Team(UUID.randomUUID(), inv.getArgument(0), (Gender) inv.getArgument(1), "");
            team.setPlayers(players);
            return team;
        });

        // GamePlan factory builds a plan based on teams
        when(gamePlanFactory.generateGamePlan(any(Team.class), any(Team.class)))
                .thenAnswer(inv -> new GamePlan(UUID.randomUUID(),
                        inv.getArgument(0), inv.getArgument(1)));
    }

    @Test
    void createSeason_generates_expected_entities_and_events() {
        // Given
        Instant start = Instant.parse("2026-01-26T00:00:00Z");

        // When
        seasonInitializer.createSeason(start);

        // Then
        // One league + leagueSeason saved multiple times during building (1 initial + 8 in loop + 1 final)
        verify(leagueRepository, times(1)).save(any(League.class));
        verify(leagueSeasonRepository, times(10)).save(any(LeagueSeason.class));

        // 8 teams, 12 players each
        verify(playerRepository, times(8 * 12)).save(any(Player.class));
        verify(teamRepository, times(8)).save(any(Team.class));
        verify(teamSeasonRepository, times(8)).save(any(TeamSeason.class));

        // Round-robin double gameplan per matchup: 8*7=56 matchups, 2 game plans each
        verify(gamePlanRepository, times(56 * 2)).update(any(GamePlan.class));

        // One game per matchup and one scheduled event per game
        verify(gameRepository, times(56)).save(any(Game.class));
        verify(eventManager, times(56)).schedule(any());

        verifyNoMoreInteractions(leagueRepository, leagueSeasonRepository, playerRepository,
                teamRepository, teamSeasonRepository, gamePlanRepository, gameRepository, eventManager);
    }

    private Player dummyPlayer() {
        UUID id = UUID.randomUUID();
        return new Player(
                id,
                "Player-" + id,
                1990,
                // Shooting/finishing (10)
                50, 50, 50, 50, 50, 50, 50, 50, 50, 50,
                // Defense/rebound (7)
                50, 50, 50, 50, 50, 50, 50,
                // Physique/mental/skills (6)
                50, 50, 50, 50, 50, 50,
                // Solidite (1)
                50,
                // Potentiel (2)
                50, 50,
                // Attitude/comportement (4)
                50, 50, 50, 50
        );
    }
}
