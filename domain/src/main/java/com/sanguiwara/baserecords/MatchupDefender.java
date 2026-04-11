package com.sanguiwara.baserecords;

import java.util.Objects;

/**
 * Typed wrapper for the defensive player used as the matchup key.
 */
public record MatchupDefender(Player player) {

    public MatchupDefender {
        Objects.requireNonNull(player, "player");
    }
}
