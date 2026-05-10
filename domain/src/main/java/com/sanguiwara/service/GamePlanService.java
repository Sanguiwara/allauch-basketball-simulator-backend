package com.sanguiwara.service;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GamePlanService {
    Optional<GamePlan> getGamePlan(UUID id);

    Optional<GamePlan> getNextUpcomingGamePlanForClub(UUID clubId);

    Optional<GamePlan> getNextUpcomingGamePlanForAUserSub(String sub);

    GamePlan update(GamePlan gamePlan);


    List<GamePlan> saveAndApplyToUpcomingGamePlans(GamePlan gamePlan);



    GamePlan generateGamePlan(Team t1, Team t2);
}
