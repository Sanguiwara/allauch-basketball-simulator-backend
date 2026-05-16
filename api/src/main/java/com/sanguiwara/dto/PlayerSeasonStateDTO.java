package com.sanguiwara.dto;

import java.util.UUID;

public record PlayerSeasonStateDTO(
        UUID playerId,
        UUID leagueSeasonId,
        PlayerDTO seasonStart,
        PlayerDTO current,
        PlayerDeltaDTO delta
) {
}
