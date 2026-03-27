package com.sanguiwara.timeevent;


import com.sanguiwara.repository.GameTimeEventRepository;
import com.sanguiwara.repository.TrainingTimeEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EventManager is responsible for:
 * - storing all scheduled events
 * - executing events when they become due
 * <p>
 * Designed for a "simulation/game loop" style:
 * you call {@link #runDueEvents(Instant)} whenever your game time advances.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class EventManager {

    private final GameTimeEventRepository gameTimeEventRepository;
    private final TrainingTimeEventRepository trainingTimeEventRepository;


    public void loadEventsFromDatabase() {
        gameTimeEventRepository.findAll().forEach(this::schedule);
        trainingTimeEventRepository.findAll().forEach(this::schedule);
    }


    private final PriorityQueue<TimeEvent> queue = new PriorityQueue<>(
            Comparator.comparing(TimeEvent::getExecuteAt).thenComparing(TimeEvent::getId)
    );

    private final Map<UUID, TimeEvent> eventById = new HashMap<>();

    /**
     * Schedule an event.
     */
    public void schedule(TimeEvent timeEvent) {
        Objects.requireNonNull(timeEvent, "event");

        eventById.put(timeEvent.getId(), timeEvent);
        queue.add(timeEvent);
    }


    /**
     * Cancel a scheduled event by id. Returns true if it existed.
     */
    public void cancel(UUID eventId) {
        TimeEvent existing = eventById.remove(eventId);
        if (existing == null) return;
        queue.remove(existing);
    }

    /**
     * Returns the next event to execute (without removing it).
     */
    public Optional<TimeEvent> returnNext() {
        return Optional.ofNullable(queue.peek());
    }

    /**
     * Returns number of scheduled events.
     */
    public int size() {
        return queue.size();
    }

    /**
     * List all scheduled events ordered by executeAt.
     */
    public List<TimeEvent> listAllOrdered() {
        // Copy to avoid mutating the queue order
        return queue.stream()
                .sorted(Comparator.comparing(TimeEvent::getExecuteAt).thenComparing(TimeEvent::getId))
                .collect(Collectors.toList());
    }

    /**
     * Execute all events with executeAt <= now.
     * Returns how many were executed.
     * <p>
     * IMPORTANT: This method is synchronous. It executes immediately.
     */
    public int runDueEvents(Instant now) {
        Objects.requireNonNull(now, "now");

        int executed = 0;

        while (true) {
            TimeEvent next = queue.peek();
            if (next == null) break;

            if (next.getExecuteAt().isAfter(now)) break;

            // Remove from structures first (so re-scheduling inside execute() works cleanly)
            queue.poll();
            eventById.remove(next.getId());

            try {
                next.execute();
                switch (next) {
                    case GameTimeEvent e -> gameTimeEventRepository.deleteById(e.getId());
                    case TrainingTimeEvent e -> trainingTimeEventRepository.deleteById(e.getId());
                    default -> {
                        // If you have other event types, handle their persistence here.
                    }
                }
            } catch (Exception e) {
                // Decide your policy:
                // - swallow and continue (game keeps running)
                // - or rethrow to stop everything
                // For a simulation, continuing is often better.
                log.error(
                        "Error while executing time event id={} type={} executeAt={}",
                        next.getId(),
                        next.getClass().getSimpleName(),
                        next.getExecuteAt(),
                        e
                );
            }

            executed++;
        }

        return executed;
    }

}
