package com.sanguiwara.timeevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class CronTimeEvent {

    private final EventManager eventManager;

    @EventListener(ApplicationReadyEvent.class)
    public void runAtStartup() {
        executeDueEvents("startup");
    }

    //@Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "0 */5 * * * *")
    public void runEveryFiveMinutes() {
        executeDueEvents("cron");
    }

    private void executeDueEvents(String trigger) {
        Instant now = Instant.now();
        int executed = eventManager.runDueEvents(now);
        if (executed > 0) {
            log.info("Executed {} due time events at {} (trigger={})", executed, now, trigger);
        }
    }
}
