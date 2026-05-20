package com.sanguiwara.mapper;

import com.sanguiwara.badges.BadgeCatalog;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BadgeDTOMapperTest {

    private final BadgeDTOMapper mapper = new BadgeDTOMapperImpl();

    @Test
    void toDto_mapsBadgeFields() {
        var badge = BadgeCatalog.badgeMap().values().iterator().next();

        var dto = mapper.toDto(badge);

        assertThat(dto.id()).isEqualTo(badge.id());
        assertThat(dto.name()).isEqualTo(badge.name());
        assertThat(dto.dropRate()).isEqualTo(badge.dropRate());
        assertThat(dto.types()).isEqualTo(badge.types());
    }

    @Test
    void toDtoList_resolvesBadgeIds() {
        long badgeId = BadgeCatalog.badgeMap().keySet().iterator().next();

        var dtos = mapper.toDtoList(Set.of(badgeId));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.getFirst().id()).isEqualTo(badgeId);
    }
}
