package com.sanguiwara.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Getter;

import com.sanguiwara.progression.ProgressionEventType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
public class PlayerProgressionId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "player_id", columnDefinition = "uuid", nullable = false)
    private UUID playerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private ProgressionEventType eventType;

    @Column(name = "event_id", columnDefinition = "uuid", nullable = false)
    private UUID eventId;

    protected PlayerProgressionId() {
    }

    public PlayerProgressionId(UUID playerId, ProgressionEventType eventType, UUID eventId) {
        this.playerId = playerId;
        this.eventType = eventType;
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerProgressionId other)) return false;
        return Objects.equals(playerId, other.playerId)
                && eventType == other.eventType
                && Objects.equals(eventId, other.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, eventType, eventId);
    }
}
