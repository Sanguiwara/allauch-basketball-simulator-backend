package com.sanguiwara.service;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Team;

import java.util.Optional;
import java.util.UUID;

public interface GamePlanService {
    Optional<GamePlan> getGamePlan(UUID id);

    Optional<GamePlan> getNextUpcomingGamePlanForClub(UUID clubId);


    GamePlan update(GamePlan gamePlan);


    void delete(UUID id);


    GamePlan generateGamePlan(Team t1, Team t2);
}
