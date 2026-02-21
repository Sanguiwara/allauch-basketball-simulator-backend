package com.sanguiwara;

import com.sanguiwara.baserecords.InGamePlayer;

final class StocksProgressionManager {

    private static final int MIN_SKILL_VALUE = 1;
    private static final int MAX_SKILL_VALUE = 99;

    private static final int MINUTES_REFERENCE = 20;
    private static final double POTENTIAL_MULT_BASE = 0.6;
    private static final double POTENTIAL_MULT_RANGE = 1.2;

    private static final int STOCKS_SOFT_CAP = 6;
    private static final double BASE_STEAL_GAIN = 5;
    private static final double BASE_BLOCK_GAIN = 1.9;
    private static final double PROTECT_RIM_DELTA_MULT = 0.75;

    void applyStocksProgression(InGamePlayer p) {
        var player = p.getPlayer();
        int minutesPlayed = p.getMinutesPlayed();
        if (minutesPlayed == 0) {
            return;
        }

        double minutesMult = minutesMultiplier(minutesPlayed);
        double potentialMult = potentialMultiplier(player.getPotentielSkill());

        int steals = p.getSteals();
        if (steals > 0) {
            double delta = BASE_STEAL_GAIN
                    * saturatingLog(steals)
                    * minutesMult
                    * potentialMult;
            player.setSteal(applyDelta(player.getSteal(), (int) Math.round(delta)));
        }

        int blocks = p.getBlocks();
        if (blocks > 0) {
            int timingBlock = player.getTimingBlock();
            double timingDelta = BASE_BLOCK_GAIN
                    * saturatingLog(blocks)
                    * minutesMult
                    * potentialMult
                    * diminishingMultiplier(timingBlock);
            player.setTimingBlock(applyDelta(player.getTimingBlock(), (int) Math.round(timingDelta)));

            int protect = player.getProtectionCercle();
            double protectDelta = (BASE_BLOCK_GAIN * PROTECT_RIM_DELTA_MULT)
                    * saturatingLog(blocks)
                    * minutesMult
                    * potentialMult
                    * diminishingMultiplier(protect);
            player.setProtectionCercle(applyDelta(player.getProtectionCercle(), (int) Math.round(protectDelta)));
        }
    }

    private static int applyDelta(int currentSkill, int delta) {
        return Math.clamp(currentSkill + delta, MIN_SKILL_VALUE, MAX_SKILL_VALUE);
    }

    private static double saturatingLog(int value) {
        if (value <= 0) {
            return 0.0;
        }
        return Math.log1p(value) / Math.log1p(StocksProgressionManager.STOCKS_SOFT_CAP);
    }

    private static double minutesMultiplier(int minutesPlayed) {
        return minutesPlayed / (double) MINUTES_REFERENCE;
    }

    private static double diminishingMultiplier(int currentSkill0to99) {
        return 1.0 - currentSkill0to99 / (double) MAX_SKILL_VALUE;
    }

    private static double potentialMultiplier(int potential0to99) {
        double p = potential0to99 / (double) MAX_SKILL_VALUE;
        return POTENTIAL_MULT_BASE + POTENTIAL_MULT_RANGE * p;
    }
}

