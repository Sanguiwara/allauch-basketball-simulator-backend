package com.sanguiwara.entity;

import com.sanguiwara.baserecords.Position;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "gameplans")
public class GamePlanEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_home_id", nullable = false)
    private TeamEntity teamHome;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_visitor_id", nullable = false)
    private TeamEntity teamVisitor;

    /**
     * Les joueurs actifs du match (roster in-game).
     * Si InGamePlayer est une entity, mappe en OneToMany.
     * Sinon, fais-en un @Embeddable (value object).
     */
    @OneToMany(mappedBy = "gamePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InGamePlayerEntity> activePlayers = new ArrayList<>();

    /**
     * Matchups Player -> Player.
     * En JPA, une Map d'Entity vers Entity se mappe via une table de jointure.
     */
    @ManyToMany
    @JoinTable(
            name = "game_matchups",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_defender_id")
    )
    @MapKeyJoinColumn(name = "player_attacker_id")
    private Map<PlayerEntity, PlayerEntity> matchups = new HashMap<>();

    /**
     * Position -> InGamePlayer.
     * Ici Position est probablement un enum : parfait pour ElementCollection + Map.
     * Et InGamePlayer est une entity : on stocke une FK.
     */
    @ManyToMany
    @JoinTable(
            name = "game_positions",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "in_game_player_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "position")
    private Map<Position, InGamePlayerEntity> positions = new EnumMap<>(Position.class);


}






