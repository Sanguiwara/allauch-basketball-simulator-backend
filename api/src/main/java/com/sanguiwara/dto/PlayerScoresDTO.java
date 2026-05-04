package com.sanguiwara.dto;

public record PlayerScoresDTO(
        double threePtScore,
        double threePtDefenseScore,
        double twoPtScore,
        double twoPtDefenseScore,
        double driveScore,
        double driveDefenseScore,
        double manToManPlaymakingOffScore,
        double manToManPlaymakingDefScore,
        double zonePlaymakingOffScore,
        double zonePlaymakingDefScore,
        double zone23DefenseScore,
        double zone32DefenseScore,
        double zone212DefenseScore,
        double reboundScore,
        double stealScore
) {
}
