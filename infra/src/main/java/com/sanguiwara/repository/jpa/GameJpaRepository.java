package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.GameEntity;
import com.sanguiwara.repository.jpa.projection.GameSummaryJPAProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface GameJpaRepository extends JpaRepository<GameEntity, UUID> {

    @Query("""
    select new com.sanguiwara.repository.jpa.projection.GameSummaryJPAProjection(
      g.id,
      g.executeAt,
      g.homeGamePlan.id,
      g.awayGamePlan.id,
      ht.id,
      ht.name,
      at.id,
      at.name,
      hc.id,
      ac.id,
      gr.id,
      gr.homeScore.threePointShootingResult.attempts,
      gr.homeScore.threePointShootingResult.made,
      gr.homeScore.driveResult.attempts,
      gr.homeScore.driveResult.made,
      gr.homeScore.twoPointShootingResult.attempts,
      gr.homeScore.twoPointShootingResult.made,
      gr.awayScore.threePointShootingResult.attempts,
      gr.awayScore.threePointShootingResult.made,
      gr.awayScore.driveResult.attempts,
      gr.awayScore.driveResult.made,
      gr.awayScore.twoPointShootingResult.attempts,
      gr.awayScore.twoPointShootingResult.made
    )
    from GameEntity g
    join g.homeGamePlan hgp
    join hgp.ownerTeam ht
    join ht.club hc
    join g.awayGamePlan agp
    join agp.ownerTeam at
    join at.club ac
    left join g.gameResult gr
  """)
    List<GameSummaryJPAProjection> findAllSummaryRows();

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
