package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.ShotEvent;
import com.sanguiwara.result.ShotResult;

import java.util.List;

public interface ShotSpec<E extends ShotEvent, R extends ShotResult<E>> {

    void distributeShotAttempts(GamePlan plan);
    double computePct(InGamePlayer shooter, double advantage, boolean assistBonusPct);
    double evaluateMatchupAdvantage(Player attacker, Player defender );

    int getAttempts(InGamePlayer shooter);




    E create(InGamePlayer shooter, int shotNumber, boolean assisted, java.util.UUID assisterId, double pct, boolean made, double advantage);


    R createResult(int attempts, int made, List<E> events);

    R empty();

    R combine(R a, R b);


}