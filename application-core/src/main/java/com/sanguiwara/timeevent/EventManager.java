package com.sanguiwara.timeevent;


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
public class EventManager {

    // Next events first
    private final PriorityQueue<TimeEvent> queue = new PriorityQueue<>(
            Comparator.comparing(TimeEvent::getExecuteAt).thenComparing(TimeEvent::getId)
    );

    // For fast cancel / lookup
    private final Map<UUID, TimeEvent> eventById = new HashMap<>();

    /** Schedule an event. */
    public void schedule(TimeEvent timeEvent) {
        Objects.requireNonNull(timeEvent, "event");

        if (eventById.containsKey(timeEvent.getId())) {
            throw new IllegalArgumentException("Event id already scheduled: " + timeEvent.getId());
        }
        eventById.put(timeEvent.getId(), timeEvent);
        queue.add(timeEvent);
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

    /**
     * Convenience: advance game time to `newTime` and run due events.
     * If you track game time elsewhere, you can ignore this method.
     */
    public int advanceTo(Instant newTime) {
        return runDueEvents(newTime);
    }
}
