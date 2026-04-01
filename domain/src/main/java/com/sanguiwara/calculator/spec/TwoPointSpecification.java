package com.sanguiwara.calculator.spec;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.ShotContext;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.Target;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.TwoPointShootingResult;
import com.sanguiwara.type.ShotType;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
public class TwoPointSpecification implements ShotSpec<TwoPointShotEvent, TwoPointShootingResult> {
    private static final double ASSIST_BONUS_PCT = 0.15;

    // Constants for Shot Percentage Calculation
    private static final double TWO_POINT_SHOT_COEFF = 0.15;
    private static final double MATCHUP_COEFFICIENT = 0.40;
    private static final double MIN_SHOT_PCT = 0.10;
    private static final double MAX_SHOT_PCT = 0.85;

    // Constants for Offensive Score
    private static final double OFF_SPEED_COEFF = 0.08;
    private static final double OFF_SIZE_COEFF = 0.22;
    private static final double OFF_ENDURANCE_COEFF = 0.12;
    private static final double OFF_FINITION_AU_CERCLE = 0.15;
    private static final double OFF_2PTS_COEFF = 0.28;
    private static final double OFF_IQ_COEFF = 0.15;

    // Constants for Defensive Score
    private static final double DEF_INTERIOR_POST_COEFF = 0.27;
    private static final double DEF_SPEED_COEFF = 0.10;
    private static final double DEF_SIZE_COEFF = 0.28;
    private static final double DEF_ENDURANCE_COEFF = 0.12;
    private static final double DEF_IQ_COEFF = 0.18;
    private static final double DEF_STEAL_COEFF = 0.05;

    // Constants for Attempts Sampling
    // Intensity distribution is handled by ShotAttemptDistributor with shared constants across ShotSpec.
    public static final double MAX_MATCHUP_ADVANTAGE = 50.0;
    private final Random random;
    private final BadgeEngine badgeEngine;


    @Override
    public void distributeShotAttempts(GamePlan team) {
        ShotAttemptDistributor.distributeAttempts(
                team,
                team.getMidRangeAttempts(),
                InGamePlayer::getUsagePost,
                InGamePlayer::setTwoPointContribution,
                InGamePlayer::getTwoPointContribution,
                InGamePlayer::setTwoPointWeight,
                InGamePlayer::getTwoPointWeight,
                InGamePlayer::addTwoPointShot,
                random
        );

    }

    @Override
    public double computePct(InGamePlayer shooter, double matchupAdvantage, boolean isAssistedShot) {

        double assistBonusPct = isAssistedShot ? ASSIST_BONUS_PCT : 0.0;
        double base =  (shooter.getPlayer().getTir2Pts() / 100.0) * TWO_POINT_SHOT_COEFF ;
        double scaledMatchupAdvantageImpact = (matchupAdvantage / MAX_MATCHUP_ADVANTAGE) * MATCHUP_COEFFICIENT;
        double pct = base + scaledMatchupAdvantageImpact + assistBonusPct;
        pct = badgeEngine.apply(shooter.getPlayer(), BadgeType.TWO_POINT, Target.SHOT_PCT, pct,
                ShotContext.forShot(ShotType.TWO_POINT, isAssistedShot, matchupAdvantage));
        return Math.clamp(pct, MIN_SHOT_PCT, MAX_SHOT_PCT);

    }


    public double getDefensiveScoreForAShot(Player defender) {
        double score = DEF_SPEED_COEFF * defender.getSpeed()
                + DEF_SIZE_COEFF * defender.getSize()
                + DEF_ENDURANCE_COEFF * defender.getEndurance()
                + DEF_IQ_COEFF * defender.getBasketballIqDef()
                + DEF_STEAL_COEFF * defender.getSteal()
                + DEF_INTERIOR_POST_COEFF * defender.getDefPoste();
        return badgeEngine.apply(defender, BadgeType.DEF_EXTER, Target.DEFENSE_SCORE, score, ShotContext.empty());
    }

    @Override
    public double getPlayerScoreForAShot(Player attacker) {
        return OFF_SPEED_COEFF * attacker.getSpeed()
                + OFF_SIZE_COEFF * attacker.getSize()
                + OFF_ENDURANCE_COEFF * attacker.getEndurance()
                + OFF_FINITION_AU_CERCLE * attacker.getFinitionAuCercle()
                + OFF_2PTS_COEFF * attacker.getTir2Pts()
                + OFF_IQ_COEFF * attacker.getBasketballIqOff();
    }

    @Override
    public ShotType getShotType() {
        return ShotType.TWO_POINT;
    }

    @Override
    public int getAttempts(InGamePlayer shooter) {
        return shooter.getTwoPointAttempts();
    }

    @Override
    public TwoPointShotEvent create(InGamePlayer shooter, int shotNumber, boolean assisted, UUID assisterId, double pct, boolean made, double advantage, boolean blocked) {
        shooter.recordTwoPointShot(made);
        return new TwoPointShotEvent(shooter.getPlayer().getId(), shotNumber, assisted, assisterId, pct, made, advantage, blocked, ShotType.TWO_POINT);
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
        return TwoPointShootingResult.combine(a, b);
    }

    @Override
    public double getBlockProbabilityCoefficient() {
        return 0.6;
    }


}
