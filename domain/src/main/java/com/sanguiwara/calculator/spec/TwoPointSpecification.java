package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.GamePlan;
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
    public static final double MAX_MATCHUP_ADVANTAGE = 50.0;
    public static final double AGGRESSIVENESS_DIVISOR = 100.0;
    private final Random random;


   @Override
    public void distributeShotAttempts(GamePlan team){
        double totalWeight = 0.0;
        for (InGamePlayer inGamePlayer : team.getActivePlayers()) {
            double usage01 = (inGamePlayer.getUsagePost() - USAGE_BASE_OFFSET) / USAGE_DIVISOR;
            double aggr01 = inGamePlayer.getPlayer().agressivite() / AGGRESSIVENESS_DIVISOR;
            double intensity = USAGE_WEIGHT * usage01 + AGGR_WEIGHT * aggr01;
            totalWeight += intensity;
            inGamePlayer.setTwoPointContribution(intensity);
        }
        for (InGamePlayer inGamePlayer : team.getActivePlayers()) {
            double twoPointWeight = inGamePlayer.getTwoPointContribution() / totalWeight;
            inGamePlayer.setTwoPointWeight(
                    twoPointWeight);
        }

        for(int i =0; i< team.getMidRangeAttempts(); i++){
            InGamePlayer shooter = pickShooter(team.getActivePlayers());
            shooter.addTwoPointShot();
        }

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
    public int getAttempts(InGamePlayer shooter) {
        return shooter.getTwoPointAttempts();
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

    public InGamePlayer pickShooter( List<InGamePlayer> potentialShooters) {
        double total = 0.0;
        for (InGamePlayer p : potentialShooters) {
            total += p.getTwoPointWeight();
        }
        InGamePlayer playerToReturn = null;

        double r = random.nextDouble() * total;
        for (InGamePlayer p : potentialShooters) {
            r -=  p.getTwoPointWeight();
            if (r <= 0.0) {
                playerToReturn = p;
                return playerToReturn;
            }
        }
        return playerToReturn;

    }



}
