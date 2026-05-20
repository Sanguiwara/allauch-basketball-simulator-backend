package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.factory.PlayerArchetype;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerMapperTest {

    private final PlayerMapper mapper = mapper();

    @Test
    void toEntity_mapsBadgeIds_toBadgeEntities() {
        UUID playerId = UUID.randomUUID();
        var p = Player.builder()
                .id(playerId)
                .name("p")
                .birthDate(20000101)
                .archetype(PlayerArchetype.DRIVE_SPECIALIST)
                .badgeIds(Set.of(1L, 2L))
                .build();

        var e = mapper.toEntity(p);

        assertThat(e.getArchetype()).isEqualTo(PlayerArchetype.DRIVE_SPECIALIST);
        assertThat(e.getBadges()).extracting("id").containsExactlyInAnyOrder(1L, 2L);
    }

    private static PlayerMapperImpl mapper() {
        PlayerMapperImpl mapper = new PlayerMapperImpl();
        setField(mapper, "badgeEntityMapper", new BadgeEntityMapperImpl());
        setField(mapper, "entityReferenceMapper", new EntityReferenceMapperImpl());
        setField(mapper, "playerTemporaryModifierEntityMapper", new PlayerTemporaryModifierEntityMapperImpl());
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

