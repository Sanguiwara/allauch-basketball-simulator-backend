package com.sanguiwara.factory;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Team;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GamePlanFactory {

//TODO SUPPRIMER

    public GamePlan generateGamePlan(Team t1, Team t2) {


        return new GamePlan(null, t1, t2);

    }
}
