package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.ShotContext;
import com.sanguiwara.badges.Target;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BlockCalculator {
    private static final int TOTAL_MINUTES_FOR_TEAM = 200;

    public static final double MIN_BLOCK_PROBABILITY = 0.01;
    public static final double MAX_BLOCK_PROBABILITY = 0.40;
    public static final double MAX_SCORE = 100.0;

    private static final double SIZE_WEIGHT = 0.30;
    private static final double AGRESSIVITE_WEIGHT = 0.05;
    private static final double PHYSIQUE_WEIGHT = 0.10;
    private static final double BBIQ_DEF_WEIGHT = 0.15;
    private static final double TIMING_BLOCK_WEIGHT = 0.30;
    private static final double ENDURANCE_WEIGHT = 0.10;

    private final BadgeEngine badgeEngine;

    public double populateGamePlanWithBlockScore(GamePlan gamePlan) {

        double homeBlockScore = 0.0;

        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {
            double playerBlockScore = getPlayerBlockScore(inGamePlayer) * ((double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM);
            inGamePlayer.setBlockContribution(playerBlockScore);
            homeBlockScore += playerBlockScore ;
        }
        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {
            inGamePlayer.setBlockWeight(inGamePlayer.getBlockContribution()/homeBlockScore);
        }
        return scoreToProbability(homeBlockScore);
    }

    private double getPlayerBlockScore(InGamePlayer inGamePlayer) {
        Player player = inGamePlayer.getPlayer();
        double score = SIZE_WEIGHT * player.getSize()
                + AGRESSIVITE_WEIGHT * player.getAgressivite()
                + PHYSIQUE_WEIGHT * player.getPhysique()
                + BBIQ_DEF_WEIGHT * player.getBasketballIqDef()
                + TIMING_BLOCK_WEIGHT * player.getTimingBlock()
                + ENDURANCE_WEIGHT * player.getEndurance();

        return badgeEngine.apply(player, BadgeType.BLOCK, Target.BLOCK_SCORE, score, ShotContext.empty());
    }
    public static double scoreToProbability(double score) {
        return MIN_BLOCK_PROBABILITY +  (score / MAX_SCORE) * (MAX_BLOCK_PROBABILITY - MIN_BLOCK_PROBABILITY);
    }




}
