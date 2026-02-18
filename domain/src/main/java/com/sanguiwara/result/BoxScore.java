package com.sanguiwara.result;

public record BoxScore(ThreePointShootingResult threePointShootingResult,
                       DriveResult driveResult,
                       TwoPointShootingResult twoPointShootingResult){
    public int totalPoints() {
        return threePointShootingResult.made() * 3 + driveResult.made() * 2 + twoPointShootingResult.made()  *2;
    }
}

