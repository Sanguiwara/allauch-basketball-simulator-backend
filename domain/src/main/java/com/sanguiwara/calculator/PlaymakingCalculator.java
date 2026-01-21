package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaymakingCalculator {
    // Constantes pour le calcul du pourcentage de passes décisives
    private static final double ASSIST_CONTRIBUTION_DIVISOR = 50.0;
    private static final double ASSIST_PROBABILITY_MULTIPLIER = 0.60;
    private static final double MIN_ASSIST_PROBABILITY = 0.10;
    private static final double MAX_ASSIST_PROBABILITY = 0.50;

    // Constantes pour la contribution globale au playmaking
    private static final double MIN_TOTAL_PLAYMAKING = -50.0;
    private static final double MAX_TOTAL_PLAYMAKING = 100.0;

    // Poids pour le score offensif individuel
    private static final double OFF_SPEED_WEIGHT = 0.15;
    private static final double OFF_SIZE_WEIGHT = 0.05;
    private static final double OFF_ENDURANCE_WEIGHT = 0.05;
    private static final double OFF_PASSING_WEIGHT = 0.10;
    private static final double OFF_IQ_WEIGHT = 0.23;
    private static final double OFF_HANDLING_WEIGHT = 0.10;
    private static final double OFF_3PT_WEIGHT = 0.05;
    private static final double OFF_2PT_WEIGHT = 0.05;
    private static final double OFF_FINISH_WEIGHT = 0.05;
    private static final double OFF_FLOATER_WEIGHT = 0.025;

    // Poids pour le score défensif individuel
    private static final double DEF_SPEED_WEIGHT = 0.15;
    private static final double DEF_SIZE_WEIGHT = 0.05;
    private static final double DEF_EXTERIOR_WEIGHT = 0.35;
    private static final double DEF_ENDURANCE_WEIGHT = 0.05;
    private static final double DEF_IQ_WEIGHT = 0.10;
    private static final double DEF_STEAL_WEIGHT = 0.10;

    // Seuils pour l'avantage individuel
    private static final double MIN_INDIVIDUAL_ADVANTAGE = -5.0;
    private static final double MAX_INDIVIDUAL_ADVANTAGE = 20.0;
    public static final double DEFAULT_CONTRIBUTION = 10.0;


    public double getAssistedShotPercentage(double playmakingContribution) {
        return clamp((playmakingContribution / ASSIST_CONTRIBUTION_DIVISOR) * ASSIST_PROBABILITY_MULTIPLIER, MIN_ASSIST_PROBABILITY, MAX_ASSIST_PROBABILITY);
    }

    public double getTotalPlaymakingContribution(GamePlan home, GamePlan visitor) {


        double totalPlayMakingContribution = home.getActivePlayers().stream()
                .mapToDouble(homePlayer -> {
                    Player visitorPlayer = visitor.getMatchups().get(homePlayer.getPlayer());
                    if (visitorPlayer != null) {
                        return getIndividualPlayMakingContribution(homePlayer, visitorPlayer);
                    } else {
                        homePlayer.setPlaymakingContribution(DEFAULT_CONTRIBUTION);
                        return DEFAULT_CONTRIBUTION;
                    }
                })
                .sum();

        double finalTotalPlayMakingContribution = totalPlayMakingContribution;
        home.getActivePlayers().forEach(activePlayer -> activePlayer.setAssistWeight(activePlayer.getPlaymakingContribution() / finalTotalPlayMakingContribution));

        totalPlayMakingContribution = clamp(totalPlayMakingContribution, MIN_TOTAL_PLAYMAKING, MAX_TOTAL_PLAYMAKING);
        return getAssistedShotPercentage(totalPlayMakingContribution);
    }


    public double getIndividualPlayMakingContribution(InGamePlayer inGameOff, Player def) {
        Player off = inGameOff.getPlayer();

        double offScore =
                OFF_SPEED_WEIGHT * off.speed() +
                        OFF_SIZE_WEIGHT * off.size()
                        + OFF_ENDURANCE_WEIGHT * off.endurance() +
                        OFF_PASSING_WEIGHT * off.passingSkills() +
                        OFF_IQ_WEIGHT * off.basketballIqOff() +
                        OFF_HANDLING_WEIGHT * off.ballhandling() +
                        OFF_3PT_WEIGHT * off.tir3Pts() +
                        OFF_2PT_WEIGHT * off.tir2Pts()
                        + OFF_FINISH_WEIGHT * off.finitionAuCercle()
                        + OFF_FLOATER_WEIGHT * off.floater();

        double defScore =
                DEF_SPEED_WEIGHT * def.speed() +
                        DEF_SIZE_WEIGHT * off.size() +
                        DEF_EXTERIOR_WEIGHT * def.defExterieur()
                        + DEF_ENDURANCE_WEIGHT * def.endurance()
                        + DEF_IQ_WEIGHT * def.basketballIqDef()
                        + DEF_STEAL_WEIGHT * def.steal();


        double adv = offScore - defScore;
        double advantage = clamp(adv, MIN_INDIVIDUAL_ADVANTAGE, MAX_INDIVIDUAL_ADVANTAGE);
        inGameOff.setPlaymakingContribution(advantage);
        return advantage;
    }

    static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }


}
