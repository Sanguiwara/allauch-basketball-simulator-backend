package com.sanguiwara.progression;

import com.sanguiwara.modifiers.PlayerModifier;

import java.util.Set;
import java.util.UUID;

/**
 * Progression deltas for a given player, triggered by a given event.
 * eventType+eventId identify the event (GAME, TRAINING, ...).
 * badgeIds and temporaryModifiers are snapshots of rewards gained during the event.
 */
public record PlayerProgression(
        UUID playerId,
        ProgressionEventType eventType,
        UUID eventId,
        Set<Long> badgeIds,
        Set<PlayerModifier> temporaryModifiers,
        PlayerProgressionDelta delta
) {
    public PlayerProgression {
        badgeIds = badgeIds == null ? Set.of() : Set.copyOf(badgeIds);
        temporaryModifiers = temporaryModifiers == null ? Set.of() : Set.copyOf(temporaryModifiers);
    }
}
