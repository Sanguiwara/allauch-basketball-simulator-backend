package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.calculator.spec.ShotSpec;

public interface DefensiveScheme {
    DefenseType type();

    double calculateAdvantageForAPlayer(Player player, GamePlan defensiveGamePlan, ShotSpec<?,?> shotSpec);


    double getOffensiveTeamPlaymakingScore(GamePlan offenseTeam, GamePlan defenseTeam);
}

