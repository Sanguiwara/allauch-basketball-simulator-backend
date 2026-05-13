package com.sanguiwara.progression;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.factory.PlayerArchetype;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record ArchetypeProgressionProfile(
        PlayerArchetype archetype,
        Map<TrainingType, Double> trainingMultipliers,
        Map<ProgressionSkillGroup, Double> trainingSkillMultipliers,
        Map<ProgressionSkillGroup, Double> matchSkillMultipliers,
        Map<BadgeType, Double> badgeDropRateMultipliers
) {
    public ArchetypeProgressionProfile {
        Objects.requireNonNull(archetype, "archetype");
        Objects.requireNonNull(trainingMultipliers, "trainingMultipliers");
        Objects.requireNonNull(trainingSkillMultipliers, "trainingSkillMultipliers");
        Objects.requireNonNull(matchSkillMultipliers, "matchSkillMultipliers");
        Objects.requireNonNull(badgeDropRateMultipliers, "badgeDropRateMultipliers");

        validateMultipliers(trainingMultipliers, "training");
        validateMultipliers(trainingSkillMultipliers, "training skill");
        validateMultipliers(matchSkillMultipliers, "match skill");
        validateMultipliers(badgeDropRateMultipliers, "badge drop rate");

        trainingMultipliers = Map.copyOf(trainingMultipliers);
        trainingSkillMultipliers = Map.copyOf(trainingSkillMultipliers);
        matchSkillMultipliers = Map.copyOf(matchSkillMultipliers);
        badgeDropRateMultipliers = Map.copyOf(badgeDropRateMultipliers);
    }

    public double trainingMultiplier(TrainingType trainingType, ProgressionSkillGroup skillGroup) {
        Objects.requireNonNull(trainingType, "trainingType");
        Objects.requireNonNull(skillGroup, "skillGroup");
        return multiplier(trainingMultipliers, trainingType)
                * multiplier(trainingSkillMultipliers, skillGroup);
    }

    public double matchMultiplier(ProgressionSkillGroup skillGroup) {
        Objects.requireNonNull(skillGroup, "skillGroup");
        return multiplier(matchSkillMultipliers, skillGroup);
    }

    public double badgeDropRateMultiplier(Set<BadgeType> badgeTypes) {
        Objects.requireNonNull(badgeTypes, "badgeTypes");
        return badgeTypes.stream()
                .mapToDouble(type -> multiplier(badgeDropRateMultipliers, type))
                .max()
                .orElse(1.0);
    }

    public double effectiveBadgeDropRate(Badge badge) {
        Objects.requireNonNull(badge, "badge");
        double baseRate = badge.dropRate();
        validateDropRate(baseRate, "Base badge drop rate for " + badge.id());

        double effectiveRate = baseRate * badgeDropRateMultiplier(badge.types());
        validateDropRate(effectiveRate, "Effective badge drop rate for " + badge.id());
        return effectiveRate;
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
