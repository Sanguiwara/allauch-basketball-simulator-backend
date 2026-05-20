package com.sanguiwara.dto;

import com.sanguiwara.progression.ProgressionSkillGroup;
import com.sanguiwara.progression.training.TrainablePlayerStat;

public record TrainingProgressionImpactDTO(
        TrainablePlayerStat stat,
        String playerField,
        ProgressionSkillGroup skillGroup,
        int minDelta,
        int maxDelta,
        double progressionMultiplier
) {
}
