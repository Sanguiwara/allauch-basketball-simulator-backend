package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.ReboundContext;
import com.sanguiwara.badges.Target;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ReboundCalculator {
    public static final double MIN_OFFENSIVE_REBOUND_PROBABILITY = 0.00;
    public static final double MAX_OFFENSIVE_REBOUND_PROBABILITY = 0.50;

    public static final int TOTAL_MINUTES_FOR_TEAM = 200;

    private static final double ADVANTAGE_SHIFT = 1.0;
    private static final double ADVANTAGE_SCALE = 2.0;

    private final java.util.Random random;
    private final BadgeEngine badgeEngine;

    public int evaluateOffensiveReboundForTeam(GamePlan offenseGamePlan, GamePlan defenseGamePlan) {
        double reboundAdvantage = evaluateReboundAdvantage(offenseGamePlan, defenseGamePlan);

        double normalizedAdvantage = (reboundAdvantage + ADVANTAGE_SHIFT) / ADVANTAGE_SCALE;

        double offensiveReboundProbability =
                MIN_OFFENSIVE_REBOUND_PROBABILITY
                        + normalizedAdvantage * (MAX_OFFENSIVE_REBOUND_PROBABILITY - MIN_OFFENSIVE_REBOUND_PROBABILITY);

        log.debug("ReboundAdvantage for team: {} (normalized: {}), offensiveRebProb: {}",
                String.format("%.4f", reboundAdvantage),
                String.format("%.4f", normalizedAdvantage),
                String.format("%.4f", offensiveReboundProbability));

        int offensiveRebounds = 0;
        for (int i = 0; i < offenseGamePlan.getTotalShotNumber(); i++) {
            if (random.nextDouble() < offensiveReboundProbability) {
                InGamePlayer rebounder = pickRebounder(offenseGamePlan.getActivePlayers());
                rebounder.addOffensiveRebound();
                offensiveRebounds++;
                if (log.isDebugEnabled() && rebounder.getPlayer() != null) {
                    log.debug("Offensive rebound credited to: {}", rebounder.getPlayer().getName());
                }
            } else {
                InGamePlayer rebounder = pickRebounder(defenseGamePlan.getActivePlayers());
                if (rebounder != null) rebounder.addDefensiveRebound();
            }
        }

        log.info("Team recorded offensive rebounds: {} out of {} shots (p~={})",
                offensiveRebounds, offenseGamePlan.getTotalShotNumber(), String.format("%.3f", offensiveReboundProbability));

        return offensiveRebounds;
    }

    private double evaluateReboundAdvantage(GamePlan home, GamePlan visitor) {
        double homeReboundScore = populateTeamReboundScore(home, ReboundContext.offensive());
        double visitorReboundScore = populateTeamReboundScore(visitor, ReboundContext.defensive());

        log.info("Rebound scores for home: {}, visitor: {}", homeReboundScore, visitorReboundScore);

        return (homeReboundScore - visitorReboundScore) / (homeReboundScore + visitorReboundScore);
    }

    private double populateTeamReboundScore(GamePlan gamePlan, ReboundContext context) {
        double homeReboundScore = 0.0;

        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {
            double minutesShare = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
            double playerReboundScore = getPlayerReboundScore(inGamePlayer, context) * minutesShare;
            inGamePlayer.setReboundContribution(playerReboundScore);
            homeReboundScore += playerReboundScore;
        }
        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {
            inGamePlayer.setReboundWeight(inGamePlayer.getReboundContribution() / homeReboundScore);
        }
        return homeReboundScore;
    }

    private double getPlayerReboundScore(InGamePlayer inGamePlayer, ReboundContext context) {
        Player player = inGamePlayer.getPlayer();
        double score = PlayerScoreCalculator.calculateReboundScore(player);
        return badgeEngine.apply(player, BadgeType.REBOUND, Target.REBOUND_SCORE, score, context);
    }

    private InGamePlayer pickRebounder(List<InGamePlayer> potentialRebounders) {
        double total = 0.0;
        for (InGamePlayer p : potentialRebounders) {
            total += p.getReboundWeight();
        }
        InGamePlayer playerToReturn = null;

        double r = random.nextDouble() * total;
        for (InGamePlayer p : potentialRebounders) {
            r -= p.getReboundWeight();
            if (r <= 0.0) {
                playerToReturn = p;
                break;
            }
        }
        return playerToReturn;
    }
}
