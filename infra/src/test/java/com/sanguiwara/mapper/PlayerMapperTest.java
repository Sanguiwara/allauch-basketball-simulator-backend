package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerMapperTest {

    private final PlayerMapper mapper = Mappers.getMapper(PlayerMapper.class);

    @Test
    void toEntity_mapsBadgeIds_toBadgeEntities() {
        UUID playerId = UUID.randomUUID();
        var p = Player.builder()
                .id(playerId)
                .name("p")
                .birthDate(20000101)
                .badgeIds(Set.of(1L, 2L))
                .build();

        var e = mapper.toEntity(p);

        assertThat(e.getBadges()).extracting("id").containsExactlyInAnyOrder(1L, 2L);
    }
}

