package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "league_seasons")
public class LeagueSeasonEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "year", nullable = false)
    private int year;

    @ManyToOne(optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    private LeagueEntity league;

    @ManyToMany
    @JoinTable(
            name = "league_season_teams",
            joinColumns = @JoinColumn(name = "league_season_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<TeamEntity> teams = new HashSet<>();
}
