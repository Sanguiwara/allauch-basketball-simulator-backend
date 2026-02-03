package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Team;
import com.sanguiwara.entity.ClubEntity;
import com.sanguiwara.entity.TeamEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PlayerMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMapper {
    @Mapping(target = "club", ignore = true)
    TeamEntity toEntity(Team team);

    @Mapping(target = "clubID", source = "club.id")
    Team toDomain(TeamEntity entity);

    @AfterMapping
    default void linkClub(Team team, @MappingTarget TeamEntity entity) {
        if (team.getClubID() != null) {
            ClubEntity c = new ClubEntity();
            c.setId(team.getClubID());
            entity.setClub(c);
        } else {
            entity.setClub(null);
        }
    }

}
