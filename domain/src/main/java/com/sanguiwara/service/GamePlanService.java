package com.sanguiwara.service;

import com.sanguiwara.baserecords.GamePlan;

import java.util.Optional;
import java.util.UUID;

public interface GamePlanService {
    Optional<GamePlan> getGamePlan(UUID id);


    GamePlan update(GamePlan gamePlan);


    void delete(UUID id);


}
