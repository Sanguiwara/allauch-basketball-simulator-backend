package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Team;
import com.sanguiwara.entity.TeamEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {EntityReferenceMapper.class, PlayerMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    @Mapping(target = "club", source = "clubID")
    TeamEntity toEntity(Team team);

    @Mapping(target = "clubID", source = "club")
    Team toDomain(TeamEntity entity);
}
