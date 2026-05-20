package com.sanguiwara.baserecords;

import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.training.TrainingProgression;
import com.sanguiwara.progression.training.TrainingProgressions;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Training {

    private final UUID id;
    private final Instant executeAt;
    private final Team team;
    private final TrainingProgression trainingProgression;

    @Setter
    private List<PlayerProgression> playerProgressions = new ArrayList<>();

    public Training(UUID id, Instant executeAt, Team team, TrainingType trainingType) {
        this.id = id;
        this.executeAt = executeAt;
        this.team = team;
        this.trainingProgression = Objects.requireNonNull(
                TrainingProgressions.defaultFor(trainingType),
                "trainingProgression"
        );
    }

    public TrainingType getTrainingType() {
        return trainingProgression.type();
    }

}
