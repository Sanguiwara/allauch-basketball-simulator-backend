package com.sanguiwara.calculator.spec;

import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.ShotContext;
import com.sanguiwara.badges.Target;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.type.ShotType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
public class DriveSpecification implements ShotSpec<DriveEvent, DriveResult> {
    private static final double ASSIST_BONUS_PCT = 0.15;

    // Success Pct Constants
    private static final double FINITION_CERCLE_WEIGHT = 0.15;
    private static final double FLOATER_WEIGHT = 0.10;
    private static final double ADVANTAGE_IMPACT_COEFFICIENT = 0.45;
    private static final double ADVANTAGE_DIVISOR = 50.0;
    private static final double MIN_SUCCESS_PCT = 0.15;
    private static final double MAX_SUCCESS_PCT = 0.95;

    // Attempts Sampling Constants
    // Intensity distribution is handled by ShotAttemptDistributor with shared constants across ShotSpec.

    // Offensive Score Weights
    private static final double OFF_SPEED_WEIGHT = 0.18;
    private static final double OFF_SIZE_WEIGHT = 0.08;
    private static final double OFF_ENDURANCE_WEIGHT = 0.05;
    private static final double OFF_BALLHANDLING_WEIGHT = 0.20;
    private static final double OFF_FINITION_WEIGHT = 0.35;
    private static final double OFF_FLOATER_WEIGHT = 0.10;
    private static final double OFF_IQ_WEIGHT = 0.04;

    // Defensive Score Weights
    private static final double DEF_SPEED_WEIGHT = 0.18;
    private static final double DEF_SIZE_WEIGHT = 0.22;
    private static final double DEF_EXTERIEUR_WEIGHT = 0.22;
    private static final double DEF_ENDURANCE_WEIGHT = 0.10;
    private static final double DEF_IQ_WEIGHT = 0.12;
    private static final double DEF_STEAL_WEIGHT = 0.10;
    private static final double DEF_POSTE_WEIGHT = 0.06;
    private final Random random;
    private final BadgeEngine badgeEngine;


    @Override
    public void distributeShotAttempts(GamePlan team) {
        ShotAttemptDistributor.distributeAttempts(
                team,
                team.getDriveAttempts(),
                InGamePlayer::getUsageDrive,
                InGamePlayer::setDriveContribution,
                InGamePlayer::getDriveContribution,
                InGamePlayer::setDriveWeight,
                InGamePlayer::getDriveWeight,
                InGamePlayer::addDrive,
                random
        );
    }

    @Override
    public double computePct(InGamePlayer off, double advantage, boolean isAssistedShot) {
        Player attacker = off.getPlayer();
        double assistBonusPct = isAssistedShot ? ASSIST_BONUS_PCT : 0.0;

        double base = (attacker.getFinitionAuCercle() / 100.0) * FINITION_CERCLE_WEIGHT
                + (attacker.getFloater() / 100.0) * FLOATER_WEIGHT;

        double advPct = (advantage / ADVANTAGE_DIVISOR) * ADVANTAGE_IMPACT_COEFFICIENT;

        double pct = base + advPct + assistBonusPct;
        pct = badgeEngine.apply(attacker, BadgeType.DRIVE, Target.SHOT_PCT, pct,
                ShotContext.forShot(ShotType.DRIVE, isAssistedShot, advantage));
        return clamp(pct);

    }


    @Override
    public double getDefensiveScoreForAShot(Player defender) {
        double score = DEF_SPEED_WEIGHT * defender.getSpeed()
                + DEF_SIZE_WEIGHT * defender.getSize()
                + DEF_EXTERIEUR_WEIGHT * defender.getDefExterieur()
                + DEF_ENDURANCE_WEIGHT * defender.getEndurance()
                + DEF_IQ_WEIGHT * defender.getBasketballIqDef()
                + DEF_STEAL_WEIGHT * defender.getSteal()
                + DEF_POSTE_WEIGHT * defender.getDefPoste();
        return badgeEngine.apply(defender, BadgeType.DEF_EXTER, Target.DEFENSE_SCORE, score, ShotContext.empty());
    }

    public double getPlayerScoreForAShot(Player attacker) {
        return OFF_SPEED_WEIGHT * attacker.getSpeed()
                + OFF_SIZE_WEIGHT * attacker.getSize()
                + OFF_ENDURANCE_WEIGHT * attacker.getEndurance()
                + OFF_BALLHANDLING_WEIGHT * attacker.getBallhandling()
                + OFF_FINITION_WEIGHT * attacker.getFinitionAuCercle()
                + OFF_FLOATER_WEIGHT * attacker.getFloater()
                + OFF_IQ_WEIGHT * attacker.getBasketballIqOff();
    }

    @Override
    public ShotType getShotType() {
        return ShotType.DRIVE;
    }


    @Override
    public int getAttempts(InGamePlayer shooter) {
        return shooter.getDriveAttempts();
    }

    @Override
    public DriveEvent create(InGamePlayer inGamePlayer, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage, boolean blocked) {
        inGamePlayer.recordDrive(made);
        return new DriveEvent(inGamePlayer.getPlayer().getId(), shotNumber, assisted, assisterId, pct, made, advantage, blocked, ShotType.DRIVE);
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

    @Override
    public double getBlockProbabilityCoefficient() {
        return 1;

    }


    private static double clamp(double v) {
        return Math.max(DriveSpecification.MIN_SUCCESS_PCT, Math.min(DriveSpecification.MAX_SUCCESS_PCT, v));
    }

}
