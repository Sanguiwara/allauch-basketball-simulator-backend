package com.sanguiwara.baserecords;

import com.sanguiwara.progression.PlayerProgression;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Training {


    private final UUID id;
    private final Instant executeAt;
    private final Team team;
    private final TrainingType trainingType;

    @Setter
    private List<PlayerProgression> playerProgressions = new ArrayList<>();


}
