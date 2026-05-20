package com.sanguiwara.progression.manager;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.factory.PlayerArchetype;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ArchetypeBadgeDropManagerTest {

    @Test
    void shootingBadgeDrop_usesArchetypeDropRateMultiplier() {
        Player shooter = basePlayer(PlayerArchetype.THREE_POINT_SHOOTER);
        Player allAround = basePlayer(PlayerArchetype.ALL_AROUND);
        Set<Long> randomThreePointBadgeIds = randomBadgeIdsFor(ModifierType.THREE_POINT);
        Set<Long> autoSkillBadgeIds = zeroDropRateBadgeIds();

        new ShootingSkillProgressionManager(constantDoubleRandom(0.012))
                .applyShootingAndAssistBadgeDrop(inGamePlayer(shooter));
        new ShootingSkillProgressionManager(constantDoubleRandom(0.012))
                .applyShootingAndAssistBadgeDrop(inGamePlayer(allAround));

        assertThat(shooter.getBadgeIds()).containsAll(randomThreePointBadgeIds);
        assertThat(allAround.getBadgeIds()).doesNotContainAnyElementsOf(randomThreePointBadgeIds);
        assertThat(shooter.getBadgeIds()).doesNotContainAnyElementsOf(autoSkillBadgeIds);
    }

    private static Set<Long> randomBadgeIdsFor(ModifierType badgeType) {
        return BadgeCatalog.badgeMap().values().stream()
                .filter(badge -> badge.dropRate() > 0.0)
                .filter(badge -> badge.types().contains(badgeType))
                .map(Badge::id)
                .collect(Collectors.toSet());
    }

    private static Set<Long> zeroDropRateBadgeIds() {
        return BadgeCatalog.badgeMap().values().stream()
                .filter(badge -> badge.dropRate() == 0.0)
                .map(Badge::id)
                .collect(Collectors.toSet());
    }

    private static InGamePlayer inGamePlayer(Player player) {
        return new InGamePlayer(player, UUID.randomUUID());
    }

    private static Player basePlayer(PlayerArchetype archetype) {
        return Player.builder()
                .teamsID(new HashSet<>())
                .clubID(UUID.randomUUID())
                .badgeIds(new HashSet<>())
                .id(UUID.randomUUID())
                .name("p")
                .birthDate(2000)
                .archetype(archetype)
                .injured(false)
                .build();
    }

    private static Random constantDoubleRandom(double value) {
        return new Random(0L) {
            @Override
            public double nextDouble() {
                return value;
            }
        };
    }
}
