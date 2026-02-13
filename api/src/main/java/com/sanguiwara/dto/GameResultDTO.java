package com.sanguiwara.dto;

public record GameResultDTO(
        BoxScoreDTO homeScore,
        BoxScoreDTO awayScore
) {
    public record BoxScoreDTO(
            ShootingResultDTO threePointShootingResult,
            ShootingResultDTO driveResult,
            ShootingResultDTO twoPointShootingResult
    ) {
    }

    public record ShootingResultDTO(
            int attempts,
            int made
    ) {
    }

}

