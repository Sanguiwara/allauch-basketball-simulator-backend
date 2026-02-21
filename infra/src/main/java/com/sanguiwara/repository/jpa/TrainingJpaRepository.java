package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.TrainingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingJpaRepository extends JpaRepository<TrainingEntity, UUID> {
    List<TrainingEntity> findAllByTeam_Id(UUID teamId);

    boolean existsByTeam_IdAndExecuteAt(UUID teamId, java.time.Instant executeAt);

    Optional<TrainingEntity> findFirstByTeam_IdAndExecuteAtGreaterThanEqualOrderByExecuteAtAsc(UUID teamId, Instant executeAt);
}
