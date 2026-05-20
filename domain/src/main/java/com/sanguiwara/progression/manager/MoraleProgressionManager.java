package com.sanguiwara.progression.manager;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.progression.MoraleDeltaScaler;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class MoraleProgressionManager {

    private final MatchRatingCalculator matchRatingCalculator;

    public MoraleProgressionManager() {
        this(new MatchRatingCalculator());
    }

    public MoraleProgressionManager(MatchRatingCalculator matchRatingCalculator) {
        this.matchRatingCalculator = matchRatingCalculator;
    }
    private static final int MAX_EGO_FOR_IMPACT = 99;
    private static final int MAX_WIN_LOSS_MORALE_IMPACT = 3;
    private static final int MIN_MORALE = 1;
    private static final int MAX_MORALE = 100;
    private static final int MINUTES_OK_THRESHOLD = 15;
    private static final double EGO_VOLATILITY_BASE = 0.6;
    private static final double EGO_VOLATILITY_RANGE = 1.0;

    private static final int MAX_MORALE_MINUTES_PENALTY = 10;
    private static final int MAX_MORALE_RATING_IMPACT = 6;
    private static final int DID_NOT_PLAY_MORALE_PENALTY = 5;

    public void applyMoraleFromPerformance(InGamePlayer inGamePlayer) {
        double matchRating = matchRatingCalculator.compute(inGamePlayer);

        var player = inGamePlayer.getPlayer();

        double egoVolatility = egoVolatilityMultiplier(player.getEgo());
        int minutesPenalty = computeMinutesPenalty(inGamePlayer.getMinutesPlayed(), egoVolatility);
        int ratingDelta = computeMoraleDeltaFromRating(matchRating, egoVolatility);
        player.setMorale(applyDelta(player.getMorale(), minutesPenalty + ratingDelta));
        inGamePlayer.setMatchRating(matchRating);
    }

    public void applyWinningEffect(GamePlan winningGamePlan) {
        for (InGamePlayer inGamePlayer : winningGamePlan.getActivePlayers()) {
            var player = inGamePlayer.getPlayer();
            int impact = moraleImpactFromEgo(player.getEgo());
            player.setMorale(applyDelta(player.getMorale(), impact));
        }
    }

    public void applyLosingEffect(GamePlan losingGamePlan) {
        for (InGamePlayer inGamePlayer : losingGamePlan.getActivePlayers()) {
            var player = inGamePlayer.getPlayer();
            int impact = moraleImpactFromEgo(player.getEgo());
            player.setMorale(applyDelta(player.getMorale(), -impact));
        }
    }

    public void applyDidNotPlayPenalty(GamePlan gamePlan) {
        Set<UUID> activePlayerIds = gamePlan.getActivePlayers().stream()
                .map(InGamePlayer::getPlayer)
                .map(Player::getId)
                .collect(Collectors.toSet());

        for (Player player : gamePlan.getOwnerTeam().getPlayers()) {
            if (!activePlayerIds.contains(player.getId())) {
                player.setMorale(applyDidNotPlayPenalty(player.getMorale()));
            }
        }
    }

    private static int moraleImpactFromEgo(int ego) {
        return (ego * MAX_WIN_LOSS_MORALE_IMPACT) / MAX_EGO_FOR_IMPACT;
    }

    private static int computeMinutesPenalty(int minutesPlayed, double egoVolatility) {
        if (minutesPlayed >= MINUTES_OK_THRESHOLD) {
            return 0;
        }
        double shortfall = (MINUTES_OK_THRESHOLD - minutesPlayed) / (double) MINUTES_OK_THRESHOLD;
        double rawPenalty = -MAX_MORALE_MINUTES_PENALTY * shortfall * egoVolatility;
        int penalty = (int) Math.round(rawPenalty);
        return Math.min(penalty, -1);
    }

    private static double egoVolatilityMultiplier(int ego0to99) {
        double e = (double) ego0to99 / MAX_EGO_FOR_IMPACT;
        return EGO_VOLATILITY_BASE + EGO_VOLATILITY_RANGE * e;
    }

    private static int computeMoraleDeltaFromRating(double matchRating, double egoVolatility) {
        double centered = (matchRating - MatchRatingCalculator.MATCH_RATING_NEUTRAL) / MatchRatingCalculator.MATCH_RATING_NEUTRAL;
        double rawDelta = centered * MAX_MORALE_RATING_IMPACT * egoVolatility;
        return (int) Math.round(rawDelta);
    }

    private static int applyDelta(int currentMorale, int delta) {
        return MoraleDeltaScaler.applyDelta(currentMorale, delta, MIN_MORALE, MAX_MORALE);
    }

    private static int applyDidNotPlayPenalty(int currentMorale) {
        return Math.max(MIN_MORALE, currentMorale - DID_NOT_PLAY_MORALE_PENALTY);
    }
}
