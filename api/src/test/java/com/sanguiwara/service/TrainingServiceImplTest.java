package com.sanguiwara.service;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Club;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.baserecords.Training;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.executor.TrainingExecutor;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.PlayerProgressionRepository;
import com.sanguiwara.repository.TeamRepository;
import com.sanguiwara.repository.TrainingRepository;
import com.sanguiwara.repository.TrainingTimeEventRepository;
import com.sanguiwara.timeevent.EventManager;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainingServiceImplTest {

    @Test
    void getNextTrainingForAClub_returnsNextTrainingForAChosenTeam() {
        TrainingRepository trainingRepository = mock(TrainingRepository.class);
        TeamRepository teamRepository = mock(TeamRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);
        PlayerProgressionRepository playerProgressionRepository = mock(PlayerProgressionRepository.class);
        TrainingTimeEventRepository trainingTimeEventRepository = mock(TrainingTimeEventRepository.class);
        TrainingExecutor trainingExecutor = mock(TrainingExecutor.class);
        EventManager eventManager = mock(EventManager.class);

        TrainingServiceImpl service = new TrainingServiceImpl(
                trainingRepository,
                teamRepository,
                clubRepository,
                playerProgressionRepository,
                trainingTimeEventRepository,
                trainingExecutor,
                eventManager
        );

        UUID clubId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();

        Club club = new Club("club");
        Team team = new Team(teamId, AgeCategory.U18, Gender.MALE, "team");
        club.setId(clubId);
        club.setTeams(List.of(team));

        Training training = new Training(UUID.randomUUID(), Instant.parse("2026-02-27T10:00:00Z"), team, TrainingType.SHOOTING);

        when(clubRepository.findById(eq(clubId))).thenReturn(Optional.of(club));
        when(trainingRepository.findNextByTeamId(eq(teamId), any(Instant.class))).thenReturn(Optional.of(training));

        assertThat(service.getNextTrainingForAClub(clubId)).contains(training);
    }

    @Test
    void getNextTrainingForAClub_returnsEmptyWhenClubDoesNotExist() {
        TrainingRepository trainingRepository = mock(TrainingRepository.class);
        TeamRepository teamRepository = mock(TeamRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);
        PlayerProgressionRepository playerProgressionRepository = mock(PlayerProgressionRepository.class);
        TrainingTimeEventRepository trainingTimeEventRepository = mock(TrainingTimeEventRepository.class);
        TrainingExecutor trainingExecutor = mock(TrainingExecutor.class);
        EventManager eventManager = mock(EventManager.class);

        TrainingServiceImpl service = new TrainingServiceImpl(
                trainingRepository,
                teamRepository,
                clubRepository,
                playerProgressionRepository,
                trainingTimeEventRepository,
                trainingExecutor,
                eventManager
        );

        UUID clubId = UUID.randomUUID();
        when(clubRepository.findById(eq(clubId))).thenReturn(Optional.empty());

        assertThat(service.getNextTrainingForAClub(clubId)).isEmpty();
    }

    @Test
    void getNextTrainingForAClub_returnsEmptyWhenClubHasNoTeams() {
        TrainingRepository trainingRepository = mock(TrainingRepository.class);
        TeamRepository teamRepository = mock(TeamRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);
        PlayerProgressionRepository playerProgressionRepository = mock(PlayerProgressionRepository.class);
        TrainingTimeEventRepository trainingTimeEventRepository = mock(TrainingTimeEventRepository.class);
        TrainingExecutor trainingExecutor = mock(TrainingExecutor.class);
        EventManager eventManager = mock(EventManager.class);

        TrainingServiceImpl service = new TrainingServiceImpl(
                trainingRepository,
                teamRepository,
                clubRepository,
                playerProgressionRepository,
                trainingTimeEventRepository,
                trainingExecutor,
                eventManager
        );

        UUID clubId = UUID.randomUUID();
        Club club = new Club("club");
        club.setId(clubId);
        club.setTeams(List.of());

        when(clubRepository.findById(eq(clubId))).thenReturn(Optional.of(club));

        assertThat(service.getNextTrainingForAClub(clubId)).isEmpty();
    }

    @Test
    void getNextTrainingForAUser_returnsNextTrainingForTheUsersClub() {
        TrainingRepository trainingRepository = mock(TrainingRepository.class);
        TeamRepository teamRepository = mock(TeamRepository.class);
        ClubRepository clubRepository = mock(ClubRepository.class);
        PlayerProgressionRepository playerProgressionRepository = mock(PlayerProgressionRepository.class);
        TrainingTimeEventRepository trainingTimeEventRepository = mock(TrainingTimeEventRepository.class);
        TrainingExecutor trainingExecutor = mock(TrainingExecutor.class);
        EventManager eventManager = mock(EventManager.class);

        TrainingServiceImpl service = new TrainingServiceImpl(
                trainingRepository,
                teamRepository,
                clubRepository,
                playerProgressionRepository,
                trainingTimeEventRepository,
                trainingExecutor,
                eventManager
        );

        String sub = "auth0|user-42";
        UUID clubId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();

        Club club = new Club("club");
        Team team = new Team(teamId, AgeCategory.U18, Gender.MALE, "team");
        club.setId(clubId);
        club.setTeams(List.of(team));

        Training training = new Training(UUID.randomUUID(), Instant.parse("2026-02-27T10:00:00Z"), team, TrainingType.SHOOTING);

        when(clubRepository.findByUserSub(eq(sub))).thenReturn(Optional.of(club));
        when(clubRepository.findById(eq(clubId))).thenReturn(Optional.of(club));
        when(trainingRepository.findNextByTeamId(eq(teamId), any(Instant.class))).thenReturn(Optional.of(training));

        assertThat(service.getNextTrainingForAUserSub(sub)).contains(training);
    }
}
