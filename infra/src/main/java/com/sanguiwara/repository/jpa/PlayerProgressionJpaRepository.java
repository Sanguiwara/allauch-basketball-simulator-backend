package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.PlayerProgressionEntity;
import com.sanguiwara.entity.PlayerProgressionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerProgressionJpaRepository extends JpaRepository<PlayerProgressionEntity, PlayerProgressionId> {
    List<PlayerProgressionEntity> findAllByEventGame_Id(UUID eventId);

    List<PlayerProgressionEntity> findAllByPlayer_Id(UUID playerId);
}

