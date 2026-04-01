package com.sanguiwara.dto;

import com.sanguiwara.baserecords.DefenseType;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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
        DefenseType homeDefenseType,
        DefenseType awayDefenseType,
        Map<UUID, UUID> homeMatchups,
        Map<UUID, UUID> awayMatchups,
        GameResultDTO gameResult,
        List<InGamePlayerDTO> homeActivePlayers,
        List<InGamePlayerDTO> awayActivePlayers,
        List<PlayerProgressionDTO> playerProgressions

) {
}
