package com.sanguiwara.progression;

import java.util.UUID;

/**
 * Progression deltas for a given player, triggered by a given event.
 * For now, eventId refers to a Game id. The model stays generic for future event types.
 */
public record PlayerProgression(
        UUID playerId,
        UUID eventId,
        PlayerProgressionDelta delta
) {
}

