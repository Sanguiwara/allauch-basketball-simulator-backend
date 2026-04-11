package com.sanguiwara.entity;

import com.sanguiwara.baserecords.DefenseType;
import com.sanguiwara.baserecords.Position;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "gameplans")
public class GamePlanEntity {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_home_id", nullable = false)
    private TeamEntity ownerTeam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_visitor_id", nullable = false)
    private TeamEntity opponentTeam;

    @Enumerated(EnumType.STRING)
    @Column(name = "defense_type", nullable = false)
    private DefenseType defenseType = DefenseType.MAN_TO_MAN;

    /**
     * Les joueurs actifs du match (roster in-game).
     * Si InGamePlayer est une entity, mappe en OneToMany.
     * Sinon, fais-en un @Embeddable (value object).
     */
    @OneToMany(mappedBy = "gamePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InGamePlayerEntity> activePlayers = new ArrayList<>();

    /**
     * Persistence representation for matchups.
     * Current schema stores attacker -> defender, even though domain/API use defender -> attacker.
     */
    @ManyToMany
    @JoinTable(
            name = "game_matchups",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_defender_id")
    )
    //TODO INVERSER LA TABLE POUR QUE CA SOIT DEFENDER ATTACKER, ET QU'ON AIT LES BONS NOMS
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
    @MapKeyColumn(name = "position_code")
    private Map<Position, InGamePlayerEntity> positions = new EnumMap<>(Position.class);

    // ---- Shares & totals (doivent refléter GamePlan du domaine) ----
    @Column(name = "three_pt_attempt_share", nullable = false)
    private double threePointAttemptShare = 1.0 / 3.0;

    @Column(name = "mid_range_attempt_share", nullable = false)
    private double midRangeAttemptShare = 1.0 / 3.0;

    @Column(name = "drive_attempt_share", nullable = false)
    private double driveAttemptShare = 1.0 / 3.0;

    @Column(name = "total_shot_number", nullable = false)
    private int totalShotNumber = 75;

    @Column(name = "block_score", nullable = false)
    private double blockScore = 0.0;

    @Column(name = "block_probability", nullable = false)
    private double blockProbability = 0.0;

    @Column(name = "assist_probability", nullable = false)
    private double assistProbability = 0.0;
}






