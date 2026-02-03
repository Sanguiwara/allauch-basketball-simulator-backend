package com.sanguiwara.baserecords;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Player {

    @Setter
    private Set<UUID> teamsID = new HashSet<>();
    @Setter
    private UUID clubID;

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






    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player other)) return false;
        return Objects.equals(id, other.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
