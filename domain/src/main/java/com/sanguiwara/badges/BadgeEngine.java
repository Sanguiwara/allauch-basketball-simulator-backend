package com.sanguiwara.badges;

import com.sanguiwara.baserecords.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class BadgeEngine {

    public double apply(Player player, BadgeType badgeType, Target target, double baseValue, Context context) {
        if (player == null) return baseValue;
        Set<Long> badgeIds = player.getBadgeIds();
        if (badgeIds == null || badgeIds.isEmpty()) return baseValue;

        List<Badge> badges = new ArrayList<>(badgeIds.size());
        for (Long id : badgeIds) {

            Badge badge = BadgeCatalog.badgeMap().get(id);
            if (badge != null) {
                badges.add(badge);
            }

        }

        double value = baseValue;
        for (Badge badge : badges) {
            if (!badge.types().contains(badgeType)) continue;
            List<Modifier> modifiers = badge.modifiersFor(badgeType, context);

            for (Modifier modifier : modifiers) {
                if (modifier.target() != target) continue;
                value = applyModifier(value, modifier);
            }
        }
        return value;
    }

    private static double applyModifier(double value, Modifier modifier) {
        return switch (modifier.op()) {
            case ADD -> value + modifier.value();
            case MUL -> value * modifier.value();
        };
    }
}
