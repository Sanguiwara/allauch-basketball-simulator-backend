package com.sanguiwara.progression.training;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.archetype.PlayerArchetypeDefinition;

final class MoraleTraining implements TrainingProgression {

    private static final int MIN_DELTA = 1;
    private static final int MAX_DELTA = 3;

    @Override
    public TrainingType type() {
        return TrainingType.MORALE;
    }

    @Override
    public void apply(Player player, PlayerArchetypeDefinition archetype, TrainingEngine trainingEngine) {
        trainingEngine.applyMoraleRoll(type(), archetype, player, MIN_DELTA, MAX_DELTA);
    }
}
