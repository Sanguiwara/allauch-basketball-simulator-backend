package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.TrainingTimeEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrainingTimeEventJpaRepository extends JpaRepository<TrainingTimeEventEntity, UUID> {
    Optional<TrainingTimeEventEntity> findByTrainingId(UUID trainingId);

    void deleteByTrainingId(UUID trainingId);
}
