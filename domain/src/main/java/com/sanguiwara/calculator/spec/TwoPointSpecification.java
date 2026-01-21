package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.TwoPointShootingResult;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
public class TwoPointSpecification implements ShotSpec<TwoPointShotEvent, TwoPointShootingResult> {
    private static final int MIN_ATTEMPTS = 0;
    private static final int MAX_ATTEMPTS = 20;
    private static final double MIN_ADVANTAGE = -50;
    private static final double MAX_ADVANTAGE = 50;
    private static final double ASSIST_BONUS_PCT = 0.15;

    // Constants for Shot Percentage Calculation
    private static final double BASE_SHOT_PCT = 0.30;
    private static final double TWO_POINT_SHOT_COEFF = 0.40;
    private static final double SIZE_PCT_COEFF = 0.05;
    private static final double MATCHUP_COEFFICIENT = 0.38;
    private static final double MIN_SHOT_PCT = 0.10;
    private static final double MAX_SHOT_PCT = 0.85;

    // Constants for Offensive Score
    private static final double OFF_SPEED_COEFF = 0.05;
    private static final double OFF_SIZE_COEFF = 0.25;
    private static final double OFF_ENDURANCE_COEFF = 0.15;
    private static final double OFF_BALLHANDLING_COEFF = 0.10;
    private static final double OFF_FINISH_AT_RIM_COEFF = 0.15;
    private static final double OFF_IQ_COEFF = 0.10;

    // Constants for Defensive Score
    private static final double DEF_INTERIOR_POST_COEFF = 0.35;
    private static final double DEF_SPEED_COEFF = 0.10;
    private static final double DEF_SIZE_COEFF = 0.40;
    private static final double DEF_ENDURANCE_COEFF = 0.15;
    private static final double DEF_IQ_COEFF = 0.25;
    private static final double DEF_STEAL_COEFF = 0.05;

    // Constants for Attempts Sampling
    private static final double USAGE_BASE_OFFSET = 10.0;
    private static final double USAGE_DIVISOR = 20.0;
    private static final double USAGE_WEIGHT = 0.65;
    private static final double AGGR_WEIGHT = 0.35;
    private static final double ATTEMPTS_BASE_VAL = 2.0;
    private static final double ATTEMPTS_MULT_VAL = 8.0;
    private static final double STD_DEV_BASE = 1.4;
    private static final double STD_DEV_MULT = 0.8;
    public static final double MAX_MATCHUP_ADVANTAGE = 50.0;
    public static final double AGGRESSIVENESS_DIVISOR = 100.0;
    private final Random random;


    @Override
    public int sampleAttempts(InGamePlayer shooter) {
        double usage01 = (shooter.getUsageShoot() - USAGE_BASE_OFFSET) / USAGE_DIVISOR;
        double aggr01 = shooter.getPlayer().agressivite() / AGGRESSIVENESS_DIVISOR;

        double intensity = USAGE_WEIGHT * usage01 + AGGR_WEIGHT * aggr01;
        double expected = ATTEMPTS_BASE_VAL + ATTEMPTS_MULT_VAL * intensity;
        double std = STD_DEV_BASE + STD_DEV_MULT * intensity;

        int sampled = (int) Math.round(expected + random.nextGaussian() * std);
        return Math.max(MIN_ATTEMPTS, Math.min(MAX_ATTEMPTS, sampled));
    }

    @Override
    public double computePct(InGamePlayer shooter, double matchupAdvantage, boolean isAssistedShot) {

        double assistBonusPct = isAssistedShot ? ASSIST_BONUS_PCT : 0.0;

        double base = BASE_SHOT_PCT + (shooter.getPlayer().tir2Pts() / 100.0) * TWO_POINT_SHOT_COEFF
                + (shooter.getPlayer().size() / 100.0) * SIZE_PCT_COEFF;

        double scaledMatchupAdvantageImpact = (matchupAdvantage / MAX_MATCHUP_ADVANTAGE) * MATCHUP_COEFFICIENT;


        return clamp(base + scaledMatchupAdvantageImpact + assistBonusPct);
    }

    @Override
    public double evaluateMatchupAdvantage(Player attacker, Player defender) {
        double offScore =
                OFF_SPEED_COEFF * attacker.speed()
                        + OFF_SIZE_COEFF * attacker.size()
                        + OFF_ENDURANCE_COEFF * attacker.endurance()
                        + OFF_BALLHANDLING_COEFF * attacker.ballhandling()
                        + OFF_FINISH_AT_RIM_COEFF * attacker.finitionAuCercle()
                        + OFF_IQ_COEFF * attacker.basketballIqOff();

        double defScore =
                DEF_SPEED_COEFF * defender.speed()
                        + DEF_SIZE_COEFF * defender.size()
                        + DEF_ENDURANCE_COEFF * defender.endurance()
                        + DEF_IQ_COEFF * defender.basketballIqDef()
                        + DEF_STEAL_COEFF * defender.steal()
                        + DEF_INTERIOR_POST_COEFF * defender.defPoste();
        return offScore - defScore;
        //TODO Ajouter un clamp?
    }

    @Override
    public TwoPointShotEvent create(InGamePlayer shooter, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage) {
        shooter.recordTwoPointShot(made);
        return new TwoPointShotEvent(shooter.getPlayer().id(), shotNumber, assisted, assisterId, pct, made, advantage);
    }

    @Override
    public TwoPointShootingResult createResult(int attempts, int made, List<TwoPointShotEvent> events) {
        return new TwoPointShootingResult(attempts, made, events);
    }

    @Override
    public TwoPointShootingResult empty() {
        return TwoPointShootingResult.empty();
    }

    @Override
    public TwoPointShootingResult combine(TwoPointShootingResult a, TwoPointShootingResult b) {
        return TwoPointShootingResult.combine(a,b);
    }

    private static double clamp(double v) {
        return Math.max(TwoPointSpecification.MIN_SHOT_PCT, Math.min(TwoPointSpecification.MAX_SHOT_PCT, v));
    }

}
