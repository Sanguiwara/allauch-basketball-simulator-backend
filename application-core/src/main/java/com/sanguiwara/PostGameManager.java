package com.sanguiwara;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.PlayerProgressionDelta;
import com.sanguiwara.type.ShotType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostGameManager {

    private static final int MAX_EGO_FOR_IMPACT = 99;
    private static final int MAX_MORALE_IMPACT = 20;
    private static final int MIN_MORALE = 0;
    private static final int MAX_MORALE = 100;
    private static final int MINUTES_OK_THRESHOLD = 10;
    private static final int MINUTES_REFERENCE = 20;

    private static final double MATCH_RATING_NEUTRAL = 5.0;
    private static final double FG_PCT_NEUTRAL = 0.45;

    private static final int MAX_SKILL_VALUE = 99;
    private static final int MAX_USAGE_PER_CATEGORY = 30;
    private static final int USAGE_CATEGORIES_COUNT = 3;
    private static final int DEFAULT_USAGE_PER_CATEGORY = 10;

    private static final int MADE_SOFT_CAP = 6;
    private static final int REBOUNDS_SOFT_CAP = 12;
    private static final int STOCKS_SOFT_CAP = 6;


    private static final double SHOOTING_MAKE_BONUS = 2.0;
    private static final double BASE_REBOUND_GAIN = 5;
    private static final double BASE_STEAL_GAIN = 5;
    private static final double BASE_BLOCK_GAIN = 1.9;

    private static final double REBOUND_AGGRESS_DELTA_MULT = 0.85;
    private static final double PROTECT_RIM_DELTA_MULT = 0.75;

    private static final int MAX_INACTIVITY_SKILL_DECAY = 1;

    private static final double EGO_VOLATILITY_BASE = 0.6;
    private static final double EGO_VOLATILITY_RANGE = 1.0;

    private static final int MAX_MORALE_MINUTES_PENALTY = 12;
    private static final int MAX_MORALE_RATING_IMPACT = 12;

    private static final double POTENTIAL_MULT_BASE = 0.6;
    private static final double POTENTIAL_MULT_RANGE = 1.2;

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


    // --- Shooting volume curve calibration (PATCH) ---
    private static final int SHOOTING_VOLUME_NO_REGRESSION_FROM = 4;   // >=4 : no negative volume delta
    private static final double SHOOTING_VOLUME_REGRESSION_TAU = 1.2;  // how fast regression vanishes
    private static final double SHOOTING_VOLUME_MAX_AT_15 = 10;       // target at 15 attempts (tune 2.0..3.0)
    private static final double SHOOTING_VOLUME_EXPONENT = 2.2;        // tuned so at 8 attempts ~ +1

    public void applyPostGameEffects(Game game) {

        GamePlan winningGamePlan;
        GamePlan losingGamePlan;

        if (homeTeamWon(game)) {
            winningGamePlan = game.getHomeGamePlan();
            losingGamePlan = game.getAwayGamePlan();
        } else {
            winningGamePlan = game.getAwayGamePlan();
            losingGamePlan = game.getHomeGamePlan();
        }
        applyLosingEffect(losingGamePlan);
        applyWinningEffect(winningGamePlan);
        applyProgressionForGamePlan(winningGamePlan);
        applyProgressionForGamePlan(losingGamePlan);
    }

    /**
     * Same behavior as {@link #applyPostGameEffects(Game)}, but also returns deltas for each player.
     * Persistence is orchestrated elsewhere (transaction boundary in application layer).
     */
    public List<PlayerProgression> applyPostGameEffectsAndReturnsPlayersProgression(Game game) {
        List<InGamePlayer> all = new ArrayList<>();
        all.addAll(game.getHomeGamePlan().getActivePlayers());
        all.addAll(game.getAwayGamePlan().getActivePlayers());

        Map<UUID, Player> beforeByPlayerId = new HashMap<>(all.size());
        for (InGamePlayer inGamePlayer : all) {
            Player player = inGamePlayer.getPlayer();
            beforeByPlayerId.put(player.getId(), player.snapshotPlayer());
        }

        applyPostGameEffects(game);
        List<PlayerProgression> progressionList = new ArrayList<>();

        for (InGamePlayer inGamePlayer : all) {
            Player playerAfterProgress = inGamePlayer.getPlayer();
            Player playerBeforeProgress = beforeByPlayerId.get(playerAfterProgress.getId());
            PlayerProgressionDelta delta = PlayerProgressionDelta.between(playerBeforeProgress, playerAfterProgress);
            progressionList.add(new PlayerProgression(playerAfterProgress.getId(), game.getId(), delta));
        }

        return progressionList;
    }


    private void applyProgressionForGamePlan(GamePlan gamePlan) {

        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {

            applyMoraleFromPerformance(inGamePlayer);
            applyProgression(inGamePlayer);

        }
    }

    private void applyMoraleFromPerformance(InGamePlayer inGamePlayer) {
        double matchRating = computeMatchRating(inGamePlayer);

        var player = inGamePlayer.getPlayer();

        double egoVolatility = egoVolatilityMultiplier(player.getEgo());
        int minutesPenalty = computeMinutesPenalty(inGamePlayer.getMinutesPlayed(), egoVolatility);
        int ratingDelta = computeMoraleDeltaFromRating(matchRating, egoVolatility);
        player.setMorale(player.getMorale() + minutesPenalty + ratingDelta);
        inGamePlayer.setMatchRating(matchRating);

    }

    private int computeMinutesPenalty(int minutesPlayed, double egoVolatility) {
        if (minutesPlayed >= MINUTES_OK_THRESHOLD) {
            return 0;
        }
        double shortfall = (MINUTES_OK_THRESHOLD - minutesPlayed) / (double) MINUTES_OK_THRESHOLD;
        double rawPenalty = -MAX_MORALE_MINUTES_PENALTY * shortfall * egoVolatility;
        return (int) Math.round(rawPenalty);
    }

    private int computeMoraleDeltaFromRating(double matchRating, double egoVolatility) {
        double centered = (matchRating - MATCH_RATING_NEUTRAL) / MATCH_RATING_NEUTRAL;
        double rawDelta = centered * MAX_MORALE_RATING_IMPACT * egoVolatility;
        return (int) Math.round(rawDelta);
    }

    private double computeMatchRating(InGamePlayer p) {
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

        int usageIntensity =  p.getUsageShoot() + p.getUsageDrive() + p.getUsagePost() / MAX_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT;

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



    private void applyProgression(InGamePlayer inGamePlayer) {
        var player = inGamePlayer.getPlayer();
        int minutesPlayed = inGamePlayer.getMinutesPlayed();

        // Inactivity decay (minutes low => skills slowly regress, capped).
        if (minutesPlayed < MINUTES_OK_THRESHOLD) {
            applyInactivityFactor(minutesPlayed, player);
        }


        // Performance-based progression/regression.
        applyShootingSkillProgression(inGamePlayer);
        applyReboundingProgression(inGamePlayer);
        applyStocksProgression(inGamePlayer);
    }

    private void applyInactivityFactor(int minutesPlayed, Player player) {
        double inactivity = (MINUTES_OK_THRESHOLD - minutesPlayed) / (double) MINUTES_OK_THRESHOLD;
        // For any minutesPlayed < MINUTES_OK_THRESHOLD, apply a small deterministic decay (max 1 point).
        // Using ceil ensures minutes=9 still decays by -1, while minutes=10 yields 0.
        int decay = -(int) Math.ceil(MAX_INACTIVITY_SKILL_DECAY * inactivity);
        player.setTir3Pts(player.getTir3Pts() + decay);
        player.setTir2Pts(player.getTir2Pts() + decay);
        player.setFinitionAuCercle(player.getFinitionAuCercle() + decay);
        player.setTimingRebond(player.getTimingRebond() + decay);
        player.setAgressiviteRebond(player.getAgressiviteRebond() + decay);
        player.setSteal(player.getSteal() + decay);
        player.setTimingBlock(player.getTimingBlock() + decay);
        player.setProtectionCercle(player.getProtectionCercle() + decay);
    }

    private void applyShootingSkillProgression(InGamePlayer p) {
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
            double makeBonus = SHOOTING_MAKE_BONUS * saturatingLog(made, MADE_SOFT_CAP);
            double rawDelta = volumeDelta + makeBonus;

            double scaledDelta = rawDelta
                    * minutesMult
                    * potentialMult;

            // Only change here: widen clamp to allow +2..+3 outcomes from volume if you want.

            int delta = (int) Math.round(scaledDelta);
            switch (shotType) {
                case THREE_POINT -> player.setTir3Pts(Math.clamp(player.getTir3Pts() + delta, 0, MAX_SKILL_VALUE));
                case TWO_POINT -> player.setTir2Pts(Math.clamp(player.getTir2Pts() + delta, 0, MAX_SKILL_VALUE));
                case DRIVE -> {
                    player.setFinitionAuCercle(Math.clamp(player.getFinitionAuCercle() + delta, 0, MAX_SKILL_VALUE));
                    player.setFloater(Math.clamp(player.getFloater() + delta, 0, MAX_SKILL_VALUE));

                }


            }

        }
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


    private void applyReboundingProgression(InGamePlayer p) {
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
                * saturatingLog(rebounds, REBOUNDS_SOFT_CAP)
                * minutesMultiplier
                * potentialMultiplier;

        player.setTimingRebond(player.getTimingRebond() + (int) Math.round(timingDelta));

        // Aggressivity rebounds
        double aggressDelta = (BASE_REBOUND_GAIN * REBOUND_AGGRESS_DELTA_MULT)
                * saturatingLog(rebounds, REBOUNDS_SOFT_CAP)
                * minutesMultiplier
                * potentialMultiplier;
        player.setAgressiviteRebond(player.getAgressiviteRebond() + (int) Math.round(aggressDelta));
    }

    private void applyStocksProgression(InGamePlayer p) {
        var player = p.getPlayer();
        int minutesPlayed = p.getMinutesPlayed();
        if (minutesPlayed == 0) {
            return;
        }

        double minutesMult = minutesMultiplier(minutesPlayed);
        double potentialMult = potentialMultiplier(player.getPotentielSkill());

        int steals = p.getSteals();
        if (steals > 0) {
            int current = player.getSteal();
            double delta = BASE_STEAL_GAIN
                    * saturatingLog(steals, STOCKS_SOFT_CAP)
                    * minutesMult
                    * potentialMult;
            player.setSteal(player.getSteal() + (int) Math.round(delta));
        }

        int blocks = p.getBlocks();
        if (blocks > 0) {
            int timingBlock = player.getTimingBlock();
            double timingDelta = BASE_BLOCK_GAIN
                    * saturatingLog(blocks, STOCKS_SOFT_CAP)
                    * minutesMult
                    * potentialMult
                    * diminishingMultiplier(timingBlock);
            player.setTimingBlock(player.getTimingBlock() + (int) Math.round(timingDelta));

            int protect = player.getProtectionCercle();
            double protectDelta = (BASE_BLOCK_GAIN * PROTECT_RIM_DELTA_MULT)
                    * saturatingLog(blocks, STOCKS_SOFT_CAP)
                    * minutesMult
                    * potentialMult
                    * diminishingMultiplier(protect);
            player.setProtectionCercle(player.getProtectionCercle() + (int) Math.round(protectDelta));
        }
    }

    private void applyWinningEffect(GamePlan winningGamePlan) {

        for (InGamePlayer inGamePlayer : winningGamePlan.getActivePlayers()) {
            var player = inGamePlayer.getPlayer();
            int impact = moraleImpactFromEgo(player.getEgo());
            player.setMorale(player.getMorale() + impact);
        }
    }

    private void applyLosingEffect(GamePlan losingGamePlan) {

        for (InGamePlayer inGamePlayer : losingGamePlan.getActivePlayers()) {
            var player = inGamePlayer.getPlayer();
            int impact = moraleImpactFromEgo(player.getEgo());
            player.setMorale(player.getMorale() - impact);
        }
    }

    private int moraleImpactFromEgo(int ego) {
        return (ego * MAX_MORALE_IMPACT) / MAX_EGO_FOR_IMPACT;
    }


    private boolean homeTeamWon(Game game) {
        int homeScore = game.getGameResult().homeScore().totalPoints();
        int awayScore = game.getGameResult().awayScore().totalPoints();
        return homeScore > awayScore;
    }



    private static int usageIntensityNeutral() {
        return  (DEFAULT_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT) / MAX_USAGE_PER_CATEGORY * USAGE_CATEGORIES_COUNT;
    }

    private static double saturatingLog(int value, int softCap) {
        if (value <= 0) {
            return 0.0;
        }
        return Math.log1p(value) / Math.log1p(softCap);
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

    private static double egoVolatilityMultiplier(int ego0to99) {
        double e = (double) ego0to99 /  MAX_EGO_FOR_IMPACT;
        return EGO_VOLATILITY_BASE + EGO_VOLATILITY_RANGE * e;
    }

}
