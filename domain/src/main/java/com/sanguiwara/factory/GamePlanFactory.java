package com.sanguiwara.factory;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Team;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class GamePlanFactory {



    public GamePlan generateGamePlan(Team t1, Team t2) {


        return new GamePlan(UUID.randomUUID(), t1, t2);

    }
}
