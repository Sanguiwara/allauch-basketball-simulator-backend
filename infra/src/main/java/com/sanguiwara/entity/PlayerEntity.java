package com.sanguiwara.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Tirs / finition
    @Column(name = "tir_3_pts", nullable = false)
    private Short tir3Pts;

    @Column(name = "tir_2_pts", nullable = false)
    private Short tir2Pts;

    @Column(name = "lancer_franc", nullable = false)
    private Short lancerFranc;

    @Column(name = "floater", nullable = false)
    private Short floater;

    @Column(name = "finition_au_cercle", nullable = false)
    private Short finitionAuCercle;

    // Défense / rebond
    @Column(name = "def_exterieur", nullable = false)
    private Short defExterieur;

    @Column(name = "def_poste", nullable = false)
    private Short defPoste;

    @Column(name = "protection_cercle", nullable = false)
    private Short protectionCercle;

    @Column(name = "timing_rebond", nullable = false)
    private Short timingRebond;

    @Column(name = "agressivite_rebond", nullable = false)
    private Short agressiviteRebond;

    // Physique / mental / skills
    @Column(name = "physique", nullable = false)
    private Short physique;

    @Column(name = "basketball_iq_off", nullable = false)
    private Short basketballIqOff;

    @Column(name = "basketball_iq_def", nullable = false)
    private Short basketballIqDef;

    @Column(name = "passing_skills", nullable = false)
    private Short passingSkills;

    @Column(name = "iq", nullable = false)
    private Short iq;

    @Column(name = "endurance", nullable = false)
    private Short endurance;

    @Column(name = "solidite", nullable = false)
    private Short solidite;

    // Potentiel
    @Column(name = "potentiel_skill", nullable = false)
    private Short potentielSkill;

    @Column(name = "potentiel_physique", nullable = false)
    private Short potentielPhysique;

    // Attitude / comportement
    @Column(name = "coachability", nullable = false)
    private Short coachability;

    @Column(name = "ego", nullable = false)
    private Short ego;

    @Column(name = "soft_skills", nullable = false)
    private Short softSkills;

    @Column(name = "leadership", nullable = false)
    private Short leadership;

    @Column(name = "birthYear", nullable = false)
    private String birthYear;

}
