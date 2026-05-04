package com.sanguiwara.service;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Club;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.GamePlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GamePlanServiceImplTest {

    @Test
    void getNextUpcomingGamePlanForAUserSub_returnsNextUpcomingGamePlanForUsersClub() {
        GamePlanRepository gamePlanRepository = mock(GamePlanRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);

        GamePlanServiceImpl service = new GamePlanServiceImpl(gamePlanRepository, clubRepository);

        String sub = "auth0|user-42";
        UUID clubId = UUID.randomUUID();

        Club club = new Club("club");
        club.setId(clubId);

        Team t1 = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "team-1");
        Team t2 = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "team-2");
        GamePlan gamePlan = new GamePlan(UUID.randomUUID(), t1, t2);

        when(clubRepository.findByUserSub(eq(sub))).thenReturn(Optional.of(club));
        when(gamePlanRepository.findNextUpcomingGamePlanForClub(eq(clubId))).thenReturn(Optional.of(gamePlan));
        when(gamePlanRepository.isGameFinished(eq(gamePlan.getId()))).thenReturn(true);

        assertThat(service.getNextUpcomingGamePlanForAUserSub(sub)).contains(gamePlan);
    }

    @Test
    void getNextUpcomingGamePlanForAUserSub_returnsEmptyWhenNoClubForUser() {
        GamePlanRepository gamePlanRepository = mock(GamePlanRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);

        GamePlanServiceImpl service = new GamePlanServiceImpl(gamePlanRepository, clubRepository);

        String sub = "auth0|missing-user";
        when(clubRepository.findByUserSub(eq(sub))).thenReturn(Optional.empty());

        assertThat(service.getNextUpcomingGamePlanForAUserSub(sub)).isEmpty();
    }

    @Test
    void getGamePlan_recalculatesAndPersistsScoresWhenMatchIsNotFinished() {
        GamePlanRepository gamePlanRepository = mock(GamePlanRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);
        GamePlanServiceImpl service = new GamePlanServiceImpl(gamePlanRepository, clubRepository);

        GamePlan gamePlan = createGamePlanWithOnePlayer();
        UUID gamePlanId = gamePlan.getId();

        when(gamePlanRepository.findById(eq(gamePlanId))).thenReturn(Optional.of(gamePlan));
        when(gamePlanRepository.isGameFinished(eq(gamePlanId))).thenReturn(false);
        when(gamePlanRepository.update(any(GamePlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<GamePlan> result = service.getGamePlan(gamePlanId);

        assertThat(result).contains(gamePlan);
        assertThat(gamePlan.getActivePlayers().getFirst().getThreePtScore()).isCloseTo(77.0, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getThreePtDefenseScore()).isCloseTo(44.0, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getTwoPtScore()).isCloseTo(63.75, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getTwoPtDefenseScore()).isCloseTo(52.0, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getDriveScore()).isCloseTo(61.15, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getDriveDefenseScore()).isCloseTo(50.2, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getManToManPlaymakingOffScore()).isCloseTo(67.55, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getManToManPlaymakingDefScore()).isCloseTo(41.4, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getZonePlaymakingOffScore()).isCloseTo(65.5, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getZonePlaymakingDefScore()).isCloseTo(43.0, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getZone23DefenseScore()).isCloseTo(38.25, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getZone32DefenseScore()).isCloseTo(40.3, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getZone212DefenseScore()).isCloseTo(37.15, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getReboundScore()).isCloseTo(56.6, within(0.0001));
        assertThat(gamePlan.getActivePlayers().getFirst().getStealScore()).isCloseTo(38.25, within(0.0001));
        verify(gamePlanRepository).update(gamePlan);
    }

    @Test
    void getGamePlan_doesNotRecalculateScoresWhenMatchIsFinished() {
        GamePlanRepository gamePlanRepository = mock(GamePlanRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);
        GamePlanServiceImpl service = new GamePlanServiceImpl(gamePlanRepository, clubRepository);

        GamePlan gamePlan = createGamePlanWithOnePlayer();
        gamePlan.getActivePlayers().getFirst().setThreePtScore(12.0);
        gamePlan.getActivePlayers().getFirst().setThreePtDefenseScore(15.0);
        gamePlan.getActivePlayers().getFirst().setTwoPtScore(13.0);
        gamePlan.getActivePlayers().getFirst().setTwoPtDefenseScore(16.0);
        gamePlan.getActivePlayers().getFirst().setDriveScore(14.0);
        gamePlan.getActivePlayers().getFirst().setDriveDefenseScore(17.0);
        gamePlan.getActivePlayers().getFirst().setManToManPlaymakingOffScore(18.0);
        gamePlan.getActivePlayers().getFirst().setManToManPlaymakingDefScore(19.0);
        gamePlan.getActivePlayers().getFirst().setZonePlaymakingOffScore(20.0);
        gamePlan.getActivePlayers().getFirst().setZonePlaymakingDefScore(21.0);
        gamePlan.getActivePlayers().getFirst().setZone23DefenseScore(22.0);
        gamePlan.getActivePlayers().getFirst().setZone32DefenseScore(23.0);
        gamePlan.getActivePlayers().getFirst().setZone212DefenseScore(24.0);
        gamePlan.getActivePlayers().getFirst().setReboundScore(25.0);
        gamePlan.getActivePlayers().getFirst().setStealScore(26.0);

        when(gamePlanRepository.findById(eq(gamePlan.getId()))).thenReturn(Optional.of(gamePlan));
        when(gamePlanRepository.isGameFinished(eq(gamePlan.getId()))).thenReturn(true);

        Optional<GamePlan> result = service.getGamePlan(gamePlan.getId());

        assertThat(result).contains(gamePlan);
        assertThat(gamePlan.getActivePlayers().getFirst().getThreePtScore()).isEqualTo(12.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getThreePtDefenseScore()).isEqualTo(15.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getTwoPtScore()).isEqualTo(13.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getTwoPtDefenseScore()).isEqualTo(16.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getDriveScore()).isEqualTo(14.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getDriveDefenseScore()).isEqualTo(17.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getManToManPlaymakingOffScore()).isEqualTo(18.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getManToManPlaymakingDefScore()).isEqualTo(19.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getZonePlaymakingOffScore()).isEqualTo(20.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getZonePlaymakingDefScore()).isEqualTo(21.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getZone23DefenseScore()).isEqualTo(22.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getZone32DefenseScore()).isEqualTo(23.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getZone212DefenseScore()).isEqualTo(24.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getReboundScore()).isEqualTo(25.0);
        assertThat(gamePlan.getActivePlayers().getFirst().getStealScore()).isEqualTo(26.0);
        verify(gamePlanRepository, never()).update(any(GamePlan.class));
    }

    @Test
    void update_rejectsChangesWhenMatchIsFinished() {
        GamePlanRepository gamePlanRepository = mock(GamePlanRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);
        GamePlanServiceImpl service = new GamePlanServiceImpl(gamePlanRepository, clubRepository);

        GamePlan incomingGamePlan = createGamePlanWithOnePlayer();

        when(gamePlanRepository.isGameFinished(eq(incomingGamePlan.getId()))).thenReturn(true);

        assertThatThrownBy(() -> service.update(incomingGamePlan))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(throwable -> {
                    ResponseStatusException exception = (ResponseStatusException) throwable;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(exception.getReason()).isEqualTo("Game plan can no longer be updated because the match is finished");
                });

        verify(gamePlanRepository, never()).update(any(GamePlan.class));
    }

    private static GamePlan createGamePlanWithOnePlayer() {
        return createGamePlanWithOnePlayer(UUID.randomUUID(), UUID.randomUUID(), createPlayer());
    }

    private static GamePlan createGamePlanWithOnePlayer(UUID gamePlanId, UUID inGamePlayerId, Player player) {
        Team t1 = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "team-1");
        Team t2 = new Team(UUID.randomUUID(), AgeCategory.U18, Gender.MALE, "team-2");
        GamePlan gamePlan = new GamePlan(gamePlanId, t1, t2);
        InGamePlayer inGamePlayer = new InGamePlayer(player, gamePlanId);
        inGamePlayer.setId(inGamePlayerId);
        gamePlan.setActivePlayers(List.of(inGamePlayer));
        return gamePlan;
    }

    private static Player createPlayer() {
        return Player.builder()
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
    }
}

