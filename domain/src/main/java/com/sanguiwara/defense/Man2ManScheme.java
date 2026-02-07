package com.sanguiwara.defense;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;

public abstract class Man2ManScheme implements DefensiveScheme {

    protected static final double OFF_SPEED_WEIGHT = 0.15;
    protected static final double OFF_SIZE_WEIGHT = 0.05;
    protected static final double OFF_ENDURANCE_WEIGHT = 0.05;
    protected static final double OFF_PASSING_WEIGHT = 0.10;
    protected static final double OFF_IQ_WEIGHT = 0.23;
    protected static final double OFF_HANDLING_WEIGHT = 0.10;
    protected static final double OFF_3PT_WEIGHT = 0.05;
    protected static final double OFF_2PT_WEIGHT = 0.05;
    protected static final double OFF_FINISH_WEIGHT = 0.05;
    protected static final double OFF_FLOATER_WEIGHT = 0.025;
    protected static final double DEFAULT_CONTRIBUTION = 10.0;
    protected static final double DEF_SPEED_WEIGHT_INDIV = 0.15;
    protected static final double DEF_SIZE_WEIGHT_INDIV = 0.05;
    protected static final double DEF_EXTERIOR_WEIGHT_INDIV = 0.35;
    protected static final double DEF_ENDURANCE_WEIGHT_INDIV = 0.05;
    protected static final double DEF_IQ_WEIGHT_INDIV = 0.10;
    protected static final double DEF_STEAL_WEIGHT_INDIV = 0.10;
    protected static final double MIN_INDIVIDUAL_ADVANTAGE = -5.0;
    protected static final double MAX_INDIVIDUAL_ADVANTAGE = 20.0;

    protected static final int TOTAL_MINUTES_FOR_TEAM = 200;


    @Override
    public double getOffensiveTeamPlaymakingScore(GamePlan offenseTeam, GamePlan defenseTeam) {
        return offenseTeam.getActivePlayers().stream()
                .mapToDouble(homePlayer -> {
                    Player visitorPlayer = defenseTeam.getMatchups().get(homePlayer.getPlayer());
                    if (visitorPlayer != null) {
                        return getPlayerScoreAgainstMan2Man(homePlayer, visitorPlayer);
                    } else {
                        homePlayer.setPlaymakingContribution(DEFAULT_CONTRIBUTION);
                        return DEFAULT_CONTRIBUTION;
                    }
                })
                .sum();
    }


    protected double getPlayerScoreAgainstMan2Man(InGamePlayer inGameOff, Player def) {
        Player off = inGameOff.getPlayer();

        double offScore = getIndividualPlaymakingOffScore(off);

        double defScore = getIndividualPlaymakingDefScore(def);


        double adv = offScore - defScore;
        double advantage = Math.clamp(adv, MIN_INDIVIDUAL_ADVANTAGE, MAX_INDIVIDUAL_ADVANTAGE);
        double minutesShare = (double) inGameOff.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
        double individualPlayMakingScore = offScore * minutesShare;
        inGameOff.setPlaymakingContribution(individualPlayMakingScore);

        return advantage;
    }

    private static double getIndividualPlaymakingDefScore(Player def) {
        return DEF_SPEED_WEIGHT_INDIV * def.getSpeed() +
                DEF_SIZE_WEIGHT_INDIV * def.getSize() +
                DEF_EXTERIOR_WEIGHT_INDIV * def.getDefExterieur()
                + DEF_ENDURANCE_WEIGHT_INDIV * def.getEndurance()
                + DEF_IQ_WEIGHT_INDIV * def.getBasketballIqDef()
                + DEF_STEAL_WEIGHT_INDIV * def.getSteal();
    }

    private static double getIndividualPlaymakingOffScore(Player off) {
        return OFF_SPEED_WEIGHT * off.getSpeed() +
                OFF_SIZE_WEIGHT * off.getSize()
                + OFF_ENDURANCE_WEIGHT * off.getEndurance() +
                OFF_PASSING_WEIGHT * off.getPassingSkills() +
                OFF_IQ_WEIGHT * off.getBasketballIqOff() +
                OFF_HANDLING_WEIGHT * off.getBallhandling() +
                OFF_3PT_WEIGHT * off.getTir3Pts() +
                OFF_2PT_WEIGHT * off.getTir2Pts()
                + OFF_FINISH_WEIGHT * off.getFinitionAuCercle()
                + OFF_FLOATER_WEIGHT * off.getFloater();
    }

}
