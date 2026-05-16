package com.sanguiwara.progression.manager;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.progression.ArchetypeProgressionProfile;
import com.sanguiwara.progression.ArchetypeProgressionProfiles;
import com.sanguiwara.progression.ProgressionSkillGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * Applies training effects to a player: stat progression + potential badge unlocks.
 * Random is injected to allow determinism via seed in tests/dev.
 */
public final class TrainingProgressionManager {

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

    // Balanced training deltas (inclusive). All deltas remain within [1..3].
    // Intent: trainings affecting fewer skills can roll higher deltas.
    private static final int FIVE_STATS_MIN_DELTA = 1;
    private static final int FIVE_STATS_MAX_DELTA = 2;

    private static final int FOUR_STATS_MIN_DELTA = 1;
    private static final int FOUR_STATS_MAX_DELTA = 3;

    private static final int THREE_STATS_MIN_DELTA = 2;
    private static final int THREE_STATS_MAX_DELTA = 3;

    private static final int MORALE_MIN_DELTA = 1;
    private static final int MORALE_MAX_DELTA = 3;

    private final Random random;

    public TrainingProgressionManager(Random random) {
        this.random = random;
    }

    public void applyTraining(TrainingType trainingType, Player player) {
        ArchetypeProgressionProfile profile = ArchetypeProgressionProfiles.forArchetype(player.getArchetype());

        switch (trainingType) {
            case SHOOTING -> {
                applySkillRolls(trainingType, profile, player, List.of(
                        skill(ProgressionSkillGroup.THREE_POINT, Player::getTir3Pts, Player::setTir3Pts),
                        skill(ProgressionSkillGroup.TWO_POINT, Player::getTir2Pts, Player::setTir2Pts),
                        skill(ProgressionSkillGroup.FREE_THROW, Player::getLancerFranc, Player::setLancerFranc),
                        skill(ProgressionSkillGroup.FLOATER, Player::getFloater, Player::setFloater),
                        skill(ProgressionSkillGroup.DRIVE, Player::getFinitionAuCercle, Player::setFinitionAuCercle)
                ), FIVE_STATS_MIN_DELTA, FIVE_STATS_MAX_DELTA);
                applyBadgeUnlock(profile, player, Set.of(BadgeType.THREE_POINT, BadgeType.TWO_POINT, BadgeType.DRIVE));
            }
            case DEFENSE -> {
                applySkillRolls(trainingType, profile, player, List.of(
                        skill(ProgressionSkillGroup.PERIMETER_DEFENSE, Player::getDefExterieur, Player::setDefExterieur),
                        skill(ProgressionSkillGroup.INTERIOR_DEFENSE, Player::getDefPoste, Player::setDefPoste),
                        skill(ProgressionSkillGroup.RIM_PROTECTION, Player::getProtectionCercle, Player::setProtectionCercle),
                        skill(ProgressionSkillGroup.STEAL, Player::getSteal, Player::setSteal),
                        skill(ProgressionSkillGroup.BLOCK, Player::getTimingBlock, Player::setTimingBlock)
                ), FIVE_STATS_MIN_DELTA, FIVE_STATS_MAX_DELTA);
                applyBadgeUnlock(profile, player, Set.of(BadgeType.STEAL));
            }
            case PHYSICAL -> applySkillRolls(trainingType, profile, player, List.of(
                    skill(ProgressionSkillGroup.ATHLETIC, Player::getPhysique, Player::setPhysique),
                    skill(ProgressionSkillGroup.ATHLETIC, Player::getSpeed, Player::setSpeed),
                    skill(ProgressionSkillGroup.ATHLETIC, Player::getEndurance, Player::setEndurance),
                    skill(ProgressionSkillGroup.ATHLETIC, Player::getSolidite, Player::setSolidite)
            ), FOUR_STATS_MIN_DELTA, FOUR_STATS_MAX_DELTA);
            case PLAYMAKING -> {
                applySkillRolls(trainingType, profile, player, List.of(
                        skill(ProgressionSkillGroup.DRIVE, Player::getBallhandling, Player::setBallhandling),
                        skill(ProgressionSkillGroup.PLAYMAKING, Player::getPassingSkills, Player::setPassingSkills),
                        skill(ProgressionSkillGroup.MENTAL, Player::getBasketballIqOff, Player::setBasketballIqOff),
                        skill(ProgressionSkillGroup.MENTAL, Player::getIq, Player::setIq)
                ), FOUR_STATS_MIN_DELTA, FOUR_STATS_MAX_DELTA);
                applyBadgeUnlock(profile, player, Set.of(BadgeType.ASSIST));
            }
            case MORALE -> {
                int baseDelta = rollDelta(MORALE_MIN_DELTA, MORALE_MAX_DELTA);
                double multiplier = profile.trainingMultiplier(trainingType, ProgressionSkillGroup.MORALE);
                player.setMorale(MoraleDeltaScaler.applyDelta(
                        player.getMorale(),
                        roundExpectedDelta(baseDelta * multiplier),
                        MIN_SKILL_VALUE,
                        MAX_SKILL_VALUE
                ));
            }
            case TACTICAL -> applySkillRolls(trainingType, profile, player, List.of(
                    skill(ProgressionSkillGroup.MENTAL, Player::getBasketballIqOff, Player::setBasketballIqOff),
                    skill(ProgressionSkillGroup.MENTAL, Player::getBasketballIqDef, Player::setBasketballIqDef),
                    skill(ProgressionSkillGroup.MENTAL, Player::getIq, Player::setIq)
            ), THREE_STATS_MIN_DELTA, THREE_STATS_MAX_DELTA);
        }
    }

    private void applyBadgeUnlock(ArchetypeProgressionProfile profile, Player player, Set<BadgeType> eligibleTypes) {
        if (eligibleTypes == null || eligibleTypes.isEmpty()) {
            return;
        }

        Set<Long> badgeIds = player.getBadgeIds();
        if (badgeIds == null) {
            badgeIds = new HashSet<>();
            player.setBadgeIds(badgeIds);
        }

        for (Badge badge : BadgeCatalog.badgeMap().values()) {
            if (badgeIds.contains(badge.id())) continue;
            if (!intersects(badge.types(), eligibleTypes)) continue;

            if (random.nextDouble() < profile.effectiveBadgeDropRate(badge)) {
                badgeIds.add(badge.id());
            }
        }
    }

    private static boolean intersects(Set<BadgeType> a, Set<BadgeType> b) {
        for (BadgeType t : a) {
            if (b.contains(t)) return true;
        }
        return false;
    }

    private void applySkillRolls(TrainingType trainingType, ArchetypeProgressionProfile profile, Player player, List<SkillRef> skills, int minDelta, int maxDelta) {
        if (skills == null || skills.isEmpty()) {
            return;
        }

        for (SkillRef ref : skills) {
            int baseDelta = rollDelta(minDelta, maxDelta);
            double multiplier = profile.trainingMultiplier(trainingType, ref.group);
            int current = ref.get.applyAsInt(player);
            int delta = roundExpectedDelta(expectedTrainingDelta(current, baseDelta, multiplier));
            ref.set.accept(player, current + delta);
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

    private static SkillRef skill(ProgressionSkillGroup group, ToIntFunction<Player> get, ObjIntConsumer<Player> set) {
        return new SkillRef(group, get, set);
    }

    private record SkillRef(ProgressionSkillGroup group, ToIntFunction<Player> get, ObjIntConsumer<Player> set) {}

}
