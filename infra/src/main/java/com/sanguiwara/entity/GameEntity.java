package com.sanguiwara.entity;

import com.sanguiwara.baserecords.GamePlan;
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


    @ManyToOne(optional = false)
    @JoinColumn(name = "home_team_id", nullable = false)
    private GamePlanEntity homeGamePlan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "away_team_id", nullable = false)
    private GamePlanEntity awayGamePlan;

}
