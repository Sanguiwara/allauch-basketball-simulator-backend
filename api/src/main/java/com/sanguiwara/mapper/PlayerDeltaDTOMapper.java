package com.sanguiwara.mapper;

import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.dto.BadgeDTO;
import com.sanguiwara.dto.PlayerDeltaDTO;
import com.sanguiwara.progression.PlayerProgressionDelta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PlayerDeltaDTOMapper {

    @Mapping(target = "badgesAdded", expression = "java(mapBadges(delta.badgesAdded()))")
    @Mapping(target = "badgesRemoved", expression = "java(mapBadges(delta.badgesRemoved()))")
    PlayerDeltaDTO toDto(PlayerProgressionDelta delta);

    default List<BadgeDTO> mapBadges(Set<Long> badgeIds) {
        if (badgeIds == null || badgeIds.isEmpty()) return List.of();
        var badgeMap = BadgeCatalog.badgeMap();
        return badgeIds.stream()
                .sorted(Comparator.naturalOrder())
                .map(badgeMap::get)
                .filter(Objects::nonNull)
                .map(b -> new BadgeDTO(b.id(), b.name(), b.dropRate(), b.types()))
                .toList();
    }
}
