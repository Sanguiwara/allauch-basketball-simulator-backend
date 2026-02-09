package com.sanguiwara.dto;

public record GameResultDTO(
        BoxScoreDTO homeScore,
        BoxScoreDTO awayScore
) {
    public record BoxScoreDTO(
            ShootingResultDTO threePointShootingResult,
            DriveResultDTO driveResult,
            ShootingResultDTO twoPointShootingResult
    ) {
    }

    public record ShootingResultDTO(
            int attempts,
            int made
    ) {
    }

    public record DriveResultDTO(
            int attempts,
            int made,
            int foulsDrawn
    ) {
    }
}

