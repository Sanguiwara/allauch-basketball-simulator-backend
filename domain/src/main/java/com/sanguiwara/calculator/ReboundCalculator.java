package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
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

    private static final double REB_SIZE_COEFF = 0.18;
    private static final double REB_WEIGHT_COEFF = 0.10;
    private static final double REB_AGGR_COEFF = 0.10;
    private static final double REB_AGGR_REB_COEFF = 0.18;
    private static final double REB_TIMING_COEFF = 0.18;
    private static final double REB_PHYSIQUE_COEFF = 0.14;
    private static final double REB_IQ_COEFF = 0.06;
    private static final double REB_ENDURANCE_COEFF = 0.06;

    private final java.util.Random random;

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
                    log.debug("Offensive rebound credited to: {}", rebounder.getPlayer().name());
                }
            }
        }

        log.info("Team recorded offensive rebounds: {} out of {} shots (p~={})",
                offensiveRebounds, offenseGamePlan.getTotalShotNumber(), String.format("%.3f", offensiveReboundProbability));

        return offensiveRebounds;
    }

    private double evaluateReboundAdvantage(GamePlan home, GamePlan visitor) {
        double homeReboundScore = getHomeReboundScore(home);
        double visitorReboundScore = getHomeReboundScore(visitor);

        return (homeReboundScore - visitorReboundScore) / (homeReboundScore + visitorReboundScore);
    }

    private double getHomeReboundScore(GamePlan gamePlan) {
        double homeReboundScore = 0.0;

        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {
            double minutesShare = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
            double playerReboundScore = getPlayerReboundScore(inGamePlayer) * minutesShare;
            inGamePlayer.setReboundContribution(playerReboundScore);
            homeReboundScore += playerReboundScore;
        }
        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {
            inGamePlayer.setReboundWeight(inGamePlayer.getReboundContribution() / homeReboundScore);
        }
        return homeReboundScore;
    }

    private static double getPlayerReboundScore(InGamePlayer inGamePlayer) {
        Player player = inGamePlayer.getPlayer();
        return REB_SIZE_COEFF * player.size()
                + REB_WEIGHT_COEFF * player.weight()
                + REB_AGGR_COEFF * player.agressivite()
                + REB_AGGR_REB_COEFF * player.agressiviteRebond()
                + REB_TIMING_COEFF * player.timingRebond()
                + REB_PHYSIQUE_COEFF * player.physique()
                + REB_IQ_COEFF * player.iq()
                + REB_ENDURANCE_COEFF * player.endurance();
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