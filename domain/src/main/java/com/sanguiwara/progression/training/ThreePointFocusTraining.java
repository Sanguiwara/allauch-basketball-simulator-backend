package com.sanguiwara.progression.training;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.modifiers.PlayerModifier;
import com.sanguiwara.progression.archetype.PlayerArchetypeDefinition;

import java.util.List;

final class ThreePointFocusTraining implements TrainingProgression {

    private static final double NEXT_GAME_THREE_POINT_SHOT_PCT_BONUS = 0.05;

    @Override
    public TrainingType type() {
        return TrainingType.THREE_POINT_FOCUS;
    }

    @Override
    public List<PlayerModifier> temporaryModifiers() {
        return List.of(PlayerModifier.nextGameThreePointShotPctBonus(NEXT_GAME_THREE_POINT_SHOT_PCT_BONUS));
    }

    @Override
    public void apply(Player player, PlayerArchetypeDefinition archetype, TrainingEngine trainingEngine) {
        player.addTemporaryModifier(PlayerModifier.nextGameThreePointShotPctBonus(NEXT_GAME_THREE_POINT_SHOT_PCT_BONUS));
    }
}
