package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.entity.GamePlanEntity;
import com.sanguiwara.entity.InGamePlayerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InGamePlayerMapper {

    @Mapping(target = "gamePlan", expression = "java(gamePlanRef(inGamePlayer.getGamePlanId()))")
    InGamePlayerEntity toEntity(InGamePlayer inGamePlayer);

    @Mapping(target = "gamePlanId", source = "gamePlan.id")
    InGamePlayer toDomain(InGamePlayerEntity entity);

    default GamePlanEntity gamePlanRef(UUID gamePlanId) {
        if (gamePlanId == null) return null;
        GamePlanEntity gp = new GamePlanEntity();
        gp.setId(gamePlanId);
        return gp;
    }
}
