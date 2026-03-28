package com.sanguiwara.baserecords;

import java.time.Instant;
import java.util.UUID;

/**
 * Read-model used for "list games" endpoints.
 * Naming matches {@code SimplifiedGameDTO} on purpose to make mapping trivial.
 */
public record GameSummary(
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
        GameResultSummary gameResult
) {
}

