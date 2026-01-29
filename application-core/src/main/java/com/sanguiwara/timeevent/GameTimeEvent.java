package com.sanguiwara.timeevent;

import com.sanguiwara.executor.GameExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public final class GameTimeEvent implements TimeEvent {
    private final UUID id;
    private final Instant executeAt;
    @Getter
    private final UUID gameId;
    private final GameExecutor gameExecutor;


    @Override public UUID getId() { return id; }
    @Override public Instant getExecuteAt() { return executeAt; }

    @Override
    public void execute() {
        gameExecutor.executeGame(gameId);
    }
}