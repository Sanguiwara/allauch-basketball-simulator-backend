package com.sanguiwara.mapper;

import com.sanguiwara.entity.BadgeEntity;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.entity.PlayerProgressionEntity;
import com.sanguiwara.entity.PlayerProgressionId;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.ProgressionEventType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerProgressionMapperTest {

    private final PlayerProgressionMapper mapper = Mappers.getMapper(PlayerProgressionMapper.class);

    @Test
    void toDomain_usesProgressionBadgeSnapshot_notPlayerCurrentBadges() {
        UUID playerId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        var player = new PlayerEntity();
        player.setId(playerId);
        var currentBadge = new BadgeEntity();
        currentBadge.setId(999L);
        player.getBadges().add(currentBadge);

        var entity = new PlayerProgressionEntity();
        entity.setId(new PlayerProgressionId(playerId, ProgressionEventType.GAME, eventId));
        entity.setPlayer(player);
        entity.setBadgeIds(Set.of(1L, 2L));

        PlayerProgression domain = mapper.toDomain(entity);

        assertThat(domain.playerId()).isEqualTo(playerId);
        assertThat(domain.eventType()).isEqualTo(ProgressionEventType.GAME);
        assertThat(domain.eventId()).isEqualTo(eventId);
        assertThat(domain.badgeIds()).containsExactlyInAnyOrder(1L, 2L);
        assertThat(domain.badgeIds()).doesNotContain(999L);
    }

    @Test
    void toEntity_persistsBadgeSnapshot_fromDomain() {
        UUID playerId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        var progression = new PlayerProgression(playerId, ProgressionEventType.TRAINING, eventId, Set.of(10L, 11L), null);

        PlayerProgressionEntity entity = mapper.toEntity(progression);

        assertThat(entity.getId().getPlayerId()).isEqualTo(playerId);
        assertThat(entity.getId().getEventType()).isEqualTo(ProgressionEventType.TRAINING);
        assertThat(entity.getId().getEventId()).isEqualTo(eventId);
        assertThat(entity.getBadgeIds()).containsExactlyInAnyOrder(10L, 11L);
    }
}

