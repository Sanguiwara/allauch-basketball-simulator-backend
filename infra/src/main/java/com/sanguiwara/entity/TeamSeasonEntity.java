package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "team_season")
@Getter
@Setter
public class TeamSeasonEntity {
    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)

    private UUID id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    @ManyToOne(optional = false)
    @JoinColumn(name = "league_season_id", nullable = false)
    private LeagueSeasonEntity leagueSeason;

    @Column(nullable = false)
    private Integer season;
}
