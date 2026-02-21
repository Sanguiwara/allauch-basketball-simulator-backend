package com.sanguiwara.dto;

import com.sanguiwara.baserecords.TrainingType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TrainingDTO(
        UUID id,
        Instant executeAt,
        TrainingType trainingType,
        TeamDTO team,
        List<PlayerProgressionDTO> playerProgressions
) {
}

