package com.sanguiwara.baserecords;


import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Player {

    // Accessors with record-style names to preserve API
    private final UUID id;
    private final String name;
    private final int birthDate;

    // Tirs / finition
    private final int tir3Pts;
    private final int tir2Pts;
    private final int lancerFranc;
    private final int floater;
    private final int finitionAuCercle;
    private final int speed;
    private final int ballhandling;
    private final int size;
    private final int weight;
    private final int agressivite;

    // Défense / rebond
    private final int defExterieur;
    private final int defPoste;
    private final int protectionCercle;
    private final int timingRebond;
    private final int agressiviteRebond;
    private final int steal;
    private final int timingBlock;

    // Physique / mental / skills
    private final int physique;
    private final int basketballIqOff;
    private final int basketballIqDef;
    private final int passingSkills;
    private final int iq;
    private final int endurance;

    private final int solidite;

    // Potentiel
    private final int potentielSkill;
    private final int potentielPhysique;

    // Attitude / comportement
    private final int coachability;
    private final int ego;
    private final int softSkills;
    private final int leadership;

    public Player(
            UUID id,
            String name,
            int birthDate,

            // Tirs / finition
            int tir3Pts,
            int tir2Pts,
            int lancerFranc,
            int floater,
            int finitionAuCercle,
            int speed,
            int ballhandling,
            int size,
            int weight,
            int agressivite,

            // Défense / rebond
            int defExterieur,
            int defPoste,
            int protectionCercle,
            int timingRebond,
            int agressiviteRebond,
            int steal,
            int timingBlock,

            // Physique / mental / skills
            int physique,
            int basketballIqOff,
            int basketballIqDef,
            int passingSkills,
            int iq,
            int endurance,

            int solidite,

            // Potentiel
            int potentielSkill,
            int potentielPhysique,

            // Attitude / comportement
            int coachability,
            int ego,
            int softSkills,
            int leadership
    ) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.tir3Pts = tir3Pts;
        this.tir2Pts = tir2Pts;
        this.lancerFranc = lancerFranc;
        this.floater = floater;
        this.finitionAuCercle = finitionAuCercle;
        this.speed = speed;
        this.ballhandling = ballhandling;
        this.size = size;
        this.weight = weight;
        this.agressivite = agressivite;
        this.defExterieur = defExterieur;
        this.defPoste = defPoste;
        this.protectionCercle = protectionCercle;
        this.timingRebond = timingRebond;
        this.agressiviteRebond = agressiviteRebond;
        this.steal = steal;
        this.timingBlock = timingBlock;
        this.physique = physique;
        this.basketballIqOff = basketballIqOff;
        this.basketballIqDef = basketballIqDef;
        this.passingSkills = passingSkills;
        this.iq = iq;
        this.endurance = endurance;
        this.solidite = solidite;
        this.potentielSkill = potentielSkill;
        this.potentielPhysique = potentielPhysique;
        this.coachability = coachability;
        this.ego = ego;
        this.softSkills = softSkills;
        this.leadership = leadership;
    }




    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player other)) return false;
        return Objects.equals(id, other.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
