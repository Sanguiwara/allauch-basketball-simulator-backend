package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "in_game_players")
public class InGamePlayerEntity {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
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

    @Column(name = "rebound_contrib", nullable = false)
    private double reboundContribution = 0.0;

    @Column(name = "rebound_weight", nullable = false)
    private double reboundWeight = 0.0;

    @Column(name = "three_pt_contrib", nullable = false)
    private double threePointContribution = 0.0;

    @Column(name = "three_pt_weight", nullable = false)
    private double threePointWeight = 0.0;

    @Column(name = "two_pt_contrib", nullable = false)
    private double twoPointContribution = 0.0;

    @Column(name = "two_pt_weight", nullable = false)
    private double twoPointWeight = 0.0;

    @Column(name = "drive_contrib", nullable = false)
    private double driveContribution = 0.0;

    @Column(name = "drive_weight", nullable = false)
    private double driveWeight = 0.0;

    @Column(name = "block_contrib", nullable = false)
    private double blockContribution = 0.0;

    @Column(name = "block_weight", nullable = false)
    private double blockWeight = 0.0;

    @Column(name = "steal_contrib", nullable = false)
    private double stealContribution = 0.0;

    @Column(name = "steal_weight", nullable = false)
    private double stealWeight = 0.0;

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

    @Column(name = "off_reb", nullable = false)
    private int offensiveRebounds;

    @Column(name = "def_reb", nullable = false)
    private int defensiveRebounds;

    @Column(name = "steals", nullable = false)
    private int steals;

    @Column(name = "blocks", nullable = false)
    private int blocks;

    @Column(name = "fga", nullable = false)
    private int fga;

    @Column(name = "fgm", nullable = false)
    private int fgm;

    @Column(name = "tpa", nullable = false)
    private int threePointAttempt;

    @Column(name = "tpm", nullable = false)
    private int threePointMade;

    @Column(name = "two_pa", nullable = false)
    private int twoPointAttempts;

    @Column(name = "two_pm", nullable = false)
    private int twoPointMade;

    @Column(name = "is_starter", nullable = false)
    private boolean starter;

    @Column(name = "drive_pa", nullable = false)
    private int driveAttempts;

    @Column(name = "drive_pm", nullable = false)
    private int driveMade;

    @Column(name = "minutes_played", nullable = false)
    private int minutesPlayed = 20;

    @Column(name = "match_rating", nullable = false)
    private double matchRating = 0.0;

    @Column(name = "three_pt_score", nullable = false)
    private double threePtScore = 0.0;

    @Column(name = "three_pt_defense_score", nullable = false)
    private double threePtDefenseScore = 0.0;

    @Column(name = "two_pt_score", nullable = false)
    private double twoPtScore = 0.0;

    @Column(name = "two_pt_defense_score", nullable = false)
    private double twoPtDefenseScore = 0.0;

    @Column(name = "drive_score", nullable = false)
    private double driveScore = 0.0;

    @Column(name = "drive_defense_score", nullable = false)
    private double driveDefenseScore = 0.0;

    @Column(name = "man_to_man_playmaking_off_score", nullable = false)
    private double manToManPlaymakingOffScore = 0.0;

    @Column(name = "man_to_man_playmaking_def_score", nullable = false)
    private double manToManPlaymakingDefScore = 0.0;

    @Column(name = "zone_playmaking_off_score", nullable = false)
    private double zonePlaymakingOffScore = 0.0;

    @Column(name = "zone_playmaking_def_score", nullable = false)
    private double zonePlaymakingDefScore = 0.0;

    @Column(name = "zone23_defense_score", nullable = false)
    private double zone23DefenseScore = 0.0;

    @Column(name = "zone32_defense_score", nullable = false)
    private double zone32DefenseScore = 0.0;

    @Column(name = "zone212_defense_score", nullable = false)
    private double zone212DefenseScore = 0.0;

    @Column(name = "rebound_score", nullable = false)
    private double reboundScore = 0.0;

    @Column(name = "steal_score", nullable = false)
    private double stealScore = 0.0;
}
