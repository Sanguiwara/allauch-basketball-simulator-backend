package com.sanguiwara;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;

final class MoraleProgressionManager {

    private static final int MAX_EGO_FOR_IMPACT = 99;
    private static final int MAX_MORALE_IMPACT = 20;
    private static final int MIN_MORALE = 1;
    private static final int MAX_MORALE = 100;
    private static final int MINUTES_OK_THRESHOLD = 10;
    private static final int MINUTES_REFERENCE = 20;

    private static final double MATCH_RATING_NEUTRAL = 5.0;
    private static final double FG_PCT_NEUTRAL = 0.45;

    private static final int MAX_USAGE_PER_CATEGORY = 30;
    private static final int USAGE_CATEGORIES_COUNT = 3;
    private static final int DEFAULT_USAGE_PER_CATEGORY = 10;

    private static final double EGO_VOLATILITY_BASE = 0.6;
    private static final double EGO_VOLATILITY_RANGE = 1.0;

    private static final int MAX_MORALE_MINUTES_PENALTY = 12;
    private static final int MAX_MORALE_RATING_IMPACT = 12;

    private static final double RATING_POINTS_WEIGHT = 2.0;
    private static final double RATING_FG_PCT_WEIGHT = 1.5;
    private static final double RATING_REBOUND_WEIGHT = 0.9;
    private static final double RATING_STEAL_WEIGHT = 0.8;
    private static final double RATING_BLOCK_WEIGHT = 0.7;
    private static final double RATING_ASSIST_WEIGHT = 0.4;
    private static final double RATING_USAGE_WEIGHT = 0.5;

    private static final double RATING_POINTS_PER_MIN_NEUTRAL = 0.50;
    private static final double RATING_POINTS_PER_MIN_SCALE = 0.50;
    private static final double RATING_FG_PCT_SCALE = 0.25;
    private static final double RATING_REB_PER_MIN_NEUTRAL = 0.25;
    private static final double RATING_REB_PER_MIN_SCALE = 0.25;
    private static final double RATING_STEALS_PER_MIN_NEUTRAL = 0.04;
    private static final double RATING_STEALS_PER_MIN_SCALE = 0.04;
    private static final double RATING_BLOCKS_PER_MIN_NEUTRAL = 0.03;
    private static final double RATING_BLOCKS_PER_MIN_SCALE = 0.03;
    private static final double RATING_ASSISTS_PER_MIN_NEUTRAL = 0.12;
    private static final double RATING_ASSISTS_PER_MIN_SCALE = 0.12;

    void applyMoraleFromPerformance(InGamePlayer inGamePlayer) {
        double matchRating = computeMatchRating(inGamePlayer);

        var player = inGamePlayer.getPlayer();

        double egoVolatility = egoVolatilityMultiplier(player.getEgo());
        int minutesPenalty = computeMinutesPenalty(inGamePlayer.getMinutesPlayed(), egoVolatility);
        int ratingDelta = computeMoraleDeltaFromRating(matchRating, egoVolatility);
        player.setMorale(applyDelta(player.getMorale() , minutesPenalty + ratingDelta));
        inGamePlayer.setMatchRating(matchRating);
    }

    void applyWinningEffect(GamePlan winningGamePlan) {
        for (InGamePlayer inGamePlayer : winningGamePlan.getActivePlayers()) {
            var player = inGamePlayer.getPlayer();
            int impact = moraleImpactFromEgo(player.getEgo());
            player.setMorale(player.getMorale() + impact);
        }
    }

    void applyLosingEffect(GamePlan losingGamePlan) {
        for (InGamePlayer inGamePlayer : losingGamePlan.getActivePlayers()) {
            var player = inGamePlayer.getPlayer();
            int impact = moraleImpactFromEgo(player.getEgo());
            player.setMorale(player.getMorale() - impact);
        }
    }

    private static int moraleImpactFromEgo(int ego) {
        return (ego * MAX_MORALE_IMPACT) / MAX_EGO_FOR_IMPACT;
    }

