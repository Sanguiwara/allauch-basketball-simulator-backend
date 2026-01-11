package com.sanguiwara.timeevent;

import com.sanguiwara.executor.GameExecutor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public final class ExecuteGameTimeEvent implements TimeEvent {
    private final UUID id;
    private final Instant executeAt;
    private final UUID gameId;
    private final GameExecutor gameExecutor;


    @Override public UUID getId() { return id; }
    @Override public Instant getExecuteAt() { return executeAt; }

    @Override
    public void execute() {
        gameExecutor.executeGame(gameId);
    }
}