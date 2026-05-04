package com.sanguiwara.baserecords;

import java.util.Objects;

/**
 * Typed wrapper for the offensive player assigned to a defender.
 */
public record MatchupAttacker(Player player) {

    public MatchupAttacker {
        Objects.requireNonNull(player, "player");
    }
}
