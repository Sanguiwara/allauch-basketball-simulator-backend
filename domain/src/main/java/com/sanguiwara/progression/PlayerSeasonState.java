package com.sanguiwara.progression;

import com.sanguiwara.baserecords.Player;

import java.util.UUID;

public record PlayerSeasonState(
        UUID leagueSeasonId,
        Player seasonStart,
        Player current,
        PlayerProgressionDelta delta
) {
    public static PlayerSeasonState between(UUID leagueSeasonId, Player seasonStart, Player current) {
        return new PlayerSeasonState(
                leagueSeasonId,
                seasonStart,
                current,
                PlayerProgressionDelta.between(seasonStart, current)
        );
    }
}
