package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Team;
import com.sanguiwara.entity.TeamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    TeamEntity toEntity(Team team);
    Team toDomain(TeamEntity entity);
}
