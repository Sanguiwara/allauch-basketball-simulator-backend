package com.sanguiwara.calculator;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.gameevent.DriveEvent;
import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;
import com.sanguiwara.result.BoxScore;
import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;
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





    public BoxScore calculateScoreForTeam(GamePlan home, GamePlan visitor) {

         double blockProbability = blockCalculator.populateGamePlanWithBlockScore(visitor);


        int offensiveReboundForTeam = reboundCalculator.evaluateOffensiveReboundForTeam(home, visitor);
        home.setTotalShotNumber(offensiveReboundForTeam + home.getTotalShotNumber());
        double assistProbability = playmakingCalculator.getTotalPlaymakingContribution(home, visitor);
        ThreePointShootingResult threePointShootingResult =
                threePointSimulator.getTotalShotContribution(home, visitor, assistProbability, blockProbability);
        TwoPointShootingResult twoPointShootingResult =
                twoPointSimulator.getTotalShotContribution(home, visitor, assistProbability, blockProbability);

        DriveResult driveResult =
                driveSimulator.getTotalShotContribution(home, visitor, assistProbability, blockProbability);

        return new BoxScore(threePointShootingResult, driveResult, twoPointShootingResult);

    }


}
