package com.sanguiwara.baserecords;


import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class Player {

    @Setter
    private Set<UUID> teamsID;
    @Setter
    private UUID clubID;

    private final UUID id;
    private final String name;
    private final int birthDate;

    @Setter
    private boolean injured;

    // Tirs / finition
    @Setter
    private  int tir3Pts;
    @Setter
    private  int tir2Pts;
    private  int lancerFranc;
    @Setter
    private  int floater;
    @Setter
    private  int finitionAuCercle;
    private  int speed;
    private  int ballhandling;
    private  int size;
    private  int weight;
    private  int agressivite;

    // Défense / rebond
    private  int defExterieur;
    private  int defPoste;
    @Setter
    private  int protectionCercle;
    @Setter
    private  int timingRebond;
    @Setter
    private  int agressiviteRebond;
    @Setter
    private  int steal;
    @Setter
    private  int timingBlock;

    // Physique / mental / skills
    private  int physique;
    private  int basketballIqOff;
    private  int basketballIqDef;
    private  int passingSkills;
    private  int iq;
    private  int endurance;

    private  int solidite;

    // Potentiel
    @Setter
    private  int potentielSkill;
    private  int potentielPhysique;

    // Attitude / comportement
    private  int coachability;
    private  int ego;
    private  int softSkills;
    private  int leadership;
    @Setter
    private  int morale;


    /**
     * Deep snapshot of this Player at the current instant (copies mutable collections).
     * Used to compute progression deltas after post-game mutations.
     */
    public Player snapshotPlayer() {
        Set<UUID> teamsCopy = (teamsID == null) ? new HashSet<>() : new HashSet<>(teamsID);

        return Player.builder()
                .teamsID(teamsCopy)
                .clubID(clubID)
                .id(id)
                .name(name)
                .birthDate(birthDate)
                .injured(injured)
                .tir3Pts(tir3Pts)
                .tir2Pts(tir2Pts)
                .lancerFranc(lancerFranc)
                .floater(floater)
                .finitionAuCercle(finitionAuCercle)
                .speed(speed)
                .ballhandling(ballhandling)
                .size(size)
                .weight(weight)
                .agressivite(agressivite)
                .defExterieur(defExterieur)
                .defPoste(defPoste)
                .protectionCercle(protectionCercle)
                .timingRebond(timingRebond)
                .agressiviteRebond(agressiviteRebond)
                .steal(steal)
                .timingBlock(timingBlock)
                .physique(physique)
                .basketballIqOff(basketballIqOff)
                .basketballIqDef(basketballIqDef)
                .passingSkills(passingSkills)
                .iq(iq)
                .endurance(endurance)
                .solidite(solidite)
                .potentielSkill(potentielSkill)
                .potentielPhysique(potentielPhysique)
                .coachability(coachability)
                .ego(ego)
                .softSkills(softSkills)
                .leadership(leadership)
                .morale(morale)
                .build();
    }



    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player other)) return false;
        return Objects.equals(id, other.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
