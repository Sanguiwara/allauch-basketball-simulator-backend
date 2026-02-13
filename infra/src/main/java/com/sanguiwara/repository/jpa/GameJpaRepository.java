package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.GameEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
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


    @Query("""
    select g
    from GameEntity g
    where g.executeAt >= :now
      and (
        g.homeGamePlan.ownerTeam.club.id = :clubId
        or g.awayGamePlan.ownerTeam.club.id = :clubId
      )
    order by g.executeAt asc
  """)
    List<GameEntity> findNextGameForClub(UUID clubId, Instant now, Pageable pageable);

}