    private static int computeMinutesPenalty(int minutesPlayed, double egoVolatility) {
        if (minutesPlayed >= MINUTES_OK_THRESHOLD) {
            return 0;
        }
        double shortfall = (MINUTES_OK_THRESHOLD - minutesPlayed) / (double) MINUTES_OK_THRESHOLD;
        double rawPenalty = -MAX_MORALE_MINUTES_PENALTY * shortfall * egoVolatility;
        return (int) Math.round(rawPenalty);
    }

    private static double computeMatchRating(InGamePlayer p) {
        int minutes = p.getMinutesPlayed();
        if (minutes == 0) {
            return 0.0;
        }

        double minutesNorm = minutes / (double) MINUTES_REFERENCE;

        int points = p.getPoints();
        int fga = p.getFga();
        int fgm = p.getFgm();
        int rebounds = p.getOffensiveRebounds() + p.getDefensiveRebounds();
        int steals = p.getSteals();
        int blocks = p.getBlocks();
        int assists = p.getAssists();

        double pointsPerMin = points / (double) minutes;
        double rebPerMin = rebounds / (double) minutes;
        double stealsPerMin = steals / (double) minutes;
        double blocksPerMin = blocks / (double) minutes;
        double assistsPerMin = assists / (double) minutes;

        // If no attempts, we don't punish efficiency; volume already captured by points.
        double fgPct = (fga == 0) ? FG_PCT_NEUTRAL : (fgm / (double) fga);

        int usageIntensity = p.getUsageShoot() + p.getUsageDrive() + p.getUsagePost() / MAX_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT;

        double usageNeutral = usageIntensityNeutral();
        double rating = MATCH_RATING_NEUTRAL;
        rating += RATING_POINTS_WEIGHT * ((pointsPerMin - RATING_POINTS_PER_MIN_NEUTRAL) / RATING_POINTS_PER_MIN_SCALE);
        rating += RATING_FG_PCT_WEIGHT * ((fgPct - FG_PCT_NEUTRAL) / RATING_FG_PCT_SCALE);
        rating += RATING_REBOUND_WEIGHT * ((rebPerMin - RATING_REB_PER_MIN_NEUTRAL) / RATING_REB_PER_MIN_SCALE);
        rating += RATING_STEAL_WEIGHT * ((stealsPerMin - RATING_STEALS_PER_MIN_NEUTRAL) / RATING_STEALS_PER_MIN_SCALE);
        rating += RATING_BLOCK_WEIGHT * ((blocksPerMin - RATING_BLOCKS_PER_MIN_NEUTRAL) / RATING_BLOCKS_PER_MIN_SCALE);
        rating += RATING_ASSIST_WEIGHT * ((assistsPerMin - RATING_ASSISTS_PER_MIN_NEUTRAL) / RATING_ASSISTS_PER_MIN_SCALE);
        rating += RATING_USAGE_WEIGHT * ((usageIntensity - usageNeutral) / usageNeutral);

        // If you barely play, your rating is dampened.
        rating = MATCH_RATING_NEUTRAL + (rating - MATCH_RATING_NEUTRAL) * minutesNorm;
        return rating;
    }

    private static int usageIntensityNeutral() {
        return (DEFAULT_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT) / MAX_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT;
    }

    private static double egoVolatilityMultiplier(int ego0to99) {
        double e = (double) ego0to99 / MAX_EGO_FOR_IMPACT;
        return EGO_VOLATILITY_BASE + EGO_VOLATILITY_RANGE * e;
    }

    private static int computeMoraleDeltaFromRating(double matchRating, double egoVolatility) {
        double centered = (matchRating - MATCH_RATING_NEUTRAL) / MATCH_RATING_NEUTRAL;
        double rawDelta = centered * MAX_MORALE_RATING_IMPACT * egoVolatility;
        return (int) Math.round(rawDelta);
    }

    private static int applyDelta(int currentSkill, int delta) {
        return Math.clamp(currentSkill + delta, MIN_MORALE, MAX_MORALE);
    }

}

