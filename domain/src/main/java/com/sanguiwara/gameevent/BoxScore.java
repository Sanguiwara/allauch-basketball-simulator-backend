package com.sanguiwara.gameevent;

import com.sanguiwara.result.DriveResult;
import com.sanguiwara.result.ThreePointShootingResult;
import com.sanguiwara.result.TwoPointShootingResult;

public record BoxScore(ThreePointShootingResult threePointShootingResult,
                       DriveResult driveResult,
                       TwoPointShootingResult twoPointShootingResult){}

