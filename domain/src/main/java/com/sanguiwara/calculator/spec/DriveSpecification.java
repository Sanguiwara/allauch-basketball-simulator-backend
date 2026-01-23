package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.result.DriveResult;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
public class DriveSpecification implements ShotSpec<DriveEvent, DriveResult> {
    private static final double ASSIST_BONUS_PCT = 0.15;

    // Success Pct Constants
    private static final double BASE_DRIVE_SUCCESS = 0.10;
    private static final double FINITION_CERCLE_WEIGHT = 0.35;
    private static final double FLOATER_WEIGHT = 0.20;
    private static final double ADVANTAGE_IMPACT_COEFFICIENT = 0.80;
    private static final double ADVANTAGE_DIVISOR = 50.0;
    private static final double MIN_SUCCESS_PCT = 0.05;
    private static final double MAX_SUCCESS_PCT = 0.95;

    // Attempts Sampling Constants
    private static final int USAGE_THRESHOLD = 10;
    private static final double USAGE_DIVISOR = 20.0;
    private static final double AGGRESSIVENESS_DIVISOR = 100.0;
    private static final double INTENSITY_USAGE_WEIGHT = 0.55;
    private static final double INTENSITY_AGGR_WEIGHT = 0.45;

    // Offensive Score Weights
    private static final double OFF_SPEED_WEIGHT = 0.20;
    private static final double OFF_SIZE_WEIGHT = 0.10;
    private static final double OFF_ENDURANCE_WEIGHT = 0.05;
    private static final double OFF_BALLHANDLING_WEIGHT = 0.20;
    private static final double OFF_FINITION_WEIGHT = 0.50;
    private static final double OFF_FLOATER_WEIGHT = 0.20;
    private static final double OFF_IQ_WEIGHT = 0.05;

    // Defensive Score Weights
    private static final double DEF_SPEED_WEIGHT = 0.25;
    private static final double DEF_SIZE_WEIGHT = 0.40;
    private static final double DEF_EXTERIEUR_WEIGHT = 0.45;
    private static final double DEF_ENDURANCE_WEIGHT = 0.10;
    private static final double DEF_IQ_WEIGHT = 0.10;
    private static final double DEF_STEAL_WEIGHT = 0.10;
    private static final double DEF_POSTE_WEIGHT = 0.10;
    private final Random random;



    @Override
    public void distributeShotAttempts(GamePlan team){
        double totalWeight = 0.0;
        for (InGamePlayer inGamePlayer : team.getActivePlayers()) {
            double usage01 = (inGamePlayer.getUsageShoot() - USAGE_THRESHOLD) / USAGE_DIVISOR;
            double aggr01 = inGamePlayer.getPlayer().agressivite() / AGGRESSIVENESS_DIVISOR;
            double intensity = INTENSITY_USAGE_WEIGHT * usage01 + INTENSITY_AGGR_WEIGHT * aggr01;
            totalWeight += intensity;
            inGamePlayer.setDriveContribution(intensity);
        }
        for (InGamePlayer inGamePlayer : team.getActivePlayers()) {
            double weight = inGamePlayer.getDriveContribution() / totalWeight;
            inGamePlayer.setDriveWeight(
                    weight);
        }

        for(int i =0; i< team.getDriveAttempts(); i++){
            InGamePlayer shooter = pickShooter(team.getActivePlayers());
            shooter.addDrive();
        }

    }

    @Override
    public double computePct(InGamePlayer off, double advantage, boolean isAssistedShot) {
        Player attacker = off.getPlayer();
        double assistBonusPct = isAssistedShot ? ASSIST_BONUS_PCT : 0.0;

        double base = BASE_DRIVE_SUCCESS + (attacker.finitionAuCercle() / 100.0) * FINITION_CERCLE_WEIGHT
                + (attacker.floater() / 100.0) * FLOATER_WEIGHT;

        double advPct = (advantage / ADVANTAGE_DIVISOR) * ADVANTAGE_IMPACT_COEFFICIENT;

        return clamp(base + advPct + assistBonusPct);


    }

    @Override
    public double evaluateMatchupAdvantage(Player attacker, Player defender) {
        double offScore =
                OFF_SPEED_WEIGHT * attacker.speed()
                        + OFF_SIZE_WEIGHT * attacker.size()
                        + OFF_ENDURANCE_WEIGHT * attacker.endurance()
                        + OFF_BALLHANDLING_WEIGHT * attacker.ballhandling()
                        + OFF_FINITION_WEIGHT * attacker.finitionAuCercle()
                        + OFF_FLOATER_WEIGHT * attacker.floater()
                        + OFF_IQ_WEIGHT * attacker.basketballIqOff();

        double defScore =
                DEF_SPEED_WEIGHT * defender.speed()
                        + DEF_SIZE_WEIGHT * defender.size()
                        + DEF_EXTERIEUR_WEIGHT * defender.defExterieur()
                        + DEF_ENDURANCE_WEIGHT * defender.endurance()
                        + DEF_IQ_WEIGHT * defender.basketballIqDef()
                        + DEF_STEAL_WEIGHT * defender.steal()
                        + DEF_POSTE_WEIGHT * defender.defPoste();

        return offScore - defScore;
        //TODO Clamp?
    }

    @Override
    public int getAttempts(InGamePlayer shooter) {
        return shooter.getDriveAttempts();
    }

    @Override
    public DriveEvent create(InGamePlayer inGamePlayer, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage) {
        inGamePlayer.recordDrive(made);
        return new DriveEvent(inGamePlayer.getPlayer().id(), shotNumber, assisted, assisterId, pct, made, advantage);
    }

    @Override
    public DriveResult createResult(int attempts, int made, List<DriveEvent> events) {
        return new DriveResult(attempts, made, 0, events);
    }

    @Override
    public DriveResult empty() {
        return DriveResult.empty();
    }

    @Override
    public DriveResult combine(DriveResult a, DriveResult b) {
        return DriveResult.combine(a, b);
    }

    private static double clamp(double v) {
        return Math.max(DriveSpecification.MIN_SUCCESS_PCT, Math.min(DriveSpecification.MAX_SUCCESS_PCT, v));
    }

    public InGamePlayer pickShooter( List<InGamePlayer> potentialShooters) {
        double total = 0.0;
        for (InGamePlayer p : potentialShooters) {
            total += p.getDriveWeight();
        }
        InGamePlayer playerToReturn = null;

        double r = random.nextDouble() * total;
        for (InGamePlayer p : potentialShooters) {
            r -=  p.getDriveWeight();
            if (r <= 0.0) {
                playerToReturn = p;
                return playerToReturn;
            }
        }
        return playerToReturn;

    }

}
