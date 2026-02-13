package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.dto.PlayerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlayerDTOMapper {

    @Mapping(target = "clubId", source = "clubID")
    @Mapping(target = "teamIds", source = "teamsID")
    PlayerDTO toDto(Player player);

    @Mapping(target = "clubID", source = "clubId")
    @Mapping(target = "teamsID", source = "teamIds")
    Player toDomain(PlayerDTO playerDTO);
}

