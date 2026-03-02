package com.sanguiwara.service;

import com.sanguiwara.baserecords.Training;
import com.sanguiwara.baserecords.TrainingType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingService {

    Training getTrainingById(UUID trainingId);

    List<Training> getAllTrainingsForATeam(UUID teamId);

    List<Training> getAllTrainings();

    Training createTraining(UUID teamId, Instant executeAt, TrainingType trainingType);

    Optional<Training> getNextTrainingForATeam(UUID teamId);

    Optional<Training> getNextTrainingForAClub(UUID clubId);

    Optional<Training> getNextTrainingForAUserSub(String sub);

    Training updateTraining(UUID trainingId, TrainingType trainingType);

    void executeTraining(UUID trainingId);
}
