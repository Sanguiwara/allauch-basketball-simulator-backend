package com.sanguiwara.service;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.gameevent.BoxScore;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import com.sanguiwara.service.simulator.DriveSimulator;
import com.sanguiwara.service.simulator.ThreePointSimulator;
import com.sanguiwara.service.simulator.TwoPointSimulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GameCalculator {

    private final TwoPointSimulator twoPointSimulator;
    private final DriveSimulator driveSimulator;



    public BoxScore calculate(GamePlan home, GamePlan visitor) {


        double totalPlayMakingContribution = getTotalPlaymakingContribution(home, visitor);
        double assistedShotPercentage = getAssistedShotPercentage(totalPlayMakingContribution);
        ThreePointShootingResult threePointShootingResult =
                ThreePointSimulator.getTotal3ptsContribution(home, visitor, assistedShotPercentage );
        TwoPointShootingResult twoPointShootingResult = twoPointSimulator.get2ptsTotalContribution(home, visitor,assistedShotPercentage);
        DriveResult driveResult = driveSimulator.getDriveTotalContribution(home.getActivePlayers(), visitor, assistedShotPercentage);

        return new BoxScore(threePointShootingResult,driveResult,twoPointShootingResult);




    }

    public double getAssistedShotPercentage(double playmakingContribution) {
        return clamp((playmakingContribution / 50.0) * 0.60, 0.10, 0.50);

    }


    public double getTotalPlaymakingContribution(GamePlan home, GamePlan visitor) {


        double totalPlayMakingContribution = home.getActivePlayers().stream()
                .filter(homePlayer -> homePlayer != null && visitor.getMatchups().get(homePlayer.getPlayer()) != null)
                .mapToDouble(homePlayer -> {
                    Player visitorPlayer = visitor.getMatchups().get(homePlayer.getPlayer());
                    return getIndividualPlayMakingContribution(homePlayer, visitorPlayer);
                })
                .sum();

        double finalDuelAdvantage = totalPlayMakingContribution;
        home.getActivePlayers().forEach(activePlayer -> activePlayer.setAssistWeight(finalDuelAdvantage / activePlayer.getPlaymakingContribution()));

        //duelAdvantage = duelAdvantage + home.calculateOffsenseTeamAdvantage() - visitor.calculateDefenseTeamAdvantage();
        totalPlayMakingContribution = clamp(totalPlayMakingContribution, -50, 100);
        //log.info(String.valueOf(duelAdvantage));
        return totalPlayMakingContribution;
    }







    public double getIndividualPlayMakingContribution(InGamePlayer inGameOff, Player def) {
        Player off = inGameOff.getPlayer();

        double offScore =
                0.15 * off.speed() +
                        0.05 * off.size()
                        + 0.05 * off.endurance() +
                        0.10 * off.passingSkills() +
                        0.23 * off.basketballIqOff() +
                        0.10 * off.ballhandling() +
                        0.05 * off.tir3Pts() +
                        0.05 * off.tir2Pts()
                        + 0.05 * off.finitionAuCercle()
                        + 0.025 * off.floater();

        double defScore =
                0.15 * def.speed() +
                        0.05 * off.size() +
                        0.35 * def.defExterieur()
                        + 0.05 * def.endurance()
                        + 0.10 * def.basketballIqDef()
                        + 0.10 * def.steal();


        double adv = offScore - defScore; // ~[-100;+100]
        //log.info(offScore + " - " + defScore + " = " + adv);

        double advantage = clamp(adv, -5, 20);
        inGameOff.setPlaymakingContribution(advantage);
        return advantage;


    }

    static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

}
