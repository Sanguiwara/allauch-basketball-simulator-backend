package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.result.ThreePointShootingResult;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;


@RequiredArgsConstructor
public class ThreePointSpecification implements ShotSpec<ThreePointShotEvent, ThreePointShootingResult> {
    private static final double BASE_THREE_POINT_PROBABILITY_COEFFICIENT = 0.35;
    private static final double ADVANTAGE_THREE_POINT_COEFFICIENT = 0.30;
    private static final double RATING_NORMALIZATION_DIVISOR = 100.0;
    private static final double ADVANTAGE_NORMALIZATION_DIVISOR = 50.0;

    private static final int USAGE_MIN_THRESHOLD = 10;
    private static final double USAGE_NORMALIZATION_DIVISOR = 20.0;
    private static final double AGGRESSIVENESS_NORMALIZATION_DIVISOR = 100.0;

    private static final double NORMALIZED_USAGE_COEFFICIENT = 0.70;
    private static final double NORMALIZED_AGGRESSIVENESS_COEFFICIENT = 0.30;

    private static final double EXPECTED_ATTEMPTS_BASE = 1.8;
    private static final double EXPECTED_ATTEMPTS_MULTIPLIER = 12.5;

    private static final double STD_DEV_BASE = 1.2;
    private static final double STD_DEV_MULTIPLIER = 0.8;

    private static final int MIN_ATTEMPTS = 0;
    private static final int MAX_ATTEMPTS = 20;

    private static final double SCORE_SPEED_WEIGHT_OFF = 0.05;
    private static final double SCORE_SIZE_WEIGHT_OFF = 0.05;
    private static final double SCORE_ENDURANCE_WEIGHT_OFF = 0.10;
    private static final double SCORE_RATING_WEIGHT_OFF = 0.80;
    private static final double SCORE_IQ_WEIGHT_OFF = 0.15;

    private static final double SCORE_SPEED_WEIGHT_DEF = 0.10;
    private static final double SCORE_SIZE_WEIGHT_DEF = 0.10;
    private static final double SCORE_DEF_EXT_WEIGHT = 0.55;
    private static final double SCORE_ENDURANCE_WEIGHT_DEF = 0.05;
    private static final double SCORE_IQ_WEIGHT_DEF = 0.10;

    private static final double ADVANTAGE_CLAMP_MIN = -50.0;
    private static final double ADVANTAGE_CLAMP_MAX = 50.0;
    private static final double ASSIST_BONUS_PCT = 0.15;
    private final Random random;


    @Override
    public int sampleAttempts(InGamePlayer shooter) {
        double usage01 = (shooter.getUsageShoot() - USAGE_MIN_THRESHOLD) / USAGE_NORMALIZATION_DIVISOR;
        double aggr01 = shooter.getPlayer().agressivite() / AGGRESSIVENESS_NORMALIZATION_DIVISOR;

        double intensity = NORMALIZED_USAGE_COEFFICIENT * usage01 + NORMALIZED_AGGRESSIVENESS_COEFFICIENT * aggr01;
        double expected = EXPECTED_ATTEMPTS_BASE + EXPECTED_ATTEMPTS_MULTIPLIER * intensity;
        double std = STD_DEV_BASE + STD_DEV_MULTIPLIER * intensity;

        int sampled = (int) Math.round(expected + random.nextGaussian() * std);
        return Math.max(MIN_ATTEMPTS, Math.min(MAX_ATTEMPTS, sampled));
    }

    @Override
    public double computePct(InGamePlayer shooter, double advantage, boolean isAssistedShot) {
        double assistBonusPct = isAssistedShot ? ASSIST_BONUS_PCT : 0.0;
        double basePct = (shooter.getPlayer().tir3Pts() / RATING_NORMALIZATION_DIVISOR) * BASE_THREE_POINT_PROBABILITY_COEFFICIENT;
        double advantagePct = (advantage / ADVANTAGE_NORMALIZATION_DIVISOR) * ADVANTAGE_THREE_POINT_COEFFICIENT;
        return basePct + advantagePct + assistBonusPct;
    }

    @Override
    public double evaluateMatchupAdvantage(Player attacker, Player defender) {
        double offScore =
                SCORE_SPEED_WEIGHT_OFF * attacker.speed() +
                        SCORE_SIZE_WEIGHT_OFF * attacker.size()
                        + SCORE_ENDURANCE_WEIGHT_OFF * attacker.endurance() +
                        SCORE_RATING_WEIGHT_OFF * attacker.tir3Pts() +
                        SCORE_IQ_WEIGHT_OFF * attacker.basketballIqOff();
        double defScore =
                SCORE_SPEED_WEIGHT_DEF * defender.speed() +
                        SCORE_SIZE_WEIGHT_DEF * defender.size() +
                        SCORE_DEF_EXT_WEIGHT * defender.defExterieur()
                        + SCORE_ENDURANCE_WEIGHT_DEF * defender.endurance() +
                        SCORE_IQ_WEIGHT_DEF * defender.basketballIqDef();
        double adv = offScore - defScore;

        return Math.max(ADVANTAGE_CLAMP_MIN, Math.min(ADVANTAGE_CLAMP_MAX, adv));
    }

    @Override
    public ThreePointShotEvent create(InGamePlayer shooter, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage) {
        shooter.recordThreePointShot(made);
        return new ThreePointShotEvent(shooter.getPlayer().id(), shotNumber, assisted, assisterId, pct, made, advantage);
    }


    @Override
    public ThreePointShootingResult createResult(int attempts, int made, List<ThreePointShotEvent> events) {
        return new ThreePointShootingResult(attempts, made, events);
    }

    @Override
    public ThreePointShootingResult empty() {
        return ThreePointShootingResult.empty();
    }

    @Override
    public ThreePointShootingResult combine(ThreePointShootingResult a, ThreePointShootingResult b) {
        return ThreePointShootingResult.combine(a, b);
    }
}
