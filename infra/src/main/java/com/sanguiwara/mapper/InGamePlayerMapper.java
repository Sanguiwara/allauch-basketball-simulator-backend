package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.entity.InGamePlayerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InGamePlayerMapper {

    @Mapping(target = "gamePlan", ignore = true)
    InGamePlayerEntity toEntity(InGamePlayer inGamePlayer);

    InGamePlayer toDomain(InGamePlayerEntity entity);
}
