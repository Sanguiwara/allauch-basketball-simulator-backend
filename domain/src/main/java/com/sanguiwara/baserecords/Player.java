package com.sanguiwara.baserecords;


import java.util.Objects;
import java.util.UUID;

public record Player(
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


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player other)) return false;
        return Objects.equals(id, other.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }

}
