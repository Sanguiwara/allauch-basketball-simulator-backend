package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;


    @OneToOne(cascade = CascadeType.MERGE, orphanRemoval = true, optional = false)
    @JoinColumn(name = "home_plan_id", nullable = false, unique = true)
    private GamePlanEntity homeGamePlan;

    @OneToOne(cascade = CascadeType.MERGE, orphanRemoval = true, optional = false)
    @JoinColumn(name = "away_plan_id", nullable = false, unique = true)
    private GamePlanEntity awayGamePlan;

    @Column(name = "execute_at", nullable = false)
    private Instant executeAt;

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private GameResultEntity gameResult;

}
