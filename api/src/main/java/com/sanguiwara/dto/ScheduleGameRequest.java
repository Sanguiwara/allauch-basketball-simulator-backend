package com.sanguiwara.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleGameRequest(
        UUID homeTeamId,
        UUID awayTeamId,
        LocalDateTime localDateTime,
        String zoneId,
        UUID leagueSeasonId
) {
}
