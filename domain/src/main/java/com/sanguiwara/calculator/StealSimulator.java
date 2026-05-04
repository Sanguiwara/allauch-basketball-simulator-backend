
package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.badges.ShotContext;
import com.sanguiwara.badges.BadgeEngine;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.Target;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

import static com.sanguiwara.calculator.AssistCalculator.MAX_ASSIST_PROBABILITY;
import static com.sanguiwara.calculator.AssistCalculator.MIN_ASSIST_PROBABILITY;

@Slf4j
@RequiredArgsConstructor
public class StealSimulator {
    // Minutes totales d'une équipe sur un match (5 joueurs * 40 minutes)
    public static final int TOTAL_MINUTES_FOR_TEAM = 200;

    // Bornes pour la probabilité d'interception d'équipe
    public static final double MIN_STEAL_PROBABILITY = 0.01;
    public static final double MAX_STEAL_PROBABILITY = 0.30;

    // Score d'équipe maximum utilisé pour la conversion score -> probabilité
    public static final double MAX_TEAM_STEAL_SCORE = 100.0;

    // Influence du playmaking global sur les interceptions
    // Si positif => réduit les chances d'interception, si négatif => les augmente
    private static final double PLAYMAKING_IMPACT_SCALE = 0.90; // impact max ~30%

    private final Random random;
    private final BadgeEngine badgeEngine;

    public int calculateSteals(GamePlan offensiveTeam, GamePlan defensiveTeam, double assistProbability) {


        // 1) Calculer le score d'interception de l'équipe défensive et les poids individuels
        double teamStealScore = populateTeamStealScore(defensiveTeam);
        double baseStealProbability = scoreToProbability(teamStealScore);

        // 2) Ajuster avec le playmaking (positif diminue, négatif augmente)
        double adjustedStealProbability = applyPlaymakingAdjustment(baseStealProbability, assistProbability);

        log.info("Team steal score: {} (baseProb: {}, adjustedProb: {})",
                String.format("%.4f", teamStealScore),
                String.format("%.4f", baseStealProbability),
                String.format("%.4f", adjustedStealProbability));


        // 3) Simulation sur le nombre total de tentatives/possessions de l'équipe offensive
        int steals = 0;
        int iterations = offensiveTeam.getTotalShotNumber();
        for (int i = 0; i < iterations; i++) {
            if (random.nextDouble() < adjustedStealProbability) {
                InGamePlayer stealer = pickStealer(defensiveTeam.getActivePlayers());
                assert stealer != null;
                stealer.setSteals(stealer.getSteals() + 1);
                steals++;
                    log.debug("Steal credited to: {}", stealer.getPlayer().getName());


            }
        }

        log.info("Team recorded steals: {} out of {} plays (p~={})",
                steals, iterations, String.format("%.3f", adjustedStealProbability));

        return steals;
    }

    private double populateTeamStealScore(GamePlan defensiveTeam) {
        double teamStealScore = 0.0;
        for (InGamePlayer inGamePlayer : defensiveTeam.getActivePlayers()) {
            double minutesShare = (double) inGamePlayer.getMinutesPlayed() / TOTAL_MINUTES_FOR_TEAM;
            double playerScore = getPlayerStealScore(inGamePlayer) * minutesShare;
            inGamePlayer.setStealContribution(playerScore);
            teamStealScore += playerScore;
        }
        for (InGamePlayer inGamePlayer : defensiveTeam.getActivePlayers()) {
            double weight = inGamePlayer.getStealContribution() / teamStealScore;
            inGamePlayer.setStealWeight(weight);
        }
        return teamStealScore;
    }

    private double getPlayerStealScore(InGamePlayer inGamePlayer) {
        Player p = inGamePlayer.getPlayer();
        double score = PlayerScoreCalculator.calculateStealScore(p);
        return badgeEngine.apply(p, BadgeType.STEAL, Target.STEAL_SCORE, score, ShotContext.empty());
    }

    private static double scoreToProbability(double score) {
        double prob = MIN_STEAL_PROBABILITY + (score / MAX_TEAM_STEAL_SCORE) * (MAX_STEAL_PROBABILITY - MIN_STEAL_PROBABILITY);
        return Math.clamp(prob, 0.0, 1.0);
    }

    private double applyPlaymakingAdjustment(double baseProbability, double assistProbability) {

        // Clamp safety
        double assist = Math.clamp(assistProbability, MIN_ASSIST_PROBABILITY, MAX_ASSIST_PROBABILITY);

        // Normalize [0.10 .. 0.50] -> [-1 .. 0]
        // 0.10 => -1 (bonus max)
        // 0.50 =>  0 (neutral)
        double normalizedPlaymaking =
                (assist - MAX_ASSIST_PROBABILITY) / (MAX_ASSIST_PROBABILITY - MIN_ASSIST_PROBABILITY);

        // Convert to multiplicative factor (only bonus, never penalty)
        double adjustmentFactor =
                1.0 - (normalizedPlaymaking * PLAYMAKING_IMPACT_SCALE);

        return Math.clamp(baseProbability * adjustmentFactor, 0.0, 1.0);
    }




    private InGamePlayer pickStealer(List<InGamePlayer> potentialSteelers) {
        double total = 0.0;
        for (InGamePlayer p : potentialSteelers) {
            total += p.getStealWeight();
        }
        if (total <= 0.0) return null;

        double r = random.nextDouble() * total;
        for (InGamePlayer p : potentialSteelers) {
            r -= p.getStealWeight();
            if (r <= 0.0) {
                return p;
            }
        }
        return null;
    }

}
