package com.sanguiwara.repository.jpa;

import com.sanguiwara.entity.GameEntity;
import com.sanguiwara.entity.GameTimeEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface GameTimeEventJpaRepository extends JpaRepository<GameTimeEventEntity, UUID> {

    @Query("""
            select g
            from GameTimeEventEntity e
            join e.game g
            where e.executeAt >= :now
              and g.gameResult is null
              and (
                g.homeGamePlan.ownerTeam.club.id = :clubId
                or g.awayGamePlan.ownerTeam.club.id = :clubId
              )
            order by e.executeAt asc
            """)
    List<GameEntity> findNextUpcomingGamesByClubId(UUID clubId, Instant now, Pageable pageable);
}
