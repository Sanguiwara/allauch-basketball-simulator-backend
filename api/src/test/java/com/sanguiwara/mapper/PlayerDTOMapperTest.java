package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.dto.PlayerDTO;
import com.sanguiwara.factory.PlayerArchetype;
import com.sanguiwara.modifiers.PlayerModifier;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PlayerDTOMapperTest {

    @Test
    void toDto_includesCalculatedScores() throws Exception {
        PlayerDTOMapperImpl mapper = mapper();

        Player player = Player.builder()
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(2000)
                .archetype(PlayerArchetype.THREE_POINT_SHOOTER)
                .speed(60)
                .size(70)
                .weight(65)
                .agressivite(20)
                .defExterieur(40)
                .defPoste(50)
                .protectionCercle(35)
                .endurance(80)
                .timingRebond(75)
                .agressiviteRebond(55)
                .steal(30)
                .timingBlock(25)
                .physique(35)
                .tir3Pts(90)
                .tir2Pts(65)
                .finitionAuCercle(55)
                .ballhandling(75)
                .floater(45)
                .basketballIqOff(50)
                .basketballIqDef(10)
                .passingSkills(85)
                .iq(40)
                .coachability(60)
                .build();

        PlayerDTO dto = mapper.toDto(player);

        assertThat(dto.scores()).isNotNull();
        assertThat(dto.archetype()).isEqualTo(PlayerArchetype.THREE_POINT_SHOOTER);
        assertThat(dto.scores().threePtScore()).isCloseTo(77.0, within(0.0001));
        assertThat(dto.scores().threePtDefenseScore()).isCloseTo(44.0, within(0.0001));
        assertThat(dto.scores().twoPtScore()).isCloseTo(63.75, within(0.0001));
        assertThat(dto.scores().twoPtDefenseScore()).isCloseTo(52.0, within(0.0001));
        assertThat(dto.scores().driveScore()).isCloseTo(61.15, within(0.0001));
        assertThat(dto.scores().driveDefenseScore()).isCloseTo(50.2, within(0.0001));
        assertThat(dto.scores().manToManPlaymakingOffScore()).isCloseTo(67.55, within(0.0001));
        assertThat(dto.scores().manToManPlaymakingDefScore()).isCloseTo(41.4, within(0.0001));
        assertThat(dto.scores().zonePlaymakingOffScore()).isCloseTo(65.5, within(0.0001));
        assertThat(dto.scores().zonePlaymakingDefScore()).isCloseTo(43.0, within(0.0001));
        assertThat(dto.scores().zone23DefenseScore()).isCloseTo(38.25, within(0.0001));
        assertThat(dto.scores().zone32DefenseScore()).isCloseTo(40.3, within(0.0001));
        assertThat(dto.scores().zone212DefenseScore()).isCloseTo(37.15, within(0.0001));
        assertThat(dto.scores().reboundScore()).isCloseTo(56.6, within(0.0001));
        assertThat(dto.scores().stealScore()).isCloseTo(38.25, within(0.0001));
    }

    @Test
    void toDto_exposesTemporaryModifiers() throws Exception {
        PlayerDTOMapperImpl mapper = mapper();
        PlayerModifier modifier = PlayerModifier.nextGameThreePointShotPctBonus(0.05);

        Player player = Player.builder()
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(2000)
                .temporaryModifiers(Set.of(modifier))
                .build();

        PlayerDTO dto = mapper.toDto(player);

        assertThat(dto.temporaryModifiers()).hasSize(1);
        assertThat(dto.temporaryModifiers().getFirst().effectType()).isEqualTo(modifier.effectType());
        assertThat(dto.temporaryModifiers().getFirst().target()).isEqualTo(modifier.target());
        assertThat(dto.temporaryModifiers().getFirst().op()).isEqualTo(modifier.op());
        assertThat(dto.temporaryModifiers().getFirst().value()).isEqualTo(modifier.value());
        assertThat(dto.temporaryModifiers().getFirst().gamesRemaining()).isEqualTo(modifier.gamesRemaining());
    }

    private static PlayerDTOMapperImpl mapper() throws Exception {
        PlayerDTOMapperImpl mapper = new PlayerDTOMapperImpl();
        setField(mapper, "badgeDTOMapper", new BadgeDTOMapperImpl());
        setField(mapper, "temporaryModifierDTOMapper", new TemporaryModifierDTOMapperImpl());
        return mapper;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
