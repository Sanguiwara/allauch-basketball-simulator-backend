package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

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
    @UuidGenerator
    private UUID id;

    @Column(name = "year", nullable = false)
    private int year;

    @ManyToOne(optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    private LeagueEntity league;

    // LeagueSeasonEntity
    @OneToMany(mappedBy = "leagueSeason", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamSeasonEntity> teamSeasons = new HashSet<>();
}
