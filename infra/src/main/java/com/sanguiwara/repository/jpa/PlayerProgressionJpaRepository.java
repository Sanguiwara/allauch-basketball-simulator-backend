package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.PlayerProgressionEntity;
import com.sanguiwara.entity.PlayerProgressionId;
import com.sanguiwara.progression.ProgressionEventType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerProgressionJpaRepository extends JpaRepository<PlayerProgressionEntity, PlayerProgressionId> {
    @EntityGraph(attributePaths = {"player", "player.badges"})
    List<PlayerProgressionEntity> findAllById_EventTypeAndId_EventId(ProgressionEventType eventType, UUID eventId);

    @EntityGraph(attributePaths = {"player", "player.badges"})
    List<PlayerProgressionEntity> findAllByPlayer_Id(UUID playerId);
}
