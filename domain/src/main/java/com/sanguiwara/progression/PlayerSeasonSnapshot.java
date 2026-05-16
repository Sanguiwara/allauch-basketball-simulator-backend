package com.sanguiwara.progression;

import com.sanguiwara.baserecords.Player;

import java.util.UUID;

/**
 * Immutable player state captured at the beginning of a league season.
 */
public record PlayerSeasonSnapshot(
        UUID leagueSeasonId,
        Player player
) {
    public static PlayerSeasonSnapshot from(UUID leagueSeasonId, Player player) {
        return new PlayerSeasonSnapshot(leagueSeasonId, player.snapshotPlayer());
    }
}
