package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;
import com.sanguiwara.progression.ProgressionSkillGroup;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class PlayerArchetypeDefinition {

    private final PlayerArchetype type;
    private final Map<TrainingType, Double> trainingMultipliers;
    private final Map<ProgressionSkillGroup, Double> trainingSkillMultipliers;
    private final Map<ProgressionSkillGroup, Double> matchSkillMultipliers;
    private final Map<ModifierType, Double> badgeDropRateMultipliers;

    protected PlayerArchetypeDefinition(
            PlayerArchetype type,
            Map<TrainingType, Double> trainingMultipliers,
            Map<ProgressionSkillGroup, Double> trainingSkillMultipliers,
            Map<ProgressionSkillGroup, Double> matchSkillMultipliers,
            Map<ModifierType, Double> badgeDropRateMultipliers
    ) {
        this.type = Objects.requireNonNull(type, "type");
        Objects.requireNonNull(trainingMultipliers, "trainingMultipliers");
        Objects.requireNonNull(trainingSkillMultipliers, "trainingSkillMultipliers");
        Objects.requireNonNull(matchSkillMultipliers, "matchSkillMultipliers");
        Objects.requireNonNull(badgeDropRateMultipliers, "badgeDropRateMultipliers");

        validateMultipliers(trainingMultipliers, "training");
        validateMultipliers(trainingSkillMultipliers, "training skill");
        validateMultipliers(matchSkillMultipliers, "match skill");
        validateMultipliers(badgeDropRateMultipliers, "badge drop rate");

        this.trainingMultipliers = Map.copyOf(trainingMultipliers);
        this.trainingSkillMultipliers = Map.copyOf(trainingSkillMultipliers);
        this.matchSkillMultipliers = Map.copyOf(matchSkillMultipliers);
        this.badgeDropRateMultipliers = Map.copyOf(badgeDropRateMultipliers);
    }

    public final PlayerArchetype type() {
        return type;
    }

    public final PlayerArchetype archetype() {
        return type;
    }


    public final double trainingMultiplier(TrainingType trainingType, ProgressionSkillGroup skillGroup) {
        Objects.requireNonNull(trainingType, "trainingType");
        Objects.requireNonNull(skillGroup, "skillGroup");
        return multiplier(trainingMultipliers, trainingType)
                * multiplier(trainingSkillMultipliers, skillGroup);
    }

    public final double matchMultiplier(ProgressionSkillGroup skillGroup) {
        Objects.requireNonNull(skillGroup, "skillGroup");
        return multiplier(matchSkillMultipliers, skillGroup);
    }

    public final double badgeDropRateMultiplier(Set<ModifierType> badgeTypes) {
        Objects.requireNonNull(badgeTypes, "badgeTypes");
        return badgeTypes.stream()
                .mapToDouble(type -> multiplier(badgeDropRateMultipliers, type))
                .max()
                .orElse(1.0);
    }

    public final double effectiveBadgeDropRate(Badge badge) {
        Objects.requireNonNull(badge, "badge");
        double baseRate = badge.dropRate();
        validateDropRate(baseRate, "Base badge drop rate for " + badge.id());

        double effectiveRate = baseRate * badgeDropRateMultiplier(badge.types());
        validateDropRate(effectiveRate, "Effective badge drop rate for " + badge.id());
        return effectiveRate;
    }

    public abstract void applyInitialStats(Player.PlayerBuilder builder, StatRoller roller);

    protected final void fillAll(Player.PlayerBuilder builder, StatRoller roller, int min, int max) {
        Objects.requireNonNull(builder, "builder");
        Objects.requireNonNull(roller, "roller");

        builder.tir3Pts(roller.roll(min, max))
                .tir2Pts(roller.roll(min, max))
                .lancerFranc(roller.roll(min, max))
                .floater(roller.roll(min, max))
                .finitionAuCercle(roller.roll(min, max))
                .agressivite(roller.roll(min, max))
                .speed(roller.roll(min, max))
                .ballhandling(roller.roll(min, max))
                .size(roller.roll(min, max))
                .weight(roller.roll(min, max))
                .defExterieur(roller.roll(min, max))
                .defPoste(roller.roll(min, max))
                .protectionCercle(roller.roll(min, max))
                .timingRebond(roller.roll(min, max))
                .agressiviteRebond(roller.roll(min, max))
                .steal(roller.roll(min, max))
                .timingBlock(roller.roll(min, max))
                .physique(roller.roll(min, max))
                .basketballIqOff(roller.roll(min, max))
                .basketballIqDef(roller.roll(min, max))
                .passingSkills(roller.roll(min, max))
                .iq(roller.roll(min, max))
                .endurance(roller.roll(min, max))
                .solidite(roller.roll(min, max))
                .potentielSkill(roller.roll(min, max))
                .potentielPhysique(roller.roll(min, max))
                .coachability(roller.roll(min, max))
                .ego(roller.roll(min, max))
                .softSkills(roller.roll(min, max))
                .leadership(roller.roll(min, max))
                .morale(roller.roll(min, max));
    }

    private static <T> double multiplier(Map<T, Double> multipliers, T key) {
        return multipliers.getOrDefault(key, 1.0);
    }

    private static <T> void validateMultipliers(Map<T, Double> multipliers, String label) {
        for (Map.Entry<T, Double> entry : multipliers.entrySet()) {
            Double value = entry.getValue();
            if (value == null || !Double.isFinite(value) || value <= 0.0) {
                throw new IllegalArgumentException("Invalid " + label + " multiplier for " + entry.getKey() + ": " + value);
            }
        }
    }

    private static void validateDropRate(double value, String label) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException(label + " must be between 0 and 1: " + value);
        }
    }
}
