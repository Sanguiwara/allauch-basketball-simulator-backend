package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.PlayerSeasonSnapshotEntity;
import com.sanguiwara.entity.PlayerSeasonSnapshotId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerSeasonSnapshotJpaRepository extends JpaRepository<PlayerSeasonSnapshotEntity, PlayerSeasonSnapshotId> {

    List<PlayerSeasonSnapshotEntity> findById_PlayerId(UUID playerId);
}
