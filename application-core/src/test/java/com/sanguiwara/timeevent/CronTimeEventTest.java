package com.sanguiwara.timeevent;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CronTimeEventTest {

    @Test
    void runAtStartup_runsDueEvents() {
        EventManager eventManager = mock(EventManager.class);
        when(eventManager.runDueEvents(any())).thenReturn(0);

        CronTimeEvent cronTimeEvent = new CronTimeEvent(eventManager);
        cronTimeEvent.runAtStartup();

        verify(eventManager).runDueEvents(any());
    }

    @Test
    void runEveryFiveMinutes_runsDueEvents() {
        EventManager eventManager = mock(EventManager.class);
        when(eventManager.runDueEvents(any())).thenReturn(0);

        CronTimeEvent cronTimeEvent = new CronTimeEvent(eventManager);
        cronTimeEvent.runEveryFiveMinutes();

        verify(eventManager).runDueEvents(any());
    }
}

