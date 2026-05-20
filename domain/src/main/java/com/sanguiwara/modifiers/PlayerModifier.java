package com.sanguiwara.modifiers;

import com.sanguiwara.badges.Modifier;
import com.sanguiwara.badges.ModifierOp;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.badges.Target;

import java.util.Objects;
import java.util.Optional;

public record PlayerModifier(
        ModifierType effectType,
        Target target,
        ModifierOp op,
        double value,
        int gamesRemaining
) {
    public PlayerModifier {
        Objects.requireNonNull(effectType, "effectType");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(op, "op");
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Modifier value must be finite: " + value);
        }
        if (gamesRemaining <= 0) {
            throw new IllegalArgumentException("Modifier gamesRemaining must be positive: " + gamesRemaining);
        }
    }

    public static PlayerModifier nextGameThreePointShotPctBonus(double value) {
        return new PlayerModifier(ModifierType.THREE_POINT, Target.SHOT_PCT, ModifierOp.ADD, value, 1);
    }

    public boolean appliesTo(ModifierType requestedEffectType, Target requestedTarget) {
        return effectType == requestedEffectType && target == requestedTarget;
    }

    public Modifier modifier() {
        return new Modifier(target, op, value);
    }

    public Optional<PlayerModifier> afterGame() {
        int remaining = gamesRemaining - 1;
        if (remaining <= 0) {
            return Optional.empty();
        }
        return Optional.of(new PlayerModifier(effectType, target, op, value, remaining));
    }
}
