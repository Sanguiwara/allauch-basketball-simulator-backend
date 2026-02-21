package com.sanguiwara;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.type.ShotType;

final class ShootingSkillProgressionManager {

    private static final int MIN_SKILL_VALUE = 1;
    private static final int MAX_SKILL_VALUE = 99;

    private static final int MINUTES_REFERENCE = 20;
    private static final double POTENTIAL_MULT_BASE = 0.6;
    private static final double POTENTIAL_MULT_RANGE = 1.2;

    private static final int MADE_SOFT_CAP = 6;
    private static final double SHOOTING_MAKE_BONUS = 2.0;

    // --- Shooting volume curve calibration (PATCH) ---
    private static final int SHOOTING_VOLUME_NO_REGRESSION_FROM = 4;   // >=4 : no negative volume delta
    private static final double SHOOTING_VOLUME_REGRESSION_TAU = 1.2;  // how fast regression vanishes
    private static final double SHOOTING_VOLUME_MAX_AT_15 = 10;       // target at 15 attempts (tune 2.0..3.0)
    private static final double SHOOTING_VOLUME_EXPONENT = 2.2;        // tuned so at 8 attempts ~ +1

    void applyShootingSkillProgression(InGamePlayer p) {
        var player = p.getPlayer();
        int minutesPlayed = p.getMinutesPlayed();
        if (minutesPlayed == 0) {
            return;
        }

        double minutesMult = minutesMultiplier(minutesPlayed);
        double potentialMult = potentialMultiplier(player.getPotentielSkill());

        for (ShotType shotType : ShotType.values()) {
            int attempts;
            int made;

            switch (shotType) {
                case THREE_POINT -> {
                    attempts = p.getThreePointAttempt();
                    made = p.getThreePointMade();
                }
                case TWO_POINT -> {
                    attempts = p.getTwoPointAttempts();
                    made = p.getTwoPointMade();
                }
                case DRIVE -> {
                    attempts = p.getDriveAttempts();
                    made = p.getDriveMade();
                }
                default -> throw new IllegalStateException("Unexpected shotType=" + shotType);
            }

            // Volume curve (PATCH): 0 => regress, ~4 => no regression, 8 => ~+1, 15 => ~+2.6
            double volumeDelta = shootingVolumeDelta(attempts);

            // Makes help, but volume alone can still yield progression (even on misses).
            double makeBonus = SHOOTING_MAKE_BONUS * saturatingLog(made);
            double rawDelta = volumeDelta + makeBonus;

            double scaledDelta = rawDelta
                    * minutesMult
                    * potentialMult;

            // Only change here: widen clamp to allow +2..+3 outcomes from volume if you want.

            int delta = (int) Math.round(scaledDelta);
            switch (shotType) {
                case THREE_POINT -> player.setTir3Pts(applyDelta(player.getTir3Pts(), delta));
                case TWO_POINT -> player.setTir2Pts(applyDelta(player.getTir2Pts(), delta));
                case DRIVE -> {
                    player.setFinitionAuCercle(applyDelta(player.getFinitionAuCercle(), delta));
                    player.setFloater(applyDelta(player.getFloater(), delta));

                }
            }
        }
    }

    private static int applyDelta(int currentSkill, int delta) {
        return Math.clamp(currentSkill + delta, MIN_SKILL_VALUE, MAX_SKILL_VALUE);
    }

    private static double shootingVolumeDelta(int attempts) {
        double attemptsClamped = Math.max(0.0, attempts);

        // Regression only for very low volume (fast vanishing)
        if (attemptsClamped < SHOOTING_VOLUME_NO_REGRESSION_FROM) {
            return -2.0 * Math.exp(-attemptsClamped / SHOOTING_VOLUME_REGRESSION_TAU);
        }

        // Progression: log-normalized between 4..15, smooth start.
        // normalizedVolume01 = 0 at 4 attempts, = 1 at 15 attempts
        double attemptsAfterNoRegression = attemptsClamped - SHOOTING_VOLUME_NO_REGRESSION_FROM;
        double attemptsRangeTo15 = 15.0 - SHOOTING_VOLUME_NO_REGRESSION_FROM;
        double normalizedVolume01 = Math.log1p(attemptsAfterNoRegression) / Math.log1p(attemptsRangeTo15);

        return SHOOTING_VOLUME_MAX_AT_15 * Math.pow(normalizedVolume01, SHOOTING_VOLUME_EXPONENT);
    }

    private static double saturatingLog(int value) {

        return Math.log1p(value) / Math.log1p(ShootingSkillProgressionManager.MADE_SOFT_CAP);
    }

    private static double minutesMultiplier(int minutesPlayed) {
        return minutesPlayed / (double) MINUTES_REFERENCE;
    }

    private static double potentialMultiplier(int potential0to99) {
        double p = potential0to99 / (double) MAX_SKILL_VALUE;
        return POTENTIAL_MULT_BASE + POTENTIAL_MULT_RANGE * p;
    }
}

