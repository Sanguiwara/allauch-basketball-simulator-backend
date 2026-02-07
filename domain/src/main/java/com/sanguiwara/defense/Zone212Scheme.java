package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.type.ShotType;

import java.util.EnumMap;

public final class Zone212Scheme extends ZoneDefensiveScheme {


    private static final double WEIGHT_DEF_EXTERIEUR = 0.15;
    private static final double WEIGHT_SPEED = 0.10;
    private static final double WEIGHT_IQ_DEF = 0.40;
    private static final double WEIGHT_STEAL = 0.10;
    private static final double WEIGHT_DEF_POSTE = 0.25;
    private static final double WEIGHT_PROTECTION_CERCLE = 0.15;
    private static final double WEIGHT_ENDURANCE = 0.10;
    private static final double WEIGHT_SIZE = 0.15;

    @Override
    public DefenseType type() {
        return DefenseType.ZONE_2_1_2;
    }


    @Override
    public double getPlayerDefensiveScoreAgainstShooting(Player player) {
        return WEIGHT_DEF_EXTERIEUR * player.getDefExterieur()
                + WEIGHT_SPEED * player.getSpeed()
                + WEIGHT_IQ_DEF * player.getBasketballIqDef()
                + WEIGHT_STEAL * player.getSteal()
                + WEIGHT_DEF_POSTE * player.getDefPoste()
                + WEIGHT_PROTECTION_CERCLE * player.getProtectionCercle()
                + WEIGHT_ENDURANCE * player.getEndurance()
                + WEIGHT_SIZE * player.getSize();
    }


    @Override
    public EnumMap<ShotType, Double> shotCoefficients() {
        EnumMap<ShotType, Double> coefficients = new EnumMap<>(ShotType.class);

        for (ShotType type : ShotType.values()) {
            coefficients.put(type, 1.0); // neutre par défaut
        }
        coefficients.put(ShotType.THREE_POINT, 1.2);
        coefficients.put(ShotType.TWO_POINT, 1.2);
        coefficients.put(ShotType.DRIVE, 0.8);

        return coefficients;
    }


}
