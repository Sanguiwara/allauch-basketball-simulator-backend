package com.sanguiwara.progression.training;

import com.sanguiwara.baserecords.TrainingType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TrainingProgressions {

    private static final Map<TrainingType, TrainingProgression> DEFAULTS = buildDefaults();

    private TrainingProgressions() {
    }

    public static Map<TrainingType, TrainingProgression> defaults() {
        return DEFAULTS;
    }

    public static TrainingProgression defaultFor(TrainingType type) {
        TrainingProgression progression = DEFAULTS.get(Objects.requireNonNull(type, "type"));
        if (progression == null) {
            throw new IllegalArgumentException("Unsupported training type: " + type);
        }
        return progression;
    }

    private static Map<TrainingType, TrainingProgression> buildDefaults() {
        List<TrainingProgression> trainings = List.of(
                new ShootingTraining(),
                new DefenseTraining(),
                new PhysicalTraining(),
                new PlaymakingTraining(),
                new MoraleTraining(),
                new TacticalTraining(),
                new FreePlayTraining(),
                new ThreePointFocusTraining()
        );

        EnumMap<TrainingType, TrainingProgression> byType = new EnumMap<>(TrainingType.class);
        for (TrainingProgression training : trainings) {
            TrainingProgression previous = byType.put(training.type(), training);
            if (previous != null) {
                throw new IllegalStateException("Duplicate training progression for " + training.type());
            }
        }

        if (byType.size() != TrainingType.values().length) {
            throw new IllegalStateException("Every training type must have a progression implementation");
        }

        return Map.copyOf(byType);
    }
}
