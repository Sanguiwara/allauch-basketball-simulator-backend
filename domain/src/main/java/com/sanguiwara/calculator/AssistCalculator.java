package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.defense.DefenseSchemeResolver;
import com.sanguiwara.defense.DefensiveScheme;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssistCalculator {
    // Constantes pour le calcul du pourcentage de passes décisives
    private static final double PLAYMAKING_SCORE_MIN = -50.0;
    private static final double PLAYMAKING_SCORE_MAX = 50.0;
    private static final double ASSIST_PROBABILITY_AT_MIN = 0.15;
    private static final double ASSIST_PROBABILITY_AT_MAX = 0.50;
    public static final double MIN_ASSIST_PROBABILITY = 0.15;
    public static final double MAX_ASSIST_PROBABILITY = 0.50;

    public static final double MIN_ASSIST_WEIGHT = 0.05;
    public static final double MAX_ASSIST_WEIGHT = 0.80;


    // Constantes pour la contribution globale au playmaking
    // (kept as anchors above)


    private final DefenseSchemeResolver defenseSchemeResolver;


    public double getPercentageFromScore(double teamPlayMakingScore) {
        double clampedScore = Math.clamp(teamPlayMakingScore, PLAYMAKING_SCORE_MIN, PLAYMAKING_SCORE_MAX);
        double t = (clampedScore - PLAYMAKING_SCORE_MIN) / (PLAYMAKING_SCORE_MAX - PLAYMAKING_SCORE_MIN);
        double p = ASSIST_PROBABILITY_AT_MIN + t * (ASSIST_PROBABILITY_AT_MAX - ASSIST_PROBABILITY_AT_MIN);
        return Math.clamp(p, ASSIST_PROBABILITY_AT_MIN, ASSIST_PROBABILITY_AT_MAX);
    }


    public double calculateAssistProbability(GamePlan offenseTeam, GamePlan defenseTeam) {
        DefensiveScheme defensiveScheme = defenseSchemeResolver.resolve(defenseTeam.getDefenseType());

        double teamPlayMakingScore = defensiveScheme.getOffensiveTeamPlaymakingScore(offenseTeam, defenseTeam);
        offenseTeam.getActivePlayers().forEach(activePlayer -> {
            double assistWeight = activePlayer.getPlaymakingContribution() / teamPlayMakingScore;

            assistWeight = Math.clamp(assistWeight, MIN_ASSIST_WEIGHT, MAX_ASSIST_WEIGHT);
            activePlayer.setAssistWeight(assistWeight);
        });

        return getPercentageFromScore(teamPlayMakingScore);
    }
}
