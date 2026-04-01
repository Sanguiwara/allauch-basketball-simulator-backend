package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.ShotContext;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.Target;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.type.ShotType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;


@RequiredArgsConstructor
public class ThreePointSpecification implements ShotSpec<ThreePointShotEvent, ThreePointShootingResult> {
    private static final double BASE_THREE_POINT_PROBABILITY_COEFFICIENT = 0.10;
    private static final double ADVANTAGE_THREE_POINT_COEFFICIENT = 0.35;
    private static final double RATING_NORMALIZATION_DIVISOR = 99.0;
    private static final double ADVANTAGE_NORMALIZATION_DIVISOR = 50.0;

    // Intensity distribution is handled by ShotAttemptDistributor with shared constants across ShotSpec.


    private static final double SCORE_SPEED_WEIGHT_OFF = 0.10;
    private static final double SCORE_SIZE_WEIGHT_OFF = 0.15;
    private static final double SCORE_ENDURANCE_WEIGHT_OFF = 0.10;
    private static final double SCORE_RATING_WEIGHT_OFF = 0.50;
    private static final double SCORE_IQ_WEIGHT_OFF = 0.15;

    private static final double SCORE_SPEED_WEIGHT_DEF = 0.10;
    private static final double SCORE_SIZE_WEIGHT_DEF = 0.10;
    private static final double SCORE_DEF_EXT_WEIGHT = 0.65;
    private static final double SCORE_ENDURANCE_WEIGHT_DEF = 0.05;
    private static final double SCORE_IQ_WEIGHT_DEF = 0.10;
    private static final double ASSIST_BONUS_PCT = 0.15;
    private final Random random;
    private final BadgeEngine badgeEngine;

    @Override
    public void distributeShotAttempts(GamePlan team) {
        ShotAttemptDistributor.distributeAttempts(
                team,
                team.getThreePointAttempts(),
                InGamePlayer::getUsageShoot,
                InGamePlayer::setThreePointContribution,
                InGamePlayer::getThreePointContribution,
                InGamePlayer::setThreePointWeight,
                InGamePlayer::getThreePointWeight,
                InGamePlayer::addThreePointShot,
                random
        );
        //TODO Bug possible: Si un joueur joue 1 minutes il peut quand meme avoir un gros usage+ aggressiveness et avoir trop de tirs
    }


    @Override
    public double computePct(InGamePlayer shooter, double advantage, boolean isAssistedShot) {
        double assistBonusPct = isAssistedShot ? ASSIST_BONUS_PCT : 0.0;
        double basePct = (shooter.getPlayer().getTir3Pts() / RATING_NORMALIZATION_DIVISOR) * BASE_THREE_POINT_PROBABILITY_COEFFICIENT;
        double advantagePct = (advantage / ADVANTAGE_NORMALIZATION_DIVISOR) * ADVANTAGE_THREE_POINT_COEFFICIENT;
        double pct = basePct + advantagePct + assistBonusPct;
        pct = badgeEngine.apply(shooter.getPlayer(), BadgeType.THREE_POINT, Target.SHOT_PCT, pct,
                ShotContext.forShot(ShotType.THREE_POINT, isAssistedShot, advantage));
        return Math.clamp(pct, 0.075, 0.95);
    }

    @Override
    public double getDefensiveScoreForAShot(Player defender) {
        double score = SCORE_SPEED_WEIGHT_DEF * defender.getSpeed() +
                SCORE_SIZE_WEIGHT_DEF * defender.getSize() +
                SCORE_DEF_EXT_WEIGHT * defender.getDefExterieur()
                + SCORE_ENDURANCE_WEIGHT_DEF * defender.getEndurance() +
                SCORE_IQ_WEIGHT_DEF * defender.getBasketballIqDef();
        return badgeEngine.apply(defender, BadgeType.DEF_EXTER, Target.DEFENSE_SCORE, score, ShotContext.empty());
    }


    @Override
    public double getPlayerScoreForAShot(Player attacker) {
        return SCORE_SPEED_WEIGHT_OFF * attacker.getSpeed() +
                SCORE_SIZE_WEIGHT_OFF * attacker.getSize()
                + SCORE_ENDURANCE_WEIGHT_OFF * attacker.getEndurance() +
                SCORE_RATING_WEIGHT_OFF * attacker.getTir3Pts() +
                SCORE_IQ_WEIGHT_OFF * attacker.getBasketballIqOff();
    }

    @Override
    public ShotType getShotType() {
        return ShotType.THREE_POINT;
    }

    @Override
    public int getAttempts(InGamePlayer shooter) {
        return shooter.getThreePointAttempt();
    }

    @Override
    public ThreePointShotEvent create(InGamePlayer shooter, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage, boolean blocked) {
        shooter.recordThreePointShot(made);
        return new ThreePointShotEvent(shooter.getPlayer().getId(), shotNumber, assisted, assisterId, pct, made, advantage, blocked, ShotType.THREE_POINT);
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

    @Override
    public double getBlockProbabilityCoefficient() {
        return 0.2;
    }



}
