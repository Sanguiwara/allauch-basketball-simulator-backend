package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.type.ShotType;

import java.util.EnumMap;

public final class Zone32Scheme extends ZoneDefensiveScheme {


    private static final double WEIGHT_DEF_EXTERIEUR = 0.45;
    private static final double WEIGHT_SPEED = 0.12;
    private static final double WEIGHT_STEAL = 0.12;
    private static final double WEIGHT_ENDURANCE = 0.10;
    private static final double WEIGHT_IQ_DEF = 0.25;
    private static final double WEIGHT_SIZE = 0.08;
    private static final double WEIGHT_DEF_POSTE = 0.08;
    private static final double WEIGHT_PROTECTION_CERCLE = 0.06;
    private static final double WEIGHT_TIMING_BLOCK = 0.04;

    @Override
    public DefenseType type() {
        return DefenseType.ZONE_3_2;
    }


    @Override
    public double getPlayerDefensiveScoreAgainstShooting(Player player) {
        return WEIGHT_DEF_EXTERIEUR * player.getDefExterieur()
                + WEIGHT_SPEED * player.getSpeed()
                + WEIGHT_STEAL * player.getSteal()
                + WEIGHT_ENDURANCE * player.getEndurance()
                + WEIGHT_IQ_DEF * player.getBasketballIqDef()
                + WEIGHT_SIZE * player.getSize()
                + WEIGHT_DEF_POSTE * player.getDefPoste()
                + WEIGHT_PROTECTION_CERCLE * player.getProtectionCercle()
                + WEIGHT_TIMING_BLOCK * player.getTimingBlock();
    }


    @Override
    public EnumMap<ShotType, Double> shotCoefficients() {
        EnumMap<ShotType, Double> coefficients = new EnumMap<>(ShotType.class);

        for (ShotType type : ShotType.values()) {
            coefficients.put(type, 1.0); // neutre par défaut
        }
        coefficients.put(ShotType.THREE_POINT, 0.8);
        coefficients.put(ShotType.TWO_POINT, 1.2);
        coefficients.put(ShotType.DRIVE, 1.4);

        return coefficients;
    }

}
