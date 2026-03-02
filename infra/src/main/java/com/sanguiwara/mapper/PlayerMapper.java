package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.entity.BadgeEntity;
import com.sanguiwara.entity.ClubEntity;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.entity.TeamEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface PlayerMapper {


    @Mapping(target = "teamsID", expression = "java(mapTeamIds(entity.getTeams()))")
    @Mapping(target = "clubID", source = "club.id")
    @Mapping(target = "badgeIds", expression = "java(mapBadgeIds(entity.getBadges()))")
    Player toDomain(PlayerEntity entity);


    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "club", ignore = true)
    @Mapping(target = "badges", ignore = true)
    PlayerEntity toEntity(Player player);


    @AfterMapping
    default void linkRefs(Player player, @MappingTarget PlayerEntity entity) {

        // teams: on reconstruit un Set<TeamEntity> stubs à partir des UUID
        if (player.getTeamsID() != null && !player.getTeamsID().isEmpty()) {
            Set<TeamEntity> teams = new HashSet<>();
            for (UUID id : player.getTeamsID()) {
                var teamEntity = new TeamEntity();
                teamEntity.setId(id);
                teams.add(teamEntity);
            }
            entity.setTeams(teams);
        } else {
            entity.setTeams(new HashSet<>());
        }

        // club
        if (player.getClubID() != null) {
            ClubEntity c = new ClubEntity();
            c.setId(player.getClubID());
            entity.setClub(c);
        } else {
            entity.setClub(null);
        }
    }

    // ===== helper =====
    default Set<UUID> mapTeamIds(Set<TeamEntity> teams) {
        if (teams == null || teams.isEmpty()) return new HashSet<>();
        Set<UUID> ids = new HashSet<>();
        for (TeamEntity t : teams) {
            if (t != null && t.getId() != null) ids.add(t.getId());
        }
        return ids;
    }

    default Set<Long> mapBadgeIds(Set<BadgeEntity> badges) {
        if (badges == null || badges.isEmpty()) return new HashSet<>();
        Set<Long> ids = new HashSet<>();
        for (BadgeEntity b : badges) {
            if (b != null && b.getId() != null) ids.add(b.getId());
        }
        return ids;
    }


}
