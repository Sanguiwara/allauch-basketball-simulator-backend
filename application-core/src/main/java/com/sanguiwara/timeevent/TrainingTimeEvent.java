package com.sanguiwara.timeevent;

import com.sanguiwara.executor.TrainingExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class TrainingTimeEvent implements TimeEvent{
    private final UUID id;
    private final Instant executeAt;
    private final UUID trainingId;
    private final TrainingExecutor trainingExecutor;


    @Override public UUID getId() { return id; }
    @Override public Instant getExecuteAt() { return executeAt; }

    @Override
    public void execute() {
        trainingExecutor.executeTraining(trainingId);
    }
}
