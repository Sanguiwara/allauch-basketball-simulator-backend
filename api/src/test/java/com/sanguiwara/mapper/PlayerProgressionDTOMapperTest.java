package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.modifiers.PlayerModifier;
import com.sanguiwara.progression.PlayerProgression;
import com.sanguiwara.progression.PlayerProgressionDelta;
import com.sanguiwara.progression.ProgressionEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerProgressionDTOMapperTest {

    private final PlayerProgressionDTOMapperImpl progressionMapper = new PlayerProgressionDTOMapperImpl();
    private final PlayerDeltaDTOMapperImpl deltaMapper = new PlayerDeltaDTOMapperImpl();

    @BeforeEach
    void setUp() throws Exception {
        BadgeDTOMapper badgeDTOMapper = new BadgeDTOMapperImpl();
        TemporaryModifierDTOMapper temporaryModifierDTOMapper = new TemporaryModifierDTOMapperImpl();
        setField(progressionMapper, "badgeDTOMapper", badgeDTOMapper);
        setField(progressionMapper, "temporaryModifierDTOMapper", temporaryModifierDTOMapper);
        setField(deltaMapper, "badgeDTOMapper", badgeDTOMapper);
        setField(deltaMapper, "temporaryModifierDTOMapper", temporaryModifierDTOMapper);
    }

    @Test
    void toDto_exposesTemporaryModifiersGainedByProgression() {
        UUID playerId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        PlayerModifier modifier = PlayerModifier.nextGameThreePointShotPctBonus(0.05);
        PlayerProgressionDelta delta = PlayerProgressionDelta.between(
                player(playerId, Set.of()),
                player(playerId, Set.of(modifier))
        );
        PlayerProgression progression = new PlayerProgression(
                playerId,
                ProgressionEventType.TRAINING,
                eventId,
                Set.of(),
                delta.temporaryModifiersAdded(),
                delta
        );

        var dto = progressionMapper.toDto(progression);

        assertThat(dto.temporaryModifiers()).hasSize(1);
        assertThat(dto.temporaryModifiers().get(0).effectType()).isEqualTo(modifier.effectType());
        assertThat(dto.temporaryModifiers().get(0).target()).isEqualTo(modifier.target());
        assertThat(dto.temporaryModifiers().get(0).op()).isEqualTo(modifier.op());
        assertThat(dto.temporaryModifiers().get(0).value()).isEqualTo(modifier.value());
        assertThat(dto.temporaryModifiers().get(0).gamesRemaining()).isEqualTo(modifier.gamesRemaining());
    }

    @Test
    void toDto_exposesTemporaryModifierDelta() {
        UUID playerId = UUID.randomUUID();
        PlayerModifier modifier = PlayerModifier.nextGameThreePointShotPctBonus(0.05);
        PlayerProgressionDelta delta = PlayerProgressionDelta.between(
                player(playerId, Set.of()),
                player(playerId, Set.of(modifier))
        );

        var dto = deltaMapper.toDto(delta);

        assertThat(dto.temporaryModifiersAdded()).hasSize(1);
        assertThat(dto.temporaryModifiersRemoved()).isEmpty();
        assertThat(dto.temporaryModifiersAdded().get(0).effectType()).isEqualTo(modifier.effectType());
    }

    private static Player player(UUID playerId, Set<PlayerModifier> temporaryModifiers) {
        return Player.builder()
                .id(playerId)
                .name("P")
                .birthDate(1990)
                .badgeIds(Set.of())
                .temporaryModifiers(temporaryModifiers)
                .build();
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
