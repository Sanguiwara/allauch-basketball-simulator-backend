package com.sanguiwara.mapper;

import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.dto.BadgeDTO;
import com.sanguiwara.dto.PlayerProgressionDTO;
import com.sanguiwara.progression.PlayerProgression;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PlayerProgressionDTOMapper {

    @Mapping(target = "badges", expression = "java(mapBadges(progression.badgeIds()))")
    @Mapping(target = "tir3Pts", source = "delta.tir3Pts")
    @Mapping(target = "tir2Pts", source = "delta.tir2Pts")
    @Mapping(target = "lancerFranc", source = "delta.lancerFranc")
    @Mapping(target = "floater", source = "delta.floater")
    @Mapping(target = "finitionAuCercle", source = "delta.finitionAuCercle")
    @Mapping(target = "speed", source = "delta.speed")
    @Mapping(target = "ballhandling", source = "delta.ballhandling")
    @Mapping(target = "size", source = "delta.size")
    @Mapping(target = "weight", source = "delta.weight")
    @Mapping(target = "agressivite", source = "delta.agressivite")
    @Mapping(target = "defExterieur", source = "delta.defExterieur")
    @Mapping(target = "defPoste", source = "delta.defPoste")
    @Mapping(target = "protectionCercle", source = "delta.protectionCercle")
    @Mapping(target = "timingRebond", source = "delta.timingRebond")
    @Mapping(target = "agressiviteRebond", source = "delta.agressiviteRebond")
    @Mapping(target = "steal", source = "delta.steal")
    @Mapping(target = "timingBlock", source = "delta.timingBlock")
    @Mapping(target = "physique", source = "delta.physique")
    @Mapping(target = "basketballIqOff", source = "delta.basketballIqOff")
    @Mapping(target = "basketballIqDef", source = "delta.basketballIqDef")
    @Mapping(target = "passingSkills", source = "delta.passingSkills")
    @Mapping(target = "iq", source = "delta.iq")
    @Mapping(target = "endurance", source = "delta.endurance")
    @Mapping(target = "solidite", source = "delta.solidite")
    @Mapping(target = "potentielSkill", source = "delta.potentielSkill")
    @Mapping(target = "potentielPhysique", source = "delta.potentielPhysique")
    @Mapping(target = "coachability", source = "delta.coachability")
    @Mapping(target = "ego", source = "delta.ego")
    @Mapping(target = "softSkills", source = "delta.softSkills")
    @Mapping(target = "leadership", source = "delta.leadership")
    @Mapping(target = "morale", source = "delta.morale")
    PlayerProgressionDTO toDto(PlayerProgression progression);

    default List<BadgeDTO> mapBadges(Set<Long> badgeIds) {
        if (badgeIds == null || badgeIds.isEmpty()) return List.of();
        var badgeMap = BadgeCatalog.badgeMap();
        return badgeIds.stream()
                .sorted(Comparator.naturalOrder())
                .map(badgeMap::get)
                .filter(Objects::nonNull)
                .map(b -> new BadgeDTO(b.id(), b.name(), b.dropRate(), b.types()))
                .toList();
    }
}
