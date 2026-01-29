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
@Table(name = "players")
public class PlayerEntity {

    @Id
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // Format: YYYYMMDD (ex: 19981225)
    @Column(name = "birth_date", nullable = false)
    private int birthDate;

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
}
