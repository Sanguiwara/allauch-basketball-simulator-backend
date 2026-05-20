package com.sanguiwara.modifiers;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.badges.Context;
import com.sanguiwara.badges.Modifier;
import com.sanguiwara.badges.Target;
import com.sanguiwara.baserecords.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PlayerModifierEngine {

    public double apply(Player player, ModifierType effectType, Target target, double baseValue, Context context) {
        if (player == null) return baseValue;
        Set<Long> badgeIds = player.getBadgeIds();
        double value = baseValue;

        if (badgeIds != null && !badgeIds.isEmpty()) {
            List<Badge> badges = new ArrayList<>(badgeIds.size());
            for (Long id : badgeIds) {
                Badge badge = BadgeCatalog.badgeMap().get(id);
                if (badge != null) {
                    badges.add(badge);
                }
            }

            for (Badge badge : badges) {
                if (!badge.types().contains(effectType)) continue;
                List<Modifier> modifiers = badge.modifiersFor(effectType, context);

                for (Modifier modifier : modifiers) {
                    if (modifier.target() != target) continue;
                    value = applyModifier(value, modifier);
                }
            }
        }
        return applyTemporaryModifiers(player, effectType, target, value);
    }

    private static double applyTemporaryModifiers(Player player, ModifierType effectType, Target target, double baseValue) {
        Set<PlayerModifier> modifiers = player.getTemporaryModifiers();
        if (modifiers == null || modifiers.isEmpty()) {
            return baseValue;
        }

        double value = baseValue;
        for (PlayerModifier modifier : modifiers) {
            if (!modifier.appliesTo(effectType, target)) continue;
            value = applyModifier(value, modifier.modifier());
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
