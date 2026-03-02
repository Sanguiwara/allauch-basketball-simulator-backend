package com.sanguiwara.progression.manager;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.TrainingType;

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


        switch (trainingType) {
            case SHOOTING -> {
                applySkillRolls(player, List.of(
                        skill(Player::getTir3Pts, Player::setTir3Pts),
                        skill(Player::getTir2Pts, Player::setTir2Pts),
                        skill(Player::getLancerFranc, Player::setLancerFranc),
                        skill(Player::getFloater, Player::setFloater),
                        skill(Player::getFinitionAuCercle, Player::setFinitionAuCercle)
                ), FIVE_STATS_MIN_DELTA, FIVE_STATS_MAX_DELTA);
                applyBadgeUnlock(player, Set.of(BadgeType.THREE_POINT, BadgeType.TWO_POINT, BadgeType.DRIVE));
            }
            case DEFENSE -> {
                applySkillRolls(player, List.of(
                        skill(Player::getDefExterieur, Player::setDefExterieur),
                        skill(Player::getDefPoste, Player::setDefPoste),
                        skill(Player::getProtectionCercle, Player::setProtectionCercle),
                        skill(Player::getSteal, Player::setSteal),
                        skill(Player::getTimingBlock, Player::setTimingBlock)
                ), FIVE_STATS_MIN_DELTA, FIVE_STATS_MAX_DELTA);
                applyBadgeUnlock(player, Set.of(BadgeType.STEAL));
            }
            case PHYSICAL -> applySkillRolls(player, List.of(
                    skill(Player::getPhysique, Player::setPhysique),
                    skill(Player::getSpeed, Player::setSpeed),
                    skill(Player::getEndurance, Player::setEndurance),
                    skill(Player::getSolidite, Player::setSolidite)
            ), FOUR_STATS_MIN_DELTA, FOUR_STATS_MAX_DELTA);
            case PLAYMAKING -> {
                applySkillRolls(player, List.of(
                        skill(Player::getBallhandling, Player::setBallhandling),
                        skill(Player::getPassingSkills, Player::setPassingSkills),
                        skill(Player::getBasketballIqOff, Player::setBasketballIqOff),
                        skill(Player::getIq, Player::setIq)
                ), FOUR_STATS_MIN_DELTA, FOUR_STATS_MAX_DELTA);
                applyBadgeUnlock(player, Set.of(BadgeType.ASSIST));
            }
            case MORALE -> player.setMorale(clampSkill(player.getMorale() + rollDelta(MORALE_MIN_DELTA, MORALE_MAX_DELTA)));
            case TACTICAL -> applySkillRolls(player, List.of(
                    skill(Player::getBasketballIqOff, Player::setBasketballIqOff),
                    skill(Player::getBasketballIqDef, Player::setBasketballIqDef),
                    skill(Player::getIq, Player::setIq)
            ), THREE_STATS_MIN_DELTA, THREE_STATS_MAX_DELTA);
        }
    }

    private void applyBadgeUnlock(Player player, Set<BadgeType> eligibleTypes) {
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

            if (random.nextDouble() < badge.dropRate()) {
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

    private void applySkillRolls(Player player, List<SkillRef> skills, int minDelta, int maxDelta) {
        if (skills == null || skills.isEmpty()) {
            return;
        }

        for (SkillRef ref : skills) {
            int delta = rollDelta(minDelta, maxDelta);
            int current = ref.get.applyAsInt(player);
            ref.set.accept(player, clampSkill(current + delta));
        }
    }

    private int rollDelta(int minInclusive, int maxInclusive) {
        return minInclusive + random.nextInt(maxInclusive - minInclusive + 1);
    }

    private static SkillRef skill(ToIntFunction<Player> get, ObjIntConsumer<Player> set) {
        return new SkillRef(get, set);
    }

    private record SkillRef(ToIntFunction<Player> get, ObjIntConsumer<Player> set) {}

    private static int clampSkill(int value) {
        return Math.clamp(value, MIN_SKILL_VALUE, MAX_SKILL_VALUE);
    }

}
