package com.sanguiwara.entity;

import com.sanguiwara.factory.PlayerArchetype;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "player_season_snapshots")
public class PlayerSeasonSnapshotEntity {

    @EmbeddedId
    private PlayerSeasonSnapshotId id;

    @MapsId("leagueSeasonId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_season_id", nullable = false)
    private LeagueSeasonEntity leagueSeason;

    @MapsId("playerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerEntity player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private ClubEntity club;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "archetype", nullable = false)
    private PlayerArchetype archetype;

    @Column(name = "birth_date", nullable = false)
    private int birthDate;

    @Column(name = "injured", nullable = false)
    private boolean injured;

    @Column(name = "tir_3_pts", nullable = false)
    private int tir3Pts;

    @Column(name = "tir_2_pts", nullable = false)
    private int tir2Pts;

    @Column(name = "lancer_franc", nullable = false)
    private int lancerFranc;

    @Column(name = "floater", nullable = false)
    private int floater;

    @Column(name = "finition_au_cercle", nullable = false)
    private int finitionAuCercle;

    @Column(name = "speed", nullable = false)
    private int speed;

    @Column(name = "ballhandling", nullable = false)
    private int ballhandling;

    @Column(name = "size", nullable = false)
    private int size;

    @Column(name = "weight", nullable = false)
    private int weight;

    @Column(name = "agressivite", nullable = false)
    private int agressivite;

    @Column(name = "def_exterieur", nullable = false)
    private int defExterieur;

    @Column(name = "def_poste", nullable = false)
    private int defPoste;

    @Column(name = "protection_cercle", nullable = false)
    private int protectionCercle;

    @Column(name = "timing_rebond", nullable = false)
    private int timingRebond;

    @Column(name = "agressivite_rebond", nullable = false)
    private int agressiviteRebond;

    @Column(name = "steal", nullable = false)
    private int steal;

    @Column(name = "timing_block", nullable = false)
    private int timingBlock;

    @Column(name = "physique", nullable = false)
    private int physique;

    @Column(name = "basketball_iq_off", nullable = false)
    private int basketballIqOff;

    @Column(name = "basketball_iq_def", nullable = false)
    private int basketballIqDef;

    @Column(name = "passing_skills", nullable = false)
    private int passingSkills;

    @Column(name = "iq", nullable = false)
    private int iq;

    @Column(name = "endurance", nullable = false)
    private int endurance;

    @Column(name = "solidite", nullable = false)
    private int solidite;

    @Column(name = "potentiel_skill", nullable = false)
    private int potentielSkill;

    @Column(name = "potentiel_physique", nullable = false)
    private int potentielPhysique;

    @Column(name = "coachability", nullable = false)
    private int coachability;

    @Column(name = "ego", nullable = false)
    private int ego;

    @Column(name = "soft_skills", nullable = false)
    private int softSkills;

    @Column(name = "leadership", nullable = false)
    private int leadership;

    @Column(name = "morale", nullable = false)
    private int morale;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "player_season_snapshot_badges",
            joinColumns = {
                    @JoinColumn(name = "league_season_id", referencedColumnName = "league_season_id"),
                    @JoinColumn(name = "player_id", referencedColumnName = "player_id")
            }
    )
    @Column(name = "badge_id", nullable = false)
    private Set<Long> badgeIds = new HashSet<>();
}
