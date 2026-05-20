package com.sanguiwara.mapper;

import com.sanguiwara.badges.ModifierOp;
import com.sanguiwara.badges.ModifierType;
import com.sanguiwara.badges.Target;
import com.sanguiwara.entity.BadgeEntity;
import com.sanguiwara.entity.PlayerEntity;
import com.sanguiwara.entity.PlayerProgressionEntity;
import com.sanguiwara.entity.PlayerProgressionId;
import com.sanguiwara.entity.PlayerTemporaryModifierEmbeddable;
import com.sanguiwara.modifiers.PlayerModifier;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.ProgressionEventType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerProgressionMapperTest {

    private final PlayerProgressionMapper mapper = mapper();

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
    void toDomain_mapsProgressionTemporaryModifiersToProgressionAndDelta() {
        UUID playerId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        var player = new PlayerEntity();
        player.setId(playerId);

        var modifier = new PlayerTemporaryModifierEmbeddable(
                ModifierType.THREE_POINT,
                Target.SHOT_PCT,
                ModifierOp.ADD,
                0.05,
                1
        );

        var entity = new PlayerProgressionEntity();
        entity.setId(new PlayerProgressionId(playerId, ProgressionEventType.TRAINING, eventId));
        entity.setPlayer(player);
        entity.setTemporaryModifiers(Set.of(modifier));

        PlayerProgression domain = mapper.toDomain(entity);

        assertThat(domain.temporaryModifiers())
                .containsExactly(PlayerModifier.nextGameThreePointShotPctBonus(0.05));
        assertThat(domain.delta().temporaryModifiersAdded())
                .containsExactly(PlayerModifier.nextGameThreePointShotPctBonus(0.05));
    }

    @Test
    void toEntity_persistsBadgeAndTemporaryModifierSnapshots_fromDomain() {
        UUID playerId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        PlayerModifier modifier = PlayerModifier.nextGameThreePointShotPctBonus(0.05);

        var progression = new PlayerProgression(
                playerId,
                ProgressionEventType.TRAINING,
                eventId,
                Set.of(10L, 11L),
                Set.of(modifier),
                null
        );

        PlayerProgressionEntity entity = mapper.toEntity(progression);

        assertThat(entity.getId().getPlayerId()).isEqualTo(playerId);
        assertThat(entity.getId().getEventType()).isEqualTo(ProgressionEventType.TRAINING);
        assertThat(entity.getId().getEventId()).isEqualTo(eventId);
        assertThat(entity.getBadgeIds()).containsExactlyInAnyOrder(10L, 11L);
        assertThat(entity.getTemporaryModifiers())
                .containsExactly(new PlayerTemporaryModifierEmbeddable(
                        ModifierType.THREE_POINT,
                        Target.SHOT_PCT,
                        ModifierOp.ADD,
                        0.05,
                        1
                ));
    }

    private static PlayerProgressionMapperImpl mapper() {
        PlayerTemporaryModifierEntityMapper temporaryModifierMapper = new PlayerTemporaryModifierEntityMapperImpl();
        PlayerProgressionDeltaMapperImpl deltaMapper = new PlayerProgressionDeltaMapperImpl();
        setField(deltaMapper, "playerTemporaryModifierEntityMapper", temporaryModifierMapper);

        PlayerProgressionMapperImpl mapper = new PlayerProgressionMapperImpl();
        setField(mapper, "entityReferenceMapper", new EntityReferenceMapperImpl());
        setField(mapper, "playerProgressionDeltaMapper", deltaMapper);
        setField(mapper, "playerTemporaryModifierEntityMapper", temporaryModifierMapper);
        return mapper;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}

