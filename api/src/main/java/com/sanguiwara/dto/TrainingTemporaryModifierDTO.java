package com.sanguiwara.dto;

import com.sanguiwara.badges.ModifierOp;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.badges.Target;

public record TrainingTemporaryModifierDTO(
        ModifierType effectType,
        Target target,
        ModifierOp op,
        double value,
        int gamesRemaining
) {
}
