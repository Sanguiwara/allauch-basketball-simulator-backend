package com.sanguiwara.progression.training;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.archetype.PlayerArchetypeDefinition;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

abstract class SkillTrainingProgression implements TrainingProgression {

    private final TrainingType type;
    private final List<TrainableSkill> skills;
    private final int minDelta;
    private final int maxDelta;
    private final Set<ModifierType> eligibleModifierTypes;

    SkillTrainingProgression(
            TrainingType type,
            List<TrainableSkill> skills,
            int minDelta,
            int maxDelta,
            Set<ModifierType> eligibleModifierTypes
    ) {
        this.type = type;
        this.skills = List.copyOf(skills);
        this.minDelta = minDelta;
        this.maxDelta = maxDelta;
        this.eligibleModifierTypes = Set.copyOf(eligibleModifierTypes);
    }

    @Override
    public final TrainingType type() {
        return type;
    }

    @Override
    public final List<ModifierType> badgeModifierTypes() {
        return eligibleModifierTypes.stream()
                .sorted(Comparator.comparing(ModifierType::name))
                .toList();
    }

    @Override
    public final double badgeDropRateMultiplier() {
        return 1.0;
    }

    @Override
    public final void apply(Player player, PlayerArchetypeDefinition archetype, TrainingEngine trainingEngine) {
        trainingEngine.applySkillRolls(type, archetype, player, skills, minDelta, maxDelta);
        trainingEngine.applyBadgeUnlock(archetype, player, eligibleModifierTypes);
    }
}
