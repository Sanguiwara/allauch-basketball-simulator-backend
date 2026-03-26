package com.sanguiwara.factory;

import java.util.Objects;
import java.util.Random;

public enum PlayerArchetype {
    SOLDIER,
    STRATEGIST,
    CROQUEUR,
    WHITE_SHOOTER,
    THREE_POINT_SHOOTER,
    TWO_POINT_SCORER,
    DRIVE_SPECIALIST,
    YOUNG_STAR,
    ALL_AROUND,
    ALL_STAR;

    public static PlayerArchetype random(Random rng) {
        Objects.requireNonNull(rng, "rng");
        PlayerArchetype[] values = values();
        return values[rng.nextInt(values.length)];
    }

    /**
     * Default behavior for legacy {@code generatePlayer(String)}.
     * Uniform across all archetypes.
     */
    public static PlayerArchetype defaultForGeneratePlayer(Random rng) {
        return random(rng);
    }
}
