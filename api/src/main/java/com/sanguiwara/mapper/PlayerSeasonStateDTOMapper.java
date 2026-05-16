package com.sanguiwara.mapper;

import com.sanguiwara.dto.PlayerSeasonStateDTO;
import com.sanguiwara.progression.PlayerSeasonState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PlayerDTOMapper.class, PlayerDeltaDTOMapper.class})
public interface PlayerSeasonStateDTOMapper {

    @Mapping(target = "playerId", source = "current.id")
    PlayerSeasonStateDTO toDto(PlayerSeasonState state);
}
