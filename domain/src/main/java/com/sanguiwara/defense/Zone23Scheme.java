package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.calculator.PlayerScoreCalculator;
import com.sanguiwara.type.ShotType;

import java.util.EnumMap;

public final class Zone23Scheme extends ZoneDefensiveScheme {

    public Zone23Scheme(BadgeEngine badgeEngine) {
        super(badgeEngine);
    }
    @Override
    public DefenseType type() {
        return DefenseType.ZONE_2_3;
    }


    @Override
    public double getPlayerDefensiveScoreAgainstShooting(Player player) {
        return PlayerScoreCalculator.calculateZone23DefenseScore(player);
    }


    @Override
    public EnumMap<ShotType, Double> shotCoefficients() {
        EnumMap<ShotType, Double> coefficients = new EnumMap<>(ShotType.class);

        for (ShotType type : ShotType.values()) {
            coefficients.put(type, 1.0); // neutre par défaut
        }
        coefficients.put(ShotType.THREE_POINT, 1.7);
        coefficients.put(ShotType.TWO_POINT, 1.2);
        coefficients.put(ShotType.DRIVE, 0.8);

        return coefficients;
    }

}
