package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<TeamEntity> teams = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private ClubEntity club;

    // Format: YYYYMMDD (ex: 19981225)
    @Column(name = "birth_date", nullable = false)
    private int birthDate;

    @Column(name = "injured", nullable = false)
    private boolean injured;

    // Tirs / finition
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

    // Défense / rebond
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

    // Physique / mental / skills
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

    // Potentiel
    @Column(name = "potentiel_skill", nullable = false)
    private int potentielSkill;

    @Column(name = "potentiel_physique", nullable = false)
    private int potentielPhysique;

    // Attitude / comportement
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerEntity other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
