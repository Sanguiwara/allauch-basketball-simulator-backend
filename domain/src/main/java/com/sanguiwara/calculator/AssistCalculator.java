package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.defense.DefenseSchemeResolver;
import com.sanguiwara.defense.DefensiveScheme;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssistCalculator {
    // Constantes pour le calcul du pourcentage de passes décisives
    private static final double ASSIST_CONTRIBUTION_DIVISOR = 50.0;
    private static final double ASSIST_PROBABILITY_MULTIPLIER = 0.60;
    public static final double MIN_ASSIST_PROBABILITY = 0.10;
    public static final double MAX_ASSIST_PROBABILITY = 0.50;

    // Constantes pour la contribution globale au playmaking
    private static final double MIN_TOTAL_PLAYMAKING = -50.0;
    private static final double MAX_TOTAL_PLAYMAKING = 100.0;


    private final DefenseSchemeResolver defenseSchemeResolver;


    public double getPercentageFromScore(double teamPlayMakingScore) {
        return (teamPlayMakingScore / ASSIST_CONTRIBUTION_DIVISOR) * ASSIST_PROBABILITY_MULTIPLIER;
    }


    public double setAssistProbability(GamePlan offenseTeam, GamePlan defenseTeam) {
        DefensiveScheme defensiveScheme = defenseSchemeResolver.resolve(defenseTeam.getDefenseType());

        double teamPlayMakingScore = defensiveScheme.getOffensiveTeamPlaymakingScore(offenseTeam, defenseTeam);
        offenseTeam.getActivePlayers().forEach(activePlayer -> {
            double assistWeight = activePlayer.getPlaymakingContribution() / teamPlayMakingScore;
            activePlayer.setAssistWeight(assistWeight);
        });

        return getPercentageFromScore(Math.clamp(teamPlayMakingScore, MIN_TOTAL_PLAYMAKING, MAX_TOTAL_PLAYMAKING));
    }
}