package com.sanguiwara.mapper;

import com.sanguiwara.entity.PlayerProgressionEntity;
import com.sanguiwara.progression.PlayerProgressionDelta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PlayerTemporaryModifierEntityMapper.class)
public interface PlayerProgressionDeltaMapper {

    @Mapping(target = "badgesAdded", source = "badgeIds")
    @Mapping(target = "badgesRemoved", expression = "java(java.util.Set.of())")
    @Mapping(target = "temporaryModifiersAdded", source = "temporaryModifiers")
    @Mapping(target = "temporaryModifiersRemoved", expression = "java(java.util.Set.of())")
    PlayerProgressionDelta toDomain(PlayerProgressionEntity entity);
}
