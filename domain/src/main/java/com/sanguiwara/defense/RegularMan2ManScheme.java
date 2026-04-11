package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.ShotContext;
import com.sanguiwara.badges.Target;
import com.sanguiwara.calculator.spec.ShotSpec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RegularMan2ManScheme extends Man2ManScheme {

    private static final double OFF_SPEED_WEIGHT = 0.15;
    private static final double OFF_SIZE_WEIGHT = 0.05;
    private static final double OFF_ENDURANCE_WEIGHT = 0.05;
    private static final double OFF_PASSING_WEIGHT = 0.20;
    private static final double OFF_IQ_WEIGHT = 0.25;
    private static final double OFF_HANDLING_WEIGHT = 0.15;
    private static final double OFF_3PT_WEIGHT = 0.05;
    private static final double OFF_2PT_WEIGHT = 0.05;
    private static final double OFF_FINISH_WEIGHT = 0.03;
    private static final double OFF_FLOATER_WEIGHT = 0.02;
    private static final double DEF_SPEED_WEIGHT_INDIV = 0.15;
    private static final double DEF_SIZE_WEIGHT_INDIV = 0.08;
    private static final double DEF_EXTERIOR_WEIGHT_INDIV = 0.42;
    private static final double DEF_ENDURANCE_WEIGHT_INDIV = 0.05;
    private static final double DEF_IQ_WEIGHT_INDIV = 0.15;
    private static final double DEF_STEAL_WEIGHT_INDIV = 0.15;
    private static final double MIN_INDIVIDUAL_ADVANTAGE = -15;
    private static final double MAX_INDIVIDUAL_ADVANTAGE = 25;

    public RegularMan2ManScheme(BadgeEngine badgeEngine) {
        super(badgeEngine);
    }

    @Override
    public DefenseType type() {
        return DefenseType.MAN_TO_MAN;
    }

    @Override
    public double calculateAdvantageForAPlayer(InGamePlayer attacker, GamePlan defensiveGamePlan, ShotSpec<?, ?> shotSpec) {

        Player attackerPlayer = attacker.getPlayer();
        Player defender = defensiveGamePlan.getMatchups().defenderFor(attackerPlayer);

        double defensiveScore;
        if (defender == null) {
            defensiveScore = getAverageTeamDefensiveScore(defensiveGamePlan, shotSpec);
        } else {
            double defenderScore = shotSpec.getDefensiveScoreForAShot(defender) ;

            int attackerMinutes = attacker.getMinutesPlayed();
            int defenderMinutes = getMinutesPlayedFor(defensiveGamePlan, defender);

            if (attackerMinutes <= defenderMinutes) {
                defensiveScore = defenderScore;
            } else {
                double avgTeamDefensiveScore = getAverageTeamDefensiveScore(defensiveGamePlan, shotSpec);
                int extraMinutes = attackerMinutes - defenderMinutes;
                defensiveScore = (defenderScore * defenderMinutes + avgTeamDefensiveScore * extraMinutes) / attackerMinutes;
            }
        }


        double playerScoreForAShot = shotSpec.getPlayerScoreForAShot(attackerPlayer);
        return Math.clamp(playerScoreForAShot - defensiveScore, -50, 50);
    }

    @Override
    public double getOffensiveTeamPlaymakingScore(GamePlan offenseTeam, GamePlan defenseTeam) {
        double averageTeamDefensiveScore = getAverageTeamPlaymakingDefensiveScore(defenseTeam);
        return offenseTeam.getActivePlayers().stream()
                .mapToDouble(inGameOff -> {
                    if(inGameOff.getMinutesPlayed() == 0){
                        inGameOff.setPlaymakingContribution(0.0);
                        return 0.0;
                    }
                    Player off = inGameOff.getPlayer();
                    double offScore = getIndividualPlaymakingOffScore(off);

                    double minutesShare = (double) inGameOff.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
                    double baseOffensiveContribution = offScore * minutesShare;
                    double offensiveContribution = badgeEngine.apply(
                            off,
                            BadgeType.ASSIST,
                            Target.PLAYMAKING_CONTRIBUTION,
                            baseOffensiveContribution,
                            ShotContext.empty()
                    );
                    inGameOff.setPlaymakingContribution(offensiveContribution);

                    double effectiveOffScoreForAdvantage = offScore;
                    if (offensiveContribution != baseOffensiveContribution) {
                        effectiveOffScoreForAdvantage = (offensiveContribution / minutesShare);
                    }

                    Player defender = defenseTeam.getMatchups().defenderFor(off);
                    double effectiveDefensiveScore;
                    int defenderMinutes;
                    if (defender == null) {
                        effectiveDefensiveScore = averageTeamDefensiveScore;
                    } else {
                        double defenderScore = getIndividualPlaymakingDefScore(defender);
                        int attackerMinutes = inGameOff.getMinutesPlayed();
                        defenderMinutes = getMinutesPlayedFor(defenseTeam, defender);

                        if (attackerMinutes <= defenderMinutes) {
                            effectiveDefensiveScore = defenderScore;
                        } else {
                            int extraMinutes = attackerMinutes - defenderMinutes;
                            effectiveDefensiveScore = (defenderScore * defenderMinutes + averageTeamDefensiveScore * extraMinutes) / attackerMinutes;
                        }
                    }

                    double rawAdv = effectiveOffScoreForAdvantage - effectiveDefensiveScore;
                    double clampedAdv = Math.clamp(rawAdv, MIN_INDIVIDUAL_ADVANTAGE, MAX_INDIVIDUAL_ADVANTAGE);
                    //log.info("{} for {} (rawAdv: {})", clampedAdv, off.getName(), (rawAdv * 1.5) * minutesShare);
                    double v = clampedAdv * minutesShare;
                    log.info("{} for {} (rawAdv: {})", v, off.getName(), rawAdv);

                    return v;
                })
                .sum();
    }

    private static int getMinutesPlayedFor(GamePlan defensiveGamePlan, Player defender) {
        return defensiveGamePlan.getActivePlayers().stream()
                .filter(p -> p.getPlayer().equals(defender))
                .mapToInt(InGamePlayer::getMinutesPlayed)
                .findFirst()
                .orElse(0);
    }

    private static double getAverageTeamPlaymakingDefensiveScore(GamePlan defensiveGamePlan) {
        double score = 0.0;
        for (InGamePlayer inGamePlayer : defensiveGamePlan.getActivePlayers()) {
            double minutesWeight = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
            score += minutesWeight * getIndividualPlaymakingDefScore(inGamePlayer.getPlayer()) * 0.75;
        }
        return score;
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

    private double getAverageTeamDefensiveScore(GamePlan defensiveGamePlan, ShotSpec<?, ?> shotSpec) {


        double score = 0.0;
        for (InGamePlayer inGamePlayer : defensiveGamePlan.getActivePlayers()) {
            double minutesWeight = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
            score += minutesWeight * shotSpec.getDefensiveScoreForAShot(inGamePlayer.getPlayer()) * 0.75;
        }
        return score;
    }



}
