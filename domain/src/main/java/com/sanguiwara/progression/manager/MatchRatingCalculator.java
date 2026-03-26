package com.sanguiwara.progression.manager;

import com.sanguiwara.baserecords.InGamePlayer;

/**
 * Computes a post-game match rating in [0..10] from the in-game boxscore and usage inputs.
 * Kept separate so it can be mocked in tests without reaching into morale logic.
 */
public class MatchRatingCalculator {

    static final int MINUTES_REFERENCE = 20;

    public static final double MATCH_RATING_NEUTRAL = 5.0;
    private static final double FG_PCT_NEUTRAL = 0.45;

    private static final int MAX_USAGE_PER_CATEGORY = 30;
    private static final int USAGE_CATEGORIES_COUNT = 3;
    private static final int DEFAULT_USAGE_PER_CATEGORY = 10;

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

    public double compute(InGamePlayer p) {
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

        int usageIntensity =
                p.getUsageShoot()
                        + p.getUsageDrive()
                        + p.getUsagePost() / MAX_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT;

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
        return (DEFAULT_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT)
                / MAX_USAGE_PER_CATEGORY
                * USAGE_CATEGORIES_COUNT;
    }
}

