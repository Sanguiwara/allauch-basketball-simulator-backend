package com.sanguiwara.progression;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeType;
import com.sanguiwara.badges.StandardBadge;
import com.sanguiwara.factory.PlayerArchetype;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class ArchetypeBadgeDropRateTest {

    @Test
    void badgeDropRateMultiplier_usesStrongestMatchingTypeForMultiTypeBadge() {
        ArchetypeProgressionProfile profile = ArchetypeProgressionProfiles.forArchetype(PlayerArchetype.THREE_POINT_SHOOTER);
        Badge badge = badge(0.10, BadgeType.THREE_POINT, BadgeType.DRIVE);

        assertThat(profile.badgeDropRateMultiplier(badge.types())).isEqualTo(1.50);
        assertThat(profile.effectiveBadgeDropRate(badge)).isCloseTo(0.15, within(0.0000001));
    }

    @Test
    void badgeDropRateMultiplier_defaultsToOneForUnconfiguredType() {
        ArchetypeProgressionProfile profile = ArchetypeProgressionProfiles.forArchetype(PlayerArchetype.THREE_POINT_SHOOTER);

        assertThat(profile.badgeDropRateMultiplier(EnumSet.of(BadgeType.ASSIST))).isEqualTo(1.0);
    }

    @Test
    void effectiveBadgeDropRate_keepsZeroDropRateAtZero() {
        ArchetypeProgressionProfile profile = ArchetypeProgressionProfiles.forArchetype(PlayerArchetype.THREE_POINT_SHOOTER);

        assertThat(profile.effectiveBadgeDropRate(badge(0.0, BadgeType.THREE_POINT))).isZero();
    }

    @Test
    void effectiveBadgeDropRate_rejectsInvalidEffectiveRate() {
        ArchetypeProgressionProfile profile = ArchetypeProgressionProfiles.forArchetype(PlayerArchetype.THREE_POINT_SHOOTER);

        assertThatThrownBy(() -> profile.effectiveBadgeDropRate(badge(0.80, BadgeType.THREE_POINT)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Effective badge drop rate");
    }

    private static Badge badge(double dropRate, BadgeType firstType, BadgeType... otherTypes) {
        return new StandardBadge(
                999L,
                "Test Badge",
                dropRate,
                EnumSet.of(firstType, otherTypes),
                new EnumMap<>(BadgeType.class)
        );
    }
}
