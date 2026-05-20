package com.sanguiwara.progression.archetype;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.ModifierType;
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
        PlayerArchetypeDefinition archetype = PlayerArchetypes.definitionFor(PlayerArchetype.THREE_POINT_SHOOTER);
        Badge badge = badge(0.10, ModifierType.THREE_POINT, ModifierType.DRIVE);

        assertThat(archetype.badgeDropRateMultiplier(badge.types())).isEqualTo(1.50);
        assertThat(archetype.effectiveBadgeDropRate(badge)).isCloseTo(0.15, within(0.0000001));
    }

    @Test
    void badgeDropRateMultiplier_defaultsToOneForUnconfiguredType() {
        PlayerArchetypeDefinition archetype = PlayerArchetypes.definitionFor(PlayerArchetype.THREE_POINT_SHOOTER);

        assertThat(archetype.badgeDropRateMultiplier(EnumSet.of(ModifierType.ASSIST))).isEqualTo(1.0);
    }

    @Test
    void effectiveBadgeDropRate_keepsZeroDropRateAtZero() {
        PlayerArchetypeDefinition archetype = PlayerArchetypes.definitionFor(PlayerArchetype.THREE_POINT_SHOOTER);

        assertThat(archetype.effectiveBadgeDropRate(badge(0.0, ModifierType.THREE_POINT))).isZero();
    }

    @Test
    void effectiveBadgeDropRate_rejectsInvalidEffectiveRate() {
        PlayerArchetypeDefinition archetype = PlayerArchetypes.definitionFor(PlayerArchetype.THREE_POINT_SHOOTER);

        assertThatThrownBy(() -> archetype.effectiveBadgeDropRate(badge(0.80, ModifierType.THREE_POINT)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Effective badge drop rate");
    }

    @Test
    void definitionFor_resolvesEveryPlayerArchetype() {
        for (PlayerArchetype archetype : PlayerArchetype.values()) {
            PlayerArchetypeDefinition definition = PlayerArchetypes.definitionFor(archetype);

            assertThat(definition.type()).isEqualTo(archetype);
        }
    }

    private static Badge badge(double dropRate, ModifierType firstType, ModifierType... otherTypes) {
        return new StandardBadge(
                999L,
                "Test Badge",
                dropRate,
                EnumSet.of(firstType, otherTypes),
                new EnumMap<>(ModifierType.class)
        );
    }
}
