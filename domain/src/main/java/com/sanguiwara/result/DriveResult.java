package com.sanguiwara.result;

import com.sanguiwara.gameevent.DriveEvent;

import java.util.List;

public record DriveResult(
        int attempts,
        int made,
        int foulsDrawn,
        List<DriveEvent> events
) {
    public double fgPct() {
        return attempts == 0 ? 0.0 : (made * 1.0 / attempts);
    }

    public static DriveResult empty() {
        return new DriveResult(0, 0, 0, List.of());
    }

    public static DriveResult combine(DriveResult a, DriveResult b) {
        var merged = new java.util.ArrayList<DriveEvent>(a.events().size() + b.events().size());
        merged.addAll(a.events());
        merged.addAll(b.events());
        return new DriveResult(
                a.attempts() + b.attempts(),
                a.made() + b.made(),
                a.foulsDrawn() + b.foulsDrawn(),
                java.util.Collections.unmodifiableList(merged)
        );
    }
}
