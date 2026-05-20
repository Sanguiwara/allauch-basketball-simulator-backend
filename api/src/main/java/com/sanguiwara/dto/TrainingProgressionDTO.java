package com.sanguiwara.dto;

import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.baserecords.TrainingType;

import java.util.List;

public record TrainingProgressionDTO(
        TrainingType type,
        List<TrainingProgressionImpactDTO> statImpacts,
        List<ModifierType> badgeModifierTypes,
        double badgeDropRateMultiplier,
        List<TrainingTemporaryModifierDTO> temporaryModifiers
) {
}
