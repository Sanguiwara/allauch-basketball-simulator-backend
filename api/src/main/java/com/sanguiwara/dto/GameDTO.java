package com.sanguiwara.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameDTO(
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
        GameResultDTO gameResult,
        List<InGamePlayerDTO> homeActivePlayers,
        List<InGamePlayerDTO> awayActivePlayers

) {
}
