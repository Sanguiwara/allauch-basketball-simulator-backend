package com.sanguiwara.timeevent;


import com.sanguiwara.repository.GameTimeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EventManager is responsible for:
 * - storing all scheduled events
 * - executing events when they become due
 *
 * Designed for a "simulation/game loop" style:
 * you call {@link #runDueEvents(Instant)} whenever your game time advances.
 */
@RequiredArgsConstructor
@Service
public class EventManager {

    private final GameTimeEventRepository gameTimeEventRepository;

    // Next events first
    private final PriorityQueue<TimeEvent> queue = new PriorityQueue<>(
            Comparator.comparing(TimeEvent::getExecuteAt).thenComparing(TimeEvent::getId)
    );

    // For fast cancel / lookup
    private final Map<UUID, TimeEvent> eventById = new HashMap<>();

    /** Schedule an event. */
    public void schedule(TimeEvent timeEvent) {
        Objects.requireNonNull(timeEvent, "event");

        eventById.put(timeEvent.getId(), timeEvent);
        queue.add(timeEvent);
    }

    public void populateFromDatabase() {


            gameTimeEventRepository.findAll().forEach(this::schedule);



    }

    /** Cancel a scheduled event by id. Returns true if it existed. */
    public boolean cancel(UUID eventId) {
        TimeEvent existing = eventById.remove(eventId);
        if (existing == null) return false;

        // PriorityQueue has no O(1) removal; this is acceptable for moderate sizes.
        // If you have tons of events, we can optimize with a "cancelled set" strategy.
        return queue.remove(existing);
    }

    /** Returns the next event to execute (without removing it). */
    public Optional<TimeEvent> returnNext() {
        return Optional.ofNullable(queue.peek());
    }

    /** Returns number of scheduled events. */
    public int size() {
        return queue.size();
    }

    /** List all scheduled events ordered by executeAt. */
    public List<TimeEvent> listAllOrdered() {
        // Copy to avoid mutating the queue order
        return queue.stream()
                .sorted(Comparator.comparing(TimeEvent::getExecuteAt).thenComparing(TimeEvent::getId))
                .collect(Collectors.toList());
    }

    /**
     * Execute all events with executeAt <= now.
     * Returns how many were executed.
     *
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
            } catch (Exception e) {
                // Decide your policy:
                // - swallow and continue (game keeps running)
                // - or rethrow to stop everything
                // For a simulation, continuing is often better.
            }

            executed++;
        }

        return executed;
    }

}
