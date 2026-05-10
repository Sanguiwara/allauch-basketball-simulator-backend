package com.sanguiwara.repository;

import com.sanguiwara.baserecords.GamePlan;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GamePlanRepository {

    Optional<GamePlan> findById(UUID id);

    Optional<GamePlan> findNextUpcomingGamePlanForClub(UUID clubId);

    List<GamePlan> findUpcomingUnplayedGamePlansForTeam(UUID teamId, Instant now);

    boolean isGameFinished(UUID gamePlanId);

    GamePlan update(GamePlan gamePlan);

    GamePlan save(GamePlan gamePlan);

    void deleteById(UUID id);


    void deleteAll();
}
