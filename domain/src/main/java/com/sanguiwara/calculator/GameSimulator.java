package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GameSimulator {


    private final ShotSimulator<ThreePointShotEvent, ThreePointShootingResult> threePointSimulator;
    private final ShotSimulator<TwoPointShotEvent, TwoPointShootingResult> twoPointSimulator;
    private final ShotSimulator<DriveEvent, DriveResult> driveSimulator;
    private final PlaymakingCalculator playmakingCalculator;
    private final ReboundCalculator reboundCalculator;
    private final BlockCalculator blockCalculator;
    private final StealSimulator stealSimulator;


    public GameResult calculateGame(GamePlan home, GamePlan visitor){

        double homeAssistProbability = playmakingCalculator.setAssistProbability(home, visitor);
        double visitorAssistProbability = playmakingCalculator.setAssistProbability(visitor, home);

        computePossessions(home,visitor, homeAssistProbability, visitorAssistProbability);
        BoxScore homeBoxScore = calculateScoreForTeam(home,visitor,homeAssistProbability);
        BoxScore awayBoxScore = calculateScoreForTeam(visitor,home, visitorAssistProbability);

        return new GameResult(homeBoxScore, awayBoxScore);

    }

    public void computePossessions(GamePlan home, GamePlan visitor, double homeAssistProbability, double visitorAssistProbability){


        int offensiveReboundForHomeTeam = reboundCalculator.evaluateOffensiveReboundForTeam(home, visitor);
        int offensiveReboundForVisitorTeam = reboundCalculator.evaluateOffensiveReboundForTeam(visitor, home);
        int stealsFromVisitor = stealSimulator.calculateSteals(home,visitor, homeAssistProbability);
        int stealsFromHome = stealSimulator.calculateSteals(visitor,home, visitorAssistProbability);

        visitor.addPossessions(offensiveReboundForVisitorTeam + stealsFromVisitor);
        visitor.removePossessions(offensiveReboundForHomeTeam - stealsFromHome);

        home.addPossessions(offensiveReboundForHomeTeam + stealsFromHome);
        home.removePossessions(offensiveReboundForVisitorTeam + stealsFromVisitor);

    }




    public BoxScore calculateScoreForTeam(GamePlan home, GamePlan visitor, double assistProbability) {

         double blockProbability = blockCalculator.populateGamePlanWithBlockScore(visitor);



        ThreePointShootingResult threePointShootingResult =
                threePointSimulator.getTotalShotContribution(home, visitor, assistProbability, blockProbability);
        TwoPointShootingResult twoPointShootingResult =
                twoPointSimulator.getTotalShotContribution(home, visitor, assistProbability, blockProbability);

        DriveResult driveResult =
                driveSimulator.getTotalShotContribution(home, visitor, assistProbability, blockProbability);

        return new BoxScore(threePointShootingResult, driveResult, twoPointShootingResult);

    }


}
