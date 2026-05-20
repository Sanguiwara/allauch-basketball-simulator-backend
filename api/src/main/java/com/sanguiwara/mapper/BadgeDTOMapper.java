package com.sanguiwara.mapper;

import com.sanguiwara.badges.Badge;
import com.sanguiwara.badges.BadgeCatalog;
import com.sanguiwara.dto.BadgeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BadgeDTOMapper {

    @Mapping(target = "id", expression = "java(badge.id())")
    @Mapping(target = "name", expression = "java(badge.name())")
    @Mapping(target = "dropRate", expression = "java(badge.dropRate())")
    @Mapping(target = "types", expression = "java(badge.types())")
    BadgeDTO toDto(Badge badge);

    default List<BadgeDTO> toDtoList(Set<Long> badgeIds) {
        if (badgeIds == null || badgeIds.isEmpty()) return List.of();

        var badgeMap = BadgeCatalog.badgeMap();
        return badgeIds.stream()
                .map(badgeMap::get)
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }

    default Set<Long> toIds(List<BadgeDTO> badges) {
        if (badges == null || badges.isEmpty()) return new HashSet<>();

        Set<Long> ids = new HashSet<>();
        for (BadgeDTO badge : badges) {
            if (badge != null) ids.add(badge.id());
        }
        return ids;
    }
}
