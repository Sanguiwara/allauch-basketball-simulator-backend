package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Team;
import com.sanguiwara.entity.ClubEntity;
import com.sanguiwara.entity.GamePlanEntity;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.entity.TeamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface EntityReferenceMapper {

    default UUID toId(ClubEntity entity) {
        return entity == null ? null : entity.getId();
    }

    default UUID toId(GamePlanEntity entity) {
        return entity == null ? null : entity.getId();
    }

    default UUID toId(PlayerEntity entity) {
        return entity == null ? null : entity.getId();
    }

    default UUID toId(TeamEntity entity) {
        return entity == null ? null : entity.getId();
    }

    default ClubEntity toClubEntity(UUID id) {
        if (id == null) return null;
        ClubEntity entity = new ClubEntity();
        entity.setId(id);
        return entity;
    }

    default GamePlanEntity toGamePlanEntity(UUID id) {
        if (id == null) return null;
        GamePlanEntity entity = new GamePlanEntity();
        entity.setId(id);
        return entity;
    }

    default PlayerEntity toPlayerEntity(UUID id) {
        if (id == null) return null;
        PlayerEntity entity = new PlayerEntity();
        entity.setId(id);
        return entity;
    }

    default TeamEntity toTeamEntity(UUID id) {
        if (id == null) return null;
        TeamEntity entity = new TeamEntity();
        entity.setId(id);
        return entity;
    }

    @Named("teamRefFromDomain")
    default TeamEntity toTeamEntity(Team team) {
        return team == null ? null : toTeamEntity(team.getId());
    }

    default Set<UUID> toTeamIds(Set<TeamEntity> teams) {
        if (teams == null || teams.isEmpty()) return new HashSet<>();

        Set<UUID> ids = new HashSet<>();
        for (TeamEntity team : teams) {
            UUID id = toId(team);
            if (id != null) ids.add(id);
        }
        return ids;
    }

    default Set<TeamEntity> toTeamEntities(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();

        Set<TeamEntity> teams = new HashSet<>();
        for (UUID id : ids) {
            TeamEntity team = toTeamEntity(id);
            if (team != null) teams.add(team);
        }
        return teams;
    }
}
