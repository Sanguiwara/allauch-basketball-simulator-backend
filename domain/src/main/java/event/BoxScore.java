package event;

import result.DriveResult;
import result.ThreePointShootingResult;
import result.TwoPointShootingResult;

public record BoxScore(ThreePointShootingResult threePointShootingResult,
                       DriveResult driveResult,
                       TwoPointShootingResult twoPointShootingResult){}

