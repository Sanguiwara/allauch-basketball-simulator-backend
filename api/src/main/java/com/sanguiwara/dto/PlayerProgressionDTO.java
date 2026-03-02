package com.sanguiwara.dto;

import com.sanguiwara.progression.ProgressionEventType;

import java.util.List;
import java.util.UUID;

public record PlayerProgressionDTO(
        UUID playerId,
        ProgressionEventType eventType,
        UUID eventId,
        List<BadgeDTO> badges,
        Integer tir3Pts,
        Integer tir2Pts,
        Integer lancerFranc,
        Integer floater,
        Integer finitionAuCercle,
        Integer speed,
        Integer ballhandling,
        Integer size,
        Integer weight,
        Integer agressivite,
        Integer defExterieur,
        Integer defPoste,
        Integer protectionCercle,
        Integer timingRebond,
        Integer agressiviteRebond,
        Integer steal,
        Integer timingBlock,
        Integer physique,
        Integer basketballIqOff,
        Integer basketballIqDef,
        Integer passingSkills,
        Integer iq,
        Integer endurance,
        Integer solidite,
        Integer potentielSkill,
        Integer potentielPhysique,
        Integer coachability,
        Integer ego,
        Integer softSkills,
        Integer leadership,
        Integer morale
) {
}
