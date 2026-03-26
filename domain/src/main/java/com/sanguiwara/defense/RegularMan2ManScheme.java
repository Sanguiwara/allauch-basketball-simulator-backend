package com.sanguiwara.defense;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.calculator.spec.ShotSpec;

public final class RegularMan2ManScheme extends Man2ManScheme {

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
        Player defender = defensiveGamePlan.getMatchups().get(attackerPlayer);

        double defensiveScore;
        if (defender == null) {
            defensiveScore = getAverageTeamDefensiveScore(defensiveGamePlan, shotSpec);
        } else {
            double defenderScore = shotSpec.getDefensiveScoreForAShot(defender) * 1.3;

            int attackerMinutes = attacker.getMinutesPlayed();

                int defenderMinutes = defensiveGamePlan.getActivePlayers().stream()
                        .filter(p -> p.getPlayer().equals(defender))
                        .mapToInt(InGamePlayer::getMinutesPlayed)
                        .findFirst()
                        .orElse(0);

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


    private double getAverageTeamDefensiveScore(GamePlan defensiveGamePlan, ShotSpec<?, ?> shotSpec) {


        double score = 0.0;
        for (InGamePlayer inGamePlayer : defensiveGamePlan.getActivePlayers()) {
            double minutesWeight = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
            score += minutesWeight * shotSpec.getDefensiveScoreForAShot(inGamePlayer.getPlayer()) * 0.75;
        }
        return score;
    }



}
