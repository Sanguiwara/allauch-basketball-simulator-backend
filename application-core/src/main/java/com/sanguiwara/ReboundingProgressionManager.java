package com.sanguiwara;

import com.sanguiwara.baserecords.InGamePlayer;

final class ReboundingProgressionManager {

    private static final int MIN_SKILL_VALUE = 1;
    private static final int MAX_SKILL_VALUE = 99;

    private static final int MINUTES_REFERENCE = 20;
    private static final double POTENTIAL_MULT_BASE = 0.6;
    private static final double POTENTIAL_MULT_RANGE = 1.2;

    private static final int REBOUNDS_SOFT_CAP = 12;
    private static final double BASE_REBOUND_GAIN = 5;
    private static final double REBOUND_AGGRESS_DELTA_MULT = 0.85;

    void applyReboundingProgression(InGamePlayer p) {
        var player = p.getPlayer();
        int minutesPlayed = p.getMinutesPlayed();
        if (minutesPlayed == 0) {
            return;
        }

        int rebounds = p.getOffensiveRebounds() + p.getDefensiveRebounds();
        if (rebounds == 0) {
            return;
        }

        double minutesMultiplier = minutesMultiplier(minutesPlayed);
        double potentialMultiplier = potentialMultiplier(player.getPotentielSkill());

        // Timing rebounds
        double timingDelta = BASE_REBOUND_GAIN
                * saturatingLog(rebounds)
                * minutesMultiplier
                * potentialMultiplier;

        player.setTimingRebond(applyDelta(player.getTimingRebond(), (int) Math.round(timingDelta)));

        // Aggressivity rebounds
        double aggressDelta = (BASE_REBOUND_GAIN * REBOUND_AGGRESS_DELTA_MULT)
                * saturatingLog(rebounds)
                * minutesMultiplier
                * potentialMultiplier;
        player.setAgressiviteRebond(applyDelta(player.getAgressiviteRebond(), (int) Math.round(aggressDelta)));
    }

    private static int applyDelta(int currentSkill, int delta) {
        return Math.clamp(currentSkill + delta, MIN_SKILL_VALUE, MAX_SKILL_VALUE);
    }

    private static double saturatingLog(int value) {
        if (value <= 0) {
            return 0.0;
        }
        return Math.log1p(value) / Math.log1p(ReboundingProgressionManager.REBOUNDS_SOFT_CAP);
    }

    private static double minutesMultiplier(int minutesPlayed) {
        return minutesPlayed / (double) MINUTES_REFERENCE;
    }

    private static double potentialMultiplier(int potential0to99) {
        double p = potential0to99 / (double) MAX_SKILL_VALUE;
        return POTENTIAL_MULT_BASE + POTENTIAL_MULT_RANGE * p;
    }
}

