package com.sanguiwara.dto;

import java.util.UUID;

public record InGamePlayerDTO(
        UUID id,
        PlayerDTO player,
        UUID gamePlanId,
        int usageShoot,
        int usageDrive,
        int usagePost,
        double playmakingContribution,
        double reboundContribution,
        double assistWeight,
        double reboundWeight,
        double threePointContribution,
        double threePointWeight,
        double twoPointContribution,
        double twoPointWeight,
        double driveContribution,
        double driveWeight,
        double blockContribution,
        double blockWeight,
        double stealContribution,
        double stealWeight,
        int assists,
        int points,
        int offensiveRebounds,
        int defensiveRebounds,
        int steals,
        int blocks,
        int fga,
        int fgm,
        int threePointAttempt,
        int threePointMade,
        int twoPointAttempts,
        int twoPointMade,
        int driveAttempts,
        int driveMade,
        int minutesPlayed,
        boolean starter
) {
}

