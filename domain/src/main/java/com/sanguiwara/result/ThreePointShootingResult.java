package com.sanguiwara.result;

import com.sanguiwara.gameevent.ThreePointShotEvent;

public record ThreePointShootingResult(
        int attempts,
        int made,
        java.util.List<ThreePointShotEvent> events
) {
    public static ThreePointShootingResult empty() {
        return new ThreePointShootingResult(0, 0, new java.util.ArrayList<>());
    }

    public ThreePointShootingResult combine(ThreePointShootingResult other) {
        java.util.List<ThreePointShotEvent> combinedEvents = new java.util.ArrayList<>(this.events);
        combinedEvents.addAll(other.events());
        return new ThreePointShootingResult(
                this.attempts + other.attempts(),
                this.made + other.made(),
                combinedEvents
        );
    }
}