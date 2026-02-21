package com.sanguiwara.progression;

import java.util.UUID;

/**
 * Progression deltas for a given player, triggered by a given event.
 * eventType+eventId identify the event (GAME, TRAINING, ...).
 */
public record PlayerProgression(
        UUID playerId,
        ProgressionEventType eventType,
        UUID eventId,
        PlayerProgressionDelta delta
) {
}
