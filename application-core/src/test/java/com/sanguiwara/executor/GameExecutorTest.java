package com.sanguiwara.executor;

import com.sanguiwara.PostGameManager;
import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.calculator.GameSimulator;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.repository.GameRepository;
import com.sanguiwara.repository.PlayerProgressionRepository;
import com.sanguiwara.repository.PlayerRepository;
import com.sanguiwara.result.BoxScore;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.GameResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameExecutorTest {

    @Test
    void executeGame_recalculatesAndPersistsScoresBeforeSimulation() {
        GameSimulator gameSimulator = mock(GameSimulator.class);
        GameRepository gameRepository = mock(GameRepository.class);
        PostGameManager postGameManager = mock(PostGameManager.class);
        PlayerProgressionRepository playerProgressionRepository = mock(PlayerProgressionRepository.class);
        PlayerRepository playerRepository = mock(PlayerRepository.class);

        GameExecutor executor = new GameExecutor(
                gameSimulator,
                gameRepository,
                postGameManager,
                playerProgressionRepository,
                playerRepository
        );

        Game game = createGame();
        GameResult gameResult = emptyGameResult();

        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(gameSimulator.calculateGame(any(GamePlan.class), any(GamePlan.class))).thenReturn(gameResult);
        when(postGameManager.applyPostGameEffectsAndReturnsPlayersProgression(any(Game.class))).thenReturn(List.of());
        when(playerProgressionRepository.saveAll(any())).thenReturn(List.of());
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        executor.executeGame(game.getId());

        ArgumentCaptor<Game> postGameSaveCaptor = ArgumentCaptor.forClass(Game.class);
        ArgumentCaptor<GamePlan> simulatedHomeGamePlanCaptor = ArgumentCaptor.forClass(GamePlan.class);
        ArgumentCaptor<GamePlan> simulatedAwayGamePlanCaptor = ArgumentCaptor.forClass(GamePlan.class);
        InOrder inOrder = inOrder(gameRepository, gameSimulator);

        inOrder.verify(gameRepository).findById(game.getId());
        inOrder.verify(gameRepository).save(any(Game.class));
        inOrder.verify(gameSimulator).calculateGame(simulatedHomeGamePlanCaptor.capture(), simulatedAwayGamePlanCaptor.capture());
        inOrder.verify(gameRepository).save(postGameSaveCaptor.capture());

        GamePlan simulatedHomeGamePlan = simulatedHomeGamePlanCaptor.getValue();
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getThreePtScore()).isCloseTo(77.0, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getThreePtDefenseScore()).isCloseTo(44.0, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getTwoPtScore()).isCloseTo(63.75, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getTwoPtDefenseScore()).isCloseTo(52.0, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getDriveScore()).isCloseTo(61.15, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getDriveDefenseScore()).isCloseTo(50.2, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getManToManPlaymakingOffScore()).isCloseTo(67.55, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getManToManPlaymakingDefScore()).isCloseTo(41.4, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getZonePlaymakingOffScore()).isCloseTo(65.5, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getZonePlaymakingDefScore()).isCloseTo(43.0, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getZone23DefenseScore()).isCloseTo(38.25, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getZone32DefenseScore()).isCloseTo(40.3, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getZone212DefenseScore()).isCloseTo(37.15, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getReboundScore()).isCloseTo(56.6, within(0.0001));
        assertThat(simulatedHomeGamePlan.getActivePlayers().getFirst().getStealScore()).isCloseTo(38.25, within(0.0001));

        GamePlan simulatedAwayGamePlan = simulatedAwayGamePlanCaptor.getValue();
        assertThat(simulatedAwayGamePlan.getActivePlayers().getFirst().getThreePtScore()).isCloseTo(77.0, within(0.0001));
        assertThat(simulatedAwayGamePlan.getActivePlayers().getFirst().getThreePtDefenseScore()).isCloseTo(44.0, within(0.0001));
        assertThat(simulatedAwayGamePlan.getActivePlayers().getFirst().getZone23DefenseScore()).isCloseTo(38.25, within(0.0001));
        assertThat(simulatedAwayGamePlan.getActivePlayers().getFirst().getReboundScore()).isCloseTo(56.6, within(0.0001));
        assertThat(simulatedAwayGamePlan.getActivePlayers().getFirst().getStealScore()).isCloseTo(38.25, within(0.0001));

        Game postGameSaved = postGameSaveCaptor.getValue();
        assertThat(postGameSaved.getGameResult()).isEqualTo(gameResult);

        verify(gameRepository, times(2)).save(any(Game.class));
        verify(playerRepository, times(2)).save(any(Player.class));
        verify(playerProgressionRepository).saveAll(List.of());
    }

    private static Game createGame() {
        Team homeTeam = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "home");
        Team awayTeam = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "away");

        GamePlan homeGamePlan = new GamePlan(UUID.randomUUID(), homeTeam, awayTeam);
        homeGamePlan.setActivePlayers(List.of(createInGamePlayer(homeGamePlan.getId())));

        GamePlan awayGamePlan = new GamePlan(UUID.randomUUID(), awayTeam, homeTeam);
        awayGamePlan.setActivePlayers(List.of(createInGamePlayer(awayGamePlan.getId())));

        return new Game(UUID.randomUUID(), homeGamePlan, awayGamePlan, null, Instant.now());
    }

    private static InGamePlayer createInGamePlayer(UUID gamePlanId) {
        Player player = Player.builder()
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(2000)
                .speed(60)
                .size(70)
                .weight(65)
                .agressivite(20)
                .defExterieur(40)
                .defPoste(50)
                .protectionCercle(35)
                .endurance(80)
                .timingRebond(75)
                .agressiviteRebond(55)
                .steal(30)
                .timingBlock(25)
                .physique(35)
                .tir3Pts(90)
                .tir2Pts(65)
                .finitionAuCercle(55)
                .ballhandling(75)
                .floater(45)
                .basketballIqOff(50)
                .basketballIqDef(10)
                .passingSkills(85)
                .iq(40)
                .coachability(60)
                .build();
        return new InGamePlayer(player, gamePlanId);
    }

    private static GameResult emptyGameResult() {
        BoxScore emptyBoxScore = new BoxScore(
                ThreePointShootingResult.empty(),
                DriveResult.empty(),
                TwoPointShootingResult.empty()
        );
        return new GameResult(emptyBoxScore, emptyBoxScore);
    }
}
