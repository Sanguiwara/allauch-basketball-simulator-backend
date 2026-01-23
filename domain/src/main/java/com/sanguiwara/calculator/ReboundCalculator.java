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
    public static final double MAX_OFFENSIVE_REBOUND_PROBABILITY = 1.00;
    private final java.util.Random random;


    public static final int TOTAL_MINUTES_FOR_TEAM = 200;



    public int evaluateOffensiveReboundForTeam(GamePlan offenseGamePlan, GamePlan defenseGamePlan) {
        double reboundAdvantage = evaluateReboundAdvantage(offenseGamePlan, defenseGamePlan);



        double normalizedAdvantage = (reboundAdvantage + 1.0) / 2.0;

        double offensiveReboundProbability = MIN_OFFENSIVE_REBOUND_PROBABILITY + normalizedAdvantage * (MAX_OFFENSIVE_REBOUND_PROBABILITY - MIN_OFFENSIVE_REBOUND_PROBABILITY);


            log.debug("ReboundAdvantage for team: {} (normalized: {}), offensiveRebProb: {}",
                    String.format("%.4f", reboundAdvantage),
                    String.format("%.4f", normalizedAdvantage),
                    String.format("%.4f", offensiveReboundProbability));

        int offensiveRebounds = 0;
        for (int i = 0; i <  offenseGamePlan.getTotalShotNumber(); i++) {
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

        return  ( homeReboundScore - visitorReboundScore) / ( homeReboundScore + visitorReboundScore);

    }

    private double getHomeReboundScore(GamePlan gamePlan) {
        double homeReboundScore = 0.0;

        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {
            double playerReboundScore = getPlayerReboundScore(inGamePlayer);
            inGamePlayer.setReboundContribution(playerReboundScore);
            homeReboundScore += playerReboundScore * ((double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM);
        }
        for (InGamePlayer inGamePlayer : gamePlan.getActivePlayers()) {
            inGamePlayer.setReboundWeight(inGamePlayer.getReboundContribution()/homeReboundScore);
        }
        return homeReboundScore;
    }

    private static double getPlayerReboundScore(InGamePlayer inGamePlayer) {
        Player player = inGamePlayer.getPlayer();
        return 0.18 * player.size() +
                0.10 * player.weight() +
                0.10 * player.agressivite() +
                0.18 * player.agressiviteRebond() +
                0.18 * player.timingRebond() +
                0.14 * player.physique() +
                0.06 * player.iq() +
                0.06 * player.endurance();
    }


    private InGamePlayer pickRebounder( List<InGamePlayer> potentialRebounders) {
            double total = 0.0;
            for (InGamePlayer p : potentialRebounders) {
                total +=  p.getReboundWeight();
            }
            InGamePlayer playerToReturn = null;

            double r = random.nextDouble() * total;
            for (InGamePlayer p : potentialRebounders) {
                r -=  p.getReboundWeight();
                if (r <= 0.0) {
                    playerToReturn = p;
                    break;
                }
            }
            return playerToReturn;

    }
}