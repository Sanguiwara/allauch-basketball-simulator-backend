package com.sanguiwara.mapper;

import com.sanguiwara.entity.BadgeEntity;
import org.mapstruct.Mapper;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BadgeEntityMapper {

    default Long toId(BadgeEntity badge) {
        return badge == null ? null : badge.getId();
    }

    default BadgeEntity toEntity(Long id) {
        if (id == null) return null;
        BadgeEntity badge = new BadgeEntity();
        badge.setId(id);
        return badge;
    }

    default Set<Long> toIds(Set<BadgeEntity> badges) {
        if (badges == null || badges.isEmpty()) return new HashSet<>();

        Set<Long> ids = new HashSet<>();
        for (BadgeEntity badge : badges) {
            if (badge != null && badge.getId() != null) ids.add(badge.getId());
        }
        return ids;
    }

    default Set<BadgeEntity> toEntities(Set<Long> badgeIds) {
        if (badgeIds == null || badgeIds.isEmpty()) return new HashSet<>();

        Set<BadgeEntity> badges = new HashSet<>();
        for (Long id : badgeIds) {
            BadgeEntity badge = toEntity(id);
            if (badge != null) badges.add(badge);
        }
        return badges;
    }
}
