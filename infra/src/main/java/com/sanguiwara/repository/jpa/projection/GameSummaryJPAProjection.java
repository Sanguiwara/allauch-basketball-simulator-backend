package com.sanguiwara.repository.jpa.projection;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA projection (constructor expression) for "list games" endpoints.
 * Uses boxed types for result fields because gameResult can be NULL (left join).
 */
public record GameSummaryJPAProjection(
        UUID id,
        Instant executeAt,
        UUID homeGamePlanId,
        UUID awayGamePlanId,
        UUID homeTeamId,
        String homeTeamName,
        UUID awayTeamId,
        String awayTeamName,
        UUID homeClubID,
        UUID awayClubID,
        UUID gameResultId,
        Integer homeThreePtAttempts,
        Integer homeThreePtMade,
        Integer homeDriveAttempts,
        Integer homeDriveMade,
        Integer homeTwoPtAttempts,
        Integer homeTwoPtMade,
        Integer awayThreePtAttempts,
        Integer awayThreePtMade,
        Integer awayDriveAttempts,
        Integer awayDriveMade,
        Integer awayTwoPtAttempts,
        Integer awayTwoPtMade
) {
}

