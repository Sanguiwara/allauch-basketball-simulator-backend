package com.sanguiwara.progression.manager;

import com.sanguiwara.baserecords.InGamePlayer;

/**
 * Computes a post-game match rating in [0..10] from the in-game boxscore and usage inputs.
 * Kept separate so it can be mocked in tests without reaching into morale logic.
 */
public class MatchRatingCalculator {

    public static final double MATCH_RATING_NEUTRAL = 5.0;
    private static final double MATCH_RATING_MAX = 10.0;
    private static final double FG_PCT_NEUTRAL = 0.45;
    private static final double FG_PCT_EXCELLENT = 0.70;

    private static final int MAX_USAGE_PER_CATEGORY = 30;
    private static final int USAGE_CATEGORIES_COUNT = 3;
    private static final int DEFAULT_USAGE_PER_CATEGORY = 10;
    private static final int MAX_TOTAL_USAGE = MAX_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT;
    private static final int DEFAULT_TOTAL_USAGE = DEFAULT_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT;

    private static final double RATING_POINTS_WEIGHT = 2.0;
    private static final double RATING_FG_PCT_WEIGHT = 1.5;
    private static final double RATING_REBOUND_WEIGHT = 0.9;
    private static final double RATING_STEAL_WEIGHT = 0.8;
    private static final double RATING_BLOCK_WEIGHT = 0.7;
    private static final double RATING_ASSIST_WEIGHT = 0.4;
    private static final double RATING_USAGE_WEIGHT = 0.5;

    private static final double RATING_POINTS_PER_MIN_NEUTRAL = 0.50;
    private static final double RATING_POINTS_PER_MIN_EXCELLENT = 1.00;
    private static final double RATING_REB_PER_MIN_NEUTRAL = 0.25;
    private static final double RATING_REB_PER_MIN_EXCELLENT = 0.50;
    private static final double RATING_STEALS_PER_MIN_NEUTRAL = 0.04;
    private static final double RATING_STEALS_PER_MIN_EXCELLENT = 0.08;
    private static final double RATING_BLOCKS_PER_MIN_NEUTRAL = 0.03;
    private static final double RATING_BLOCKS_PER_MIN_EXCELLENT = 0.06;
    private static final double RATING_ASSISTS_PER_MIN_NEUTRAL = 0.12;
    private static final double RATING_ASSISTS_PER_MIN_EXCELLENT = 0.24;

    public double compute(InGamePlayer p) {
        validateBoxscore(p);

        int minutes = p.getMinutesPlayed();
        if (minutes == 0) {
            return 0.0;
        }

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

        double weightedScore = 0.0;
        double totalWeight = 0.0;

        weightedScore += RATING_POINTS_WEIGHT * componentScore(
                pointsPerMin,
                RATING_POINTS_PER_MIN_NEUTRAL,
                RATING_POINTS_PER_MIN_EXCELLENT
        );
        totalWeight += RATING_POINTS_WEIGHT;

        if (fga > 0) {
            weightedScore += RATING_FG_PCT_WEIGHT * componentScore(
                    fgm / (double) fga,
                    FG_PCT_NEUTRAL,
                    FG_PCT_EXCELLENT
            );
            totalWeight += RATING_FG_PCT_WEIGHT;
        }

        weightedScore += RATING_REBOUND_WEIGHT * componentScore(
                rebPerMin,
                RATING_REB_PER_MIN_NEUTRAL,
                RATING_REB_PER_MIN_EXCELLENT
        );
        totalWeight += RATING_REBOUND_WEIGHT;

        weightedScore += RATING_STEAL_WEIGHT * componentScore(
                stealsPerMin,
                RATING_STEALS_PER_MIN_NEUTRAL,
                RATING_STEALS_PER_MIN_EXCELLENT
        );
        totalWeight += RATING_STEAL_WEIGHT;

        weightedScore += RATING_BLOCK_WEIGHT * componentScore(
                blocksPerMin,
                RATING_BLOCKS_PER_MIN_NEUTRAL,
                RATING_BLOCKS_PER_MIN_EXCELLENT
        );
        totalWeight += RATING_BLOCK_WEIGHT;

        weightedScore += RATING_ASSIST_WEIGHT * componentScore(
                assistsPerMin,
                RATING_ASSISTS_PER_MIN_NEUTRAL,
                RATING_ASSISTS_PER_MIN_EXCELLENT
        );
        totalWeight += RATING_ASSIST_WEIGHT;

        weightedScore += RATING_USAGE_WEIGHT * componentScore(
                p.getUsageShoot() + p.getUsageDrive() + p.getUsagePost(),
                DEFAULT_TOTAL_USAGE,
                MAX_TOTAL_USAGE
        );
        totalWeight += RATING_USAGE_WEIGHT;

        return MATCH_RATING_MAX * (weightedScore / totalWeight);
    }

    private static double componentScore(double value, double neutralValue, double excellentValue) {
        if (value < 0.0) {
            throw new IllegalArgumentException("rating component value must be non-negative, got=" + value);
        }
        if (neutralValue <= 0.0 || excellentValue <= neutralValue) {
            throw new IllegalArgumentException(
                    "rating component scale must satisfy 0 < neutral < excellent"
            );
        }
        if (value <= neutralValue) {
            return value / (2.0 * neutralValue);
        }
        if (value >= excellentValue) {
            return 1.0;
        }
        return MATCH_RATING_NEUTRAL / MATCH_RATING_MAX
                + ((value - neutralValue) / (excellentValue - neutralValue))
                * (MATCH_RATING_NEUTRAL / MATCH_RATING_MAX);
    }

    private static void validateBoxscore(InGamePlayer p) {
        requireNonNegative("minutesPlayed", p.getMinutesPlayed());
        requireNonNegative("points", p.getPoints());
        requireNonNegative("fga", p.getFga());
        requireNonNegative("fgm", p.getFgm());
        requireNonNegative("offensiveRebounds", p.getOffensiveRebounds());
        requireNonNegative("defensiveRebounds", p.getDefensiveRebounds());
        requireNonNegative("steals", p.getSteals());
        requireNonNegative("blocks", p.getBlocks());
        requireNonNegative("assists", p.getAssists());
        requireUsageInRange("usageShoot", p.getUsageShoot());
        requireUsageInRange("usageDrive", p.getUsageDrive());
        requireUsageInRange("usagePost", p.getUsagePost());

        if (p.getFgm() > p.getFga()) {
            throw new IllegalArgumentException(
                    "fgm must be lower than or equal to fga, got fgm=" + p.getFgm() + ", fga=" + p.getFga()
            );
        }
    }

    private static void requireNonNegative(String fieldName, int value) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must be non-negative, got=" + value);
        }
    }

    private static void requireUsageInRange(String fieldName, int value) {
        if (value < 0 || value > MAX_USAGE_PER_CATEGORY) {
            throw new IllegalArgumentException(
                    fieldName + " must be in [0.." + MAX_USAGE_PER_CATEGORY + "], got=" + value
            );
        }
    }
}

