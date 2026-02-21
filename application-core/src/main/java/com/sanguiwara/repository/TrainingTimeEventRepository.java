package com.sanguiwara.repository;

import com.sanguiwara.timeevent.TrainingTimeEvent;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface TrainingTimeEventRepository {

    Optional<TrainingTimeEvent> findById(UUID id);

    TrainingTimeEvent save(TrainingTimeEvent event);

    Collection<TrainingTimeEvent> findAll();

    Optional<TrainingTimeEvent> findByTrainingId(UUID trainingId);

    void deleteById(UUID id);

    void deleteByTrainingId(UUID trainingId);

    void deleteAll();
}
