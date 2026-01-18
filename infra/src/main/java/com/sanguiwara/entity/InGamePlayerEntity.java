package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "in_game_players")
public class InGamePlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hibernate 6 / Spring Boot 3
    private UUID id;

    // InGamePlayerEntity
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gameplan_id", nullable = false)
    private GamePlanEntity gamePlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerEntity player;

    // Inputs calculés pour le match
    @Column(name = "playmaking_contrib", nullable = false)
    private double playmakingContribution = 0.0;

    @Column(name = "assist_weight", nullable = false)
    private double assistWeight = 0.0;

    // Usage
    @Column(name = "usage_shoot", nullable = false)
    private int usageShoot;

    @Column(name = "usage_drive", nullable = false)
    private int usageDrive;

    @Column(name = "usage_post", nullable = false)
    private int usagePost;

    // Boxscore
    @Column(name = "assists", nullable = false)
    private int assists;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "fga", nullable = false)
    private int fga;

    @Column(name = "fgm", nullable = false)
    private int fgm;

    @Column(name = "tpa", nullable = false)
    private int tpa;

    @Column(name = "tpm", nullable = false)
    private int tpm;

    @Column(name = "two_pa", nullable = false)
    private int twoPa;

    @Column(name = "two_pm", nullable = false)
    private int twoPm;

    @Column(name = "is_starter", nullable = false)
    private boolean starter;
}
