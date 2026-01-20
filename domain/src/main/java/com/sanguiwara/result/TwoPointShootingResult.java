package com.sanguiwara.result;

import com.sanguiwara.gameevent.ThreePointShotEvent;
import com.sanguiwara.gameevent.TwoPointShotEvent;

import java.util.List;

public record TwoPointShootingResult(
        int attempts,
        int made,
        List<TwoPointShotEvent> events
) implements ShotResult<TwoPointShotEvent>
{
    public double pct() {
        return attempts == 0 ? 0.0 : (made * 1.0 / attempts);
    }

    public static TwoPointShootingResult empty() {
        return new TwoPointShootingResult(0, 0, List.of());
    }

    public static TwoPointShootingResult combine(TwoPointShootingResult a, TwoPointShootingResult b) {
        var merged = new java.util.ArrayList<TwoPointShotEvent>(a.events().size() + b.events().size());
        merged.addAll(a.events());
        merged.addAll(b.events());
        return new TwoPointShootingResult(
                a.attempts() + b.attempts(),
                a.made() + b.made(),
                java.util.Collections.unmodifiableList(merged)
        );
    }
}
