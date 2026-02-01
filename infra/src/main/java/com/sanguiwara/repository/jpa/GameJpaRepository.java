package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface GameJpaRepository extends JpaRepository<GameEntity, UUID> {

    @Query("""
    select g
    from GameEntity g
    where g.homeGamePlan.ownerTeam.id = :teamId
       or g.awayGamePlan.ownerTeam.id = :teamId
  """)
    List<GameEntity> findAllByParticipantTeamId(UUID teamId);
}
