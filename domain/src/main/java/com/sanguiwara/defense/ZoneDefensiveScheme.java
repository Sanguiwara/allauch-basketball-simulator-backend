package com.sanguiwara.defense;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.ShotContext;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.Target;
import com.sanguiwara.calculator.PlayerScoreCalculator;
import com.sanguiwara.calculator.spec.ShotSpec;
import com.sanguiwara.type.ShotType;

import java.util.EnumMap;
import java.util.List;

public abstract class ZoneDefensiveScheme implements DefensiveScheme {
    protected static final int TOTAL_MINUTES_FOR_TEAM = 200;

    protected final BadgeEngine badgeEngine;

    protected ZoneDefensiveScheme(BadgeEngine badgeEngine) {
        this.badgeEngine = badgeEngine;
    }

    @Override
    public double calculateAdvantageForAPlayer(InGamePlayer attacker, GamePlan defensiveGamePlan, ShotSpec<?, ?> shotSpec) {
        Player player = attacker.getPlayer();
        return Math.clamp(
                shotSpec.getPlayerScoreForAShot(player) * shotCoefficients().get(shotSpec.getShotType()) - calculateShootDefenseScore(defensiveGamePlan),
                -50,
                50
        );
    }

    public abstract double getPlayerDefensiveScoreAgainstShooting(Player player);

    public abstract EnumMap<ShotType, Double> shotCoefficients();

    protected double calculateShootDefenseScore(GamePlan defensiveGamePlan) {
        List<InGamePlayer> activePlayers = defensiveGamePlan.getActivePlayers();
        double score = 0.0;
        for (InGamePlayer inGamePlayer : activePlayers) {
            double minutesWeight = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
            Player player = inGamePlayer.getPlayer();
            double playerScore = getPlayerDefensiveScoreAgainstShooting(player);
            playerScore = badgeEngine.apply(player, BadgeType.DEF_EXTER, Target.DEFENSE_SCORE, playerScore, ShotContext.empty());
            score += minutesWeight * playerScore;
        }
        return score;
        //TODO Trouver un moyen propre pour ne pas recalculer a chaque fois
    }


    @Override
    public double getOffensiveTeamPlaymakingScore(GamePlan offenseTeam, GamePlan defenseTeam) {
        double playmakingScore = 0.0;

        for (InGamePlayer inGamePlayer : offenseTeam.getActivePlayers()) {
            double minutesShare = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
            double individualPlayMakingScore = getOffensivePlayerPlaymakingScore(inGamePlayer) * minutesShare;
            individualPlayMakingScore = badgeEngine.apply(inGamePlayer.getPlayer(), BadgeType.ASSIST, Target.PLAYMAKING_CONTRIBUTION,
                    individualPlayMakingScore, ShotContext.empty());
            inGamePlayer.setPlaymakingContribution(individualPlayMakingScore);
            playmakingScore += individualPlayMakingScore;
        }
        for (InGamePlayer inGamePlayer : offenseTeam.getActivePlayers()) {
            inGamePlayer.setAssistWeight(inGamePlayer.getPlaymakingContribution() / playmakingScore);
        }
        double defenseScore = getDefensiveTeamPlaymakingScore(defenseTeam);
        playmakingScore -= defenseScore;
        return playmakingScore;

    }

    protected double getDefensiveTeamPlaymakingScore(GamePlan defenseTeam) {
        double defenseScore = 0.0;
        for (InGamePlayer inGamePlayer : defenseTeam.getActivePlayers()) {
            Player def = inGamePlayer.getPlayer();
            double minutesShare = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;

            double defScore = getDefensivePlayerPlaymakingScore(def) * minutesShare;
            defenseScore += defScore;
        }
        return defenseScore;
    }

    private static double getDefensivePlayerPlaymakingScore(Player def) {
        return PlayerScoreCalculator.calculateZonePlaymakingDefScore(def);
    }


    protected double getOffensivePlayerPlaymakingScore(InGamePlayer inGamePlayer) {
        return PlayerScoreCalculator.calculateZonePlaymakingOffScore(inGamePlayer.getPlayer());
    }


}
