package com.sanguiwara.progression.training;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.MoraleDeltaScaler;
import com.sanguiwara.progression.ProgressionSkillGroup;
import com.sanguiwara.progression.archetype.PlayerArchetypeDefinition;
import com.sanguiwara.progression.archetype.PlayerArchetypes;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * Applies training effects to a player: stat progression + potential badge unlocks.
 * Random is injected to allow determinism via seed in tests/dev.
 */
public final class TrainingEngine {

    private static final int MIN_SKILL_VALUE = 1;
    private static final int MAX_SKILL_VALUE = 99;
    private static final double MAX_GAIN_AT_MIN_SKILL = 20.0;
    private static final double ELITE_ONE_POINT_SKILL = 90.0;
    private static final double IMPACT_SATURATION = 1.30;
    private static final double TRAINING_VARIANCE = 0.25;
    private static final double SKILL_CURVE_EXPONENT =
            Math.log(1.0 / MAX_GAIN_AT_MIN_SKILL)
                    / Math.log((MAX_SKILL_VALUE - ELITE_ONE_POINT_SKILL)
                    / (double) (MAX_SKILL_VALUE - MIN_SKILL_VALUE));

    private final Random random;

    public TrainingEngine(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public void applyTraining(TrainingProgression training, Player player) {
        Objects.requireNonNull(training, "training");
        Objects.requireNonNull(player, "player");
        PlayerArchetypeDefinition archetype = PlayerArchetypes.definitionFor(player.getArchetype());
        training.apply(player, archetype, this);
    }

    void applySkillRolls(TrainingType trainingType, PlayerArchetypeDefinition archetype, Player player, List<TrainableSkill> skills, int minDelta, int maxDelta) {
        applySkillRolls(trainingType, archetype, player, skills, minDelta, maxDelta, 1.0);
    }

    void applySkillRolls(
            TrainingType trainingType,
            PlayerArchetypeDefinition archetype,
            Player player,
            List<TrainableSkill> skills,
            int minDelta,
            int maxDelta,
            double progressionMultiplier
    ) {
        if (skills.isEmpty()) {
            return;
        }
        validateProgressionMultiplier(progressionMultiplier);

        for (TrainableSkill ref : skills) {
            int baseDelta = rollDelta(minDelta, maxDelta);
            double multiplier = archetype.trainingMultiplier(trainingType, ref.group()) * progressionMultiplier;
            int current = ref.get().applyAsInt(player);
            int delta = roundExpectedDelta(expectedTrainingDelta(current, baseDelta, multiplier));
            ref.set().accept(player, current + delta);
        }
    }

    void applyMoraleRoll(TrainingType trainingType, PlayerArchetypeDefinition archetype, Player player, int minDelta, int maxDelta) {
        int baseDelta = rollDelta(minDelta, maxDelta);
        double multiplier = archetype.trainingMultiplier(trainingType, ProgressionSkillGroup.MORALE);
        player.setMorale(MoraleDeltaScaler.applyDelta(
                player.getMorale(),
                roundExpectedDelta(baseDelta * multiplier),
                MIN_SKILL_VALUE,
                MAX_SKILL_VALUE
        ));
    }

    void applyBadgeUnlock(PlayerArchetypeDefinition archetype, Player player, Set<ModifierType> eligibleTypes) {
        applyBadgeUnlock(archetype, player, eligibleTypes, 1.0);
    }

    void applyBadgeUnlock(PlayerArchetypeDefinition archetype, Player player, Set<ModifierType> eligibleTypes, double dropRateMultiplier) {
        if (eligibleTypes.isEmpty()) {
            return;
        }
        validateRateMultiplier(dropRateMultiplier);

        Set<Long> badgeIds = player.getBadgeIds();
        if (badgeIds == null) {
            badgeIds = new HashSet<>();
            player.setBadgeIds(badgeIds);
        }

        for (Badge badge : BadgeCatalog.badgeMap().values()) {
            if (badgeIds.contains(badge.id())) continue;
            if (!intersects(badge.types(), eligibleTypes)) continue;

            if (random.nextDouble() < archetype.effectiveBadgeDropRate(badge) * dropRateMultiplier) {
                badgeIds.add(badge.id());
            }
        }
    }

    private int rollDelta(int minInclusive, int maxInclusive) {
        return minInclusive + random.nextInt(maxInclusive - minInclusive + 1);
    }

    private int roundExpectedDelta(double expectedDelta) {
        int whole = (int) Math.floor(expectedDelta);
        double fraction = expectedDelta - whole;
        if (fraction == 0.0) {
            return whole;
        }
        return whole + (random.nextDouble() < fraction ? 1 : 0);
    }

    private double expectedTrainingDelta(int currentSkill, int baseDelta, double multiplier) {
        validateSkill(currentSkill);

        double skillCeiling = skillProgressionCeiling(currentSkill);
        double impactRatio = progressionImpactRatio(baseDelta, multiplier);
        double variedImpactRatio = applyLightVariance(impactRatio);

        return skillCeiling * variedImpactRatio;
    }

    private static double skillProgressionCeiling(int currentSkill) {
        double remainingRatio = (MAX_SKILL_VALUE - currentSkill)
                / (double) (MAX_SKILL_VALUE - MIN_SKILL_VALUE);
        return MAX_GAIN_AT_MIN_SKILL * Math.pow(remainingRatio, SKILL_CURVE_EXPONENT);
    }

    private static double progressionImpactRatio(int baseDelta, double multiplier) {
        double existingImpact = baseDelta * multiplier;
        return 1.0 - Math.exp(-existingImpact / IMPACT_SATURATION);
    }

    private double applyLightVariance(double impactRatio) {
        double centeredRoll = random.nextDouble() * 2.0 - 1.0;
        double variance = TRAINING_VARIANCE * impactRatio * (1.0 - impactRatio);
        return impactRatio + centeredRoll * variance;
    }

    private static void validateSkill(int value) {
        if (value < MIN_SKILL_VALUE || value > MAX_SKILL_VALUE) {
            throw new IllegalArgumentException("Skill value must be between 1 and 99: " + value);
        }
    }

    private static void validateProgressionMultiplier(double progressionMultiplier) {
        if (!Double.isFinite(progressionMultiplier) || progressionMultiplier <= 0.0) {
            throw new IllegalArgumentException("Progression multiplier must be positive: " + progressionMultiplier);
        }
    }

    private static void validateRateMultiplier(double dropRateMultiplier) {
        if (!Double.isFinite(dropRateMultiplier) || dropRateMultiplier <= 0.0 || dropRateMultiplier > 1.0) {
            throw new IllegalArgumentException("Drop rate multiplier must be between 0 and 1: " + dropRateMultiplier);
        }
    }

    private static boolean intersects(Set<ModifierType> a, Set<ModifierType> b) {
        for (ModifierType t : a) {
            if (b.contains(t)) return true;
        }
        return false;
    }
}
