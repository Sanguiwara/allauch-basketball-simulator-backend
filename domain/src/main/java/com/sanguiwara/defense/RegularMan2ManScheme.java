package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.calculator.spec.ShotSpec;

public final class RegularMan2ManScheme extends Man2ManScheme {

    public static final double DEFAULT_VALUE_EMPTY_MATCHUP = 0.0;

    public RegularMan2ManScheme(BadgeEngine badgeEngine) {
        super(badgeEngine);
    }

    @Override
    public DefenseType type() {
        return DefenseType.MAN_TO_MAN;
    }

    @Override
    public double calculateAdvantageForAPlayer(Player attacker, GamePlan defensiveGamePlan, ShotSpec<?, ?> shotSpec) {

        Player defender = defensiveGamePlan.getMatchups().get(attacker);
        if (defender == null)
            return DEFAULT_VALUE_EMPTY_MATCHUP;
        else
            return shotSpec.getPlayerScoreForAShot(attacker) - shotSpec.getDefensiveScoreForAShot(defender);
    }





}
