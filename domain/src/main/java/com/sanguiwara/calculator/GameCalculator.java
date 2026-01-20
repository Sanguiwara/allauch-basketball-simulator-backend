package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.result.BoxScore;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GameCalculator {


    private final ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator;
    private final ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator;
    private final ShotSimulator<DriveEvent, DriveResult> driveSimulator;



    public BoxScore calculate(GamePlan home, GamePlan visitor) {
        double totalPlayMakingContribution = getTotalPlaymakingContribution(home, visitor);
        double assistProbability = getAssistedShotPercentage(totalPlayMakingContribution);
        ThreePointShootingResult threePointShootingResult =
                threePointSimulator.getTotalShotContribution(home, visitor, assistProbability);
        TwoPointShootingResult twoPointShootingResult =
                twoPointSimulator.getTotalShotContribution(home, visitor, assistProbability);

        DriveResult driveResult =
                driveSimulator.getTotalShotContribution(home, visitor, assistProbability);

        return new BoxScore(threePointShootingResult, driveResult, twoPointShootingResult);


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

        totalPlayMakingContribution = clamp(totalPlayMakingContribution, -50, 100);
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


        double adv = offScore - defScore;
        double advantage = clamp(adv, -5, 20);
        inGameOff.setPlaymakingContribution(advantage);
        return advantage;


    }

    static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

}
