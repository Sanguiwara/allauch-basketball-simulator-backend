package com.sanguiwara.result;

import com.sanguiwara.gameevent.ThreePointShotEvent;

import java.util.List;

public record ThreePointShootingResult(
        int attempts,
        int made,
        List<ThreePointShotEvent> events
) implements ShotResult<ThreePointShotEvent>
{
    public static ThreePointShootingResult empty() {
        return new ThreePointShootingResult(0, 0, List.of());
    }



    public static ThreePointShootingResult combine(ThreePointShootingResult a, ThreePointShootingResult b) {
        var merged = new java.util.ArrayList<ThreePointShotEvent>(a.events().size() + b.events().size());
        merged.addAll(a.events());
        merged.addAll(b.events());
        return new ThreePointShootingResult(
                a.attempts() + b.attempts(),
                a.made() + b.made(),
                java.util.Collections.unmodifiableList(merged)
        );
    }
}