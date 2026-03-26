package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.calculator.spec.ShotSpec;

public interface DefensiveScheme {
    DefenseType type();

    /**
     * Computes the average offensive advantage for the given attacker, over his played minutes.
     * <p>
     * For man-to-man defenses, this can account for the overlap between the attacker and his matchup defender.
     */
    double calculateAdvantageForAPlayer(InGamePlayer attacker, GamePlan defensiveGamePlan, ShotSpec<?, ?> shotSpec);


    double getOffensiveTeamPlaymakingScore(GamePlan offenseTeam, GamePlan defenseTeam);
}
