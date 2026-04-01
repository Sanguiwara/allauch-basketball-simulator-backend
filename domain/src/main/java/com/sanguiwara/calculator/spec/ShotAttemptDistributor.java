package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;

/**
 * Shared implementation for distributing shot attempts across active players based on per-player intensity.
 *
 * <p>Kept package-private on purpose: this is an internal detail of shot specifications.</p>
 */
final class ShotAttemptDistributor {

    private ShotAttemptDistributor() {
    }

    // Shared intensity constants: same values as the ones historically used by ThreePointSpecification.
    private static final double USAGE_NORMALIZATION_DIVISOR = 30.0;
    private static final double AGGRESSIVENESS_NORMALIZATION_DIVISOR = 99.0;
    private static final double NORMALIZED_USAGE_COEFFICIENT = 0.90;
    private static final double NORMALIZED_AGGRESSIVENESS_COEFFICIENT = 0.10;
    private static final int TOTAL_MINUTES_FOR_A_PLAYER = 40;

    static void distributeAttempts(
            GamePlan team,
            int attempts,
            ToDoubleFunction<InGamePlayer> usageGetter,
            ObjDoubleConsumer<InGamePlayer> contributionSetter,
            ToDoubleFunction<InGamePlayer> contributionGetter,
            ObjDoubleConsumer<InGamePlayer> weightSetter,
            ToDoubleFunction<InGamePlayer> weightGetter,
            Consumer<InGamePlayer> attemptIncrementer,
            Random random
    ) {
        List<InGamePlayer> players = team.getActivePlayers();

        double totalIntensity = 0.0;
        for (InGamePlayer inGamePlayer : players) {
            double intensity = computeIntensity(inGamePlayer, usageGetter);
            totalIntensity += intensity;
            contributionSetter.accept(inGamePlayer, intensity);
        }

        for (InGamePlayer inGamePlayer : players) {
            double weight = contributionGetter.applyAsDouble(inGamePlayer) / totalIntensity;
            weightSetter.accept(inGamePlayer, weight);
        }

        for (int i = 0; i < attempts; i++) {
            InGamePlayer shooter = pickShooter(players, weightGetter, random);
            attemptIncrementer.accept(shooter);
        }
    }

    /**
     * Intensity formula inspired by ThreePointSpecification:
     * intensity = (usageCoeff * normalizedUsage + aggrCoeff * normalizedAggressiveness) * minutesFactor
     * Note: all specs share the same formula & constants by design.
     */
    private static double computeIntensity(InGamePlayer inGamePlayer,ToDoubleFunction<InGamePlayer> usageGetter ) {
        double normalizedUsage = usageGetter.applyAsDouble(inGamePlayer) / USAGE_NORMALIZATION_DIVISOR;
        double normalizedAggressiveness = inGamePlayer.getPlayer().getAgressivite() / AGGRESSIVENESS_NORMALIZATION_DIVISOR;
        double minutesFactor = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_A_PLAYER;

        return (NORMALIZED_USAGE_COEFFICIENT * normalizedUsage
                + NORMALIZED_AGGRESSIVENESS_COEFFICIENT * normalizedAggressiveness)
                * minutesFactor;
    }

    private static InGamePlayer pickShooter(
            List<InGamePlayer> potentialShooters,
            ToDoubleFunction<InGamePlayer> weightGetter,
            Random random
    ) {
        double total = 0.0;
        for (InGamePlayer p : potentialShooters) {
            total += weightGetter.applyAsDouble(p);
        }

        double r = random.nextDouble() * total;
        for (InGamePlayer p : potentialShooters) {
            r -= weightGetter.applyAsDouble(p);
            if (r <= 0.0) {
                return p;
            }
        }
        return null;
    }
}
