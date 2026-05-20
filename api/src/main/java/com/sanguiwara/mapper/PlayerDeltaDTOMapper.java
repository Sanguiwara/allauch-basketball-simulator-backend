package com.sanguiwara.mapper;

import com.sanguiwara.dto.PlayerDeltaDTO;
import com.sanguiwara.progression.PlayerProgressionDelta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BadgeDTOMapper.class, TemporaryModifierDTOMapper.class})
public interface PlayerDeltaDTOMapper {

    @Mapping(target = "badgesAdded", source = "badgesAdded")
    @Mapping(target = "badgesRemoved", source = "badgesRemoved")
    @Mapping(target = "temporaryModifiersAdded", source = "temporaryModifiersAdded")
    @Mapping(target = "temporaryModifiersRemoved", source = "temporaryModifiersRemoved")
    PlayerDeltaDTO toDto(PlayerProgressionDelta delta);
}
