package com.sanguiwara.dto;

import java.time.Instant;
import java.util.UUID;

public record GameDTO(
        UUID id,
        Instant executeAt,
        UUID homeGamePlanId,
        UUID homeTeamId,
        String homeTeamName,
        UUID awayTeamId,
        String awayTeamName
) {
}
