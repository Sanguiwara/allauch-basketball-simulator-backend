package com.sanguiwara.mapper;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.dto.BadgeDTO;
import com.sanguiwara.dto.PlayerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.*;

@Mapper(componentModel = "spring")
public interface PlayerDTOMapper {

    @Mapping(target = "clubId", source = "clubID")
    @Mapping(target = "teamIds", source = "teamsID")
    @Mapping(target = "badges", expression = "java(mapBadges(player.getBadgeIds()))")
    PlayerDTO toDto(Player player);

    @Mapping(target = "clubID", source = "clubId")
    @Mapping(target = "teamsID", source = "teamIds")
    @Mapping(target = "badgeIds", expression = "java(mapBadgeIds(playerDTO.badges()))")
    Player toDomain(PlayerDTO playerDTO);

    default List<BadgeDTO> mapBadges(Set<Long> badgeIds) {
        if (badgeIds == null || badgeIds.isEmpty()) return List.of();
        var badgeMap = BadgeCatalog.badgeMap();

        return badgeIds.stream()
                .map(badgeMap::get)
                .map(this::toDto)
                .toList();
    }

    default Set<Long> mapBadgeIds(List<BadgeDTO> badges) {
        if (badges == null || badges.isEmpty()) return new HashSet<>();
        Set<Long> ids = new HashSet<>();
        for (BadgeDTO b : badges) {
            if (b != null) ids.add(b.id());
        }
        return ids;
    }

    default BadgeDTO toDto(Badge badge) {
        return new BadgeDTO(badge.id(), badge.name(), badge.dropRate(), badge.types());
    }
}
