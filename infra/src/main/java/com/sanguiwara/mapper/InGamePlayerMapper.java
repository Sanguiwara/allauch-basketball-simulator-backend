package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.entity.InGamePlayerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {EntityReferenceMapper.class, PlayerMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InGamePlayerMapper {

    @Mapping(target = "gamePlan", source = "gamePlanId")
    InGamePlayerEntity toEntity(InGamePlayer inGamePlayer);

    @Mapping(target = "gamePlanId", source = "gamePlan")
    InGamePlayer toDomain(InGamePlayerEntity entity);
}
