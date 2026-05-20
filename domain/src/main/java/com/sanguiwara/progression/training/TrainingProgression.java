package com.sanguiwara.progression.training;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.modifiers.PlayerModifier;
import com.sanguiwara.progression.archetype.PlayerArchetypeDefinition;

import java.util.List;

public interface TrainingProgression {

    TrainingType type();

    default List<ModifierType> badgeModifierTypes() {
        return List.of();
    }

    default double badgeDropRateMultiplier() {
        return 0.0;
    }

    default List<PlayerModifier> temporaryModifiers() {
        return List.of();
    }

    default TrainingType getType() {
        return type();
    }

    default List<ModifierType> getBadgeModifierTypes() {
        return badgeModifierTypes();
    }

    default double getBadgeDropRateMultiplier() {
        return badgeDropRateMultiplier();
    }

    default List<PlayerModifier> getTemporaryModifiers() {
        return temporaryModifiers();
    }

    void apply(Player player, PlayerArchetypeDefinition archetype, TrainingEngine trainingEngine);
}
