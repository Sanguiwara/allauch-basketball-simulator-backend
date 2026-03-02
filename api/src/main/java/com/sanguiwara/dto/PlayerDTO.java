package com.sanguiwara.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record PlayerDTO(
        UUID id,
        String name,
        int birthDate,
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
        int defExterieur,
        int defPoste,
        int protectionCercle,
        int timingRebond,
        int agressiviteRebond,
        int steal,
        int timingBlock,
        int physique,
        int basketballIqOff,
        int basketballIqDef,
        int passingSkills,
        int iq,
        int endurance,
        int solidite,
        int potentielSkill,
        int potentielPhysique,
        int coachability,
        int ego,
        int softSkills,
        int leadership,
        List<BadgeDTO> badges,
        UUID clubId,
        Set<UUID> teamIds
) {
}

