package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.GameEntity;
import com.sanguiwara.entity.GamePlanEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface GamePlanJpaRepository extends JpaRepository<GamePlanEntity, UUID> {

    @Query("""
              select g
              from GameEntity g
              where g.executeAt >= :now
                and g.gameResult is null
                and (
                  g.homeGamePlan.ownerTeam.club.id = :clubId
                  or g.awayGamePlan.ownerTeam.club.id = :clubId
                )
              order by g.executeAt asc
            """)
    List<GameEntity> findNextGameForClub(@Param("clubId") UUID clubId,
                                         @Param("now") Instant now,
                                         Pageable pageable);
}
