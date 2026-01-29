package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "game_time_events")
public class GameTimeEventEntity {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "execute_at", nullable = false)
    private Instant executeAt;

    @Column(name = "game_id", columnDefinition = "uuid", nullable = false)
    private UUID gameId;
}
