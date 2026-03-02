package com.sanguiwara.mapper;

import com.sanguiwara.entity.BadgeEntity;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.entity.PlayerProgressionEntity;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.PlayerProgressionDelta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {com.sanguiwara.entity.PlayerProgressionId.class})
public interface PlayerProgressionMapper {

    @Mapping(target = "playerId", source = "player.id")
    @Mapping(target = "eventType", source = "id.eventType")
    @Mapping(target = "eventId", source = "id.eventId")
    @Mapping(target = "badgeIds", expression = "java(mapBadgeIds(entity.getPlayer()))")
    @Mapping(target = "delta", expression = "java(toDelta(entity))")
    PlayerProgression toDomain(PlayerProgressionEntity entity);

    @Mapping(target = "id", expression = "java(new PlayerProgressionId(progression.playerId(), progression.eventType(), progression.eventId()))")
    @Mapping(target = "player", expression = "java(playerRef(progression.playerId()))")
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
    PlayerProgressionEntity toEntity(PlayerProgression progression);

    default Set<Long> mapBadgeIds(PlayerEntity player) {
        if (player == null || player.getBadges() == null || player.getBadges().isEmpty()) return Set.of();
        Set<Long> ids = new HashSet<>();
        for (BadgeEntity b : player.getBadges()) {
            if (b != null && b.getId() != null) ids.add(b.getId());
        }
        return ids.isEmpty() ? Set.of() : Set.copyOf(ids);
    }

    default PlayerProgressionDelta toDelta(PlayerProgressionEntity entity) {
        if (entity == null) return null;
        return new PlayerProgressionDelta(
                entity.getTir3Pts(),
                entity.getTir2Pts(),
                entity.getLancerFranc(),
                entity.getFloater(),
                entity.getFinitionAuCercle(),
                entity.getSpeed(),
                entity.getBallhandling(),
                entity.getSize(),
                entity.getWeight(),
                entity.getAgressivite(),
                entity.getDefExterieur(),
                entity.getDefPoste(),
                entity.getProtectionCercle(),
                entity.getTimingRebond(),
                entity.getAgressiviteRebond(),
                entity.getSteal(),
                entity.getTimingBlock(),
                entity.getPhysique(),
                entity.getBasketballIqOff(),
                entity.getBasketballIqDef(),
                entity.getPassingSkills(),
                entity.getIq(),
                entity.getEndurance(),
                entity.getSolidite(),
                entity.getPotentielSkill(),
                entity.getPotentielPhysique(),
                entity.getCoachability(),
                entity.getEgo(),
                entity.getSoftSkills(),
                entity.getLeadership(),
                entity.getMorale()
        );
    }

    default PlayerEntity playerRef(UUID id) {
        if (id == null) return null;
        PlayerEntity p = new PlayerEntity();
        p.setId(id);
        return p;
    }
}
