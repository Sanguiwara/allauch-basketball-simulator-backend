package com.sanguiwara.timeevent;

import com.sanguiwara.executor.GameExecutor;
import com.sanguiwara.executor.TrainingExecutor;
import com.sanguiwara.repository.GameTimeEventRepository;
import com.sanguiwara.repository.TrainingTimeEventRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class EventManagerTest {

    @Test
    void runDueEvents_executesAndDeletesPersistedEvents() {
        GameTimeEventRepository gameTimeEventRepository = mock(GameTimeEventRepository.class);
        TrainingTimeEventRepository trainingTimeEventRepository = mock(TrainingTimeEventRepository.class);
        EventManager eventManager = new EventManager(gameTimeEventRepository, trainingTimeEventRepository);

        GameExecutor gameExecutor = mock(GameExecutor.class);
        TrainingExecutor trainingExecutor = mock(TrainingExecutor.class);

        UUID gameEventId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID trainingEventId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000010");
        UUID trainingId = UUID.fromString("00000000-0000-0000-0000-000000000020");

        Instant t0 = Instant.parse("2026-03-28T10:00:00Z");
        eventManager.schedule(new GameTimeEvent(gameEventId, t0, gameId, gameExecutor));
        eventManager.schedule(new TrainingTimeEvent(trainingEventId, t0, trainingId, trainingExecutor));

        int executed = eventManager.runDueEvents(t0);

        assertThat(executed).isEqualTo(2);
        assertThat(eventManager.size()).isZero();

        verify(gameExecutor).executeGame(gameId);
        verify(trainingExecutor).executeTraining(trainingId);
        verify(gameTimeEventRepository).deleteById(gameEventId);
        verify(trainingTimeEventRepository).deleteById(trainingEventId);
    }

    @Test
    void runDueEvents_doesNotExecuteFutureEvents() {
        GameTimeEventRepository gameTimeEventRepository = mock(GameTimeEventRepository.class);
        TrainingTimeEventRepository trainingTimeEventRepository = mock(TrainingTimeEventRepository.class);
        EventManager eventManager = new EventManager(gameTimeEventRepository, trainingTimeEventRepository);

        GameExecutor gameExecutor = mock(GameExecutor.class);
        UUID gameEventId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000030");

        Instant executeAt = Instant.parse("2026-03-28T10:00:01Z");
        eventManager.schedule(new GameTimeEvent(gameEventId, executeAt, gameId, gameExecutor));

        int executed = eventManager.runDueEvents(Instant.parse("2026-03-28T10:00:00Z"));

        assertThat(executed).isZero();
        assertThat(eventManager.size()).isEqualTo(1);
        verifyNoInteractions(gameExecutor);
        verifyNoInteractions(gameTimeEventRepository);
        verifyNoInteractions(trainingTimeEventRepository);
    }
}

