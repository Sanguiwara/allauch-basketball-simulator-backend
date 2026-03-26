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
    @Mapping(target = "badges", expression = "java(mapBadges(player.getBadgeIds()))")
    PlayerEntity toEntity(Player player);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "club", ignore = true)
    @Mapping(target = "badges", ignore = true)
    void updateEntity(Player player, @MappingTarget PlayerEntity entity);


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

    default Set<BadgeEntity> mapBadges(Set<Long> badgeIds) {
        if (badgeIds == null || badgeIds.isEmpty()) return new HashSet<>();
        Set<BadgeEntity> badges = new HashSet<>();
        for (Long id : badgeIds) {
            if (id == null) continue;
            BadgeEntity b = new BadgeEntity();
            b.setId(id);
            badges.add(b);
        }
        return badges;
    }

}
