package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Team;
import com.sanguiwara.dto.TeamDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PlayerDTOMapper.class})
public interface TeamDTOMapper {

    @Mapping(target = "clubId", source = "clubID")
    TeamDTO toDto(Team team);

    @Mapping(target = "clubID", source = "clubId")
    Team toDomain(TeamDTO teamDTO);
}

