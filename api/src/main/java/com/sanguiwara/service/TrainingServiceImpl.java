package com.sanguiwara.service;

import com.sanguiwara.baserecords.Club;
import com.sanguiwara.baserecords.Training;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.executor.TrainingExecutor;
import com.sanguiwara.progression.ProgressionEventType;
import com.sanguiwara.repository.ClubRepository;
import com.sanguiwara.repository.PlayerProgressionRepository;
import com.sanguiwara.repository.TeamRepository;
import com.sanguiwara.repository.TrainingRepository;
import com.sanguiwara.repository.TrainingTimeEventRepository;
import com.sanguiwara.timeevent.EventManager;
import com.sanguiwara.timeevent.TrainingTimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TeamRepository teamRepository;
    private final ClubRepository clubRepository;
    private final PlayerProgressionRepository playerProgressionRepository;
    private final TrainingTimeEventRepository trainingTimeEventRepository;
    private final TrainingExecutor trainingExecutor;
    private final EventManager eventManager;

    @Override
    public Training getTrainingById(UUID trainingId) {
        Training training = trainingRepository.findById(trainingId).orElseThrow();
        training.setPlayerProgressions(playerProgressionRepository.findByEvent(ProgressionEventType.TRAINING, trainingId));
        return training;
    }

    @Override
    public List<Training> getAllTrainingsForATeam(UUID teamId) {
        return trainingRepository.findAllByTeamId(teamId);
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    @Override
    public Training createTraining(UUID teamId, Instant executeAt, TrainingType trainingType) {
        var existingSameSlot = trainingRepository.findAllByTeamId(teamId).stream()
                .anyMatch(t -> executeAt.equals(t.getExecuteAt()));
        if (existingSameSlot) {
            throw new IllegalArgumentException("Training already scheduled for teamId=" + teamId + " at executeAt=" + executeAt);
        }
        var team = teamRepository.findById(teamId).orElseThrow();
        Training training = new Training(UUID.randomUUID(), executeAt, team, trainingType);
        Training saved = trainingRepository.save(training);

        TrainingTimeEvent timeEvent = new TrainingTimeEvent(UUID.randomUUID(), saved.getExecuteAt(), saved.getId(), trainingExecutor);
        TrainingTimeEvent persisted = trainingTimeEventRepository.save(timeEvent);
        eventManager.schedule(persisted);

        return saved;
    }

    @Override
    public Optional<Training> getNextTrainingForATeam(UUID teamId) {
        return trainingRepository.findNextByTeamId(teamId, Instant.now());
    }

    @Override
    public Optional<Training> getNextTrainingForAClub(UUID clubId) {
        return clubRepository.findById(clubId)
                .map(Club::getTeams)
                .filter(teams -> !teams.isEmpty())
                .map(teams -> teams.get(ThreadLocalRandom.current().nextInt(teams.size())))
                .flatMap(team -> getNextTrainingForATeam(team.getId()));
    }

    @Override
    public Optional<Training> getNextTrainingForAUserSub(String sub) {
        return clubRepository.findByUserSub(sub)
                .flatMap(club -> getNextTrainingForAClub(club.getId()));
    }

    @Override
    public Training updateTraining(UUID trainingId, TrainingType trainingType) {
        Training existing = trainingRepository.findById(trainingId).orElseThrow();
        TrainingType newTrainingType = trainingType != null ? trainingType : existing.getTrainingType();

        Training updated = new Training(existing.getId(), existing.getExecuteAt(), existing.getTeam(), newTrainingType);
        return trainingRepository.save(updated);
    }

    @Override
    public void executeTraining(UUID trainingId) {
        trainingTimeEventRepository.findByTrainingId(trainingId).ifPresent(e -> eventManager.cancel(e.getId()));
        trainingExecutor.executeTraining(trainingId);
        trainingTimeEventRepository.deleteByTrainingId(trainingId);
    }
}
