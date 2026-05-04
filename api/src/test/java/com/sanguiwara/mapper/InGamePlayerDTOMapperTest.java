package com.sanguiwara.mapper;

import com.sanguiwara.baserecords.InGamePlayer;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.dto.InGamePlayerDTO;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class InGamePlayerDTOMapperTest {

    @Test
    void toDto_exposesCalculatedScoresThroughNestedPlayerDto() throws Exception {
        InGamePlayerDTOMapperImpl mapper = new InGamePlayerDTOMapperImpl();
        setField(mapper, "playerDTOMapper", new PlayerDTOMapperImpl());

        Player player = Player.builder()
                .id(UUID.randomUUID())
                .name("Test Player")
                .birthDate(2000)
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

        InGamePlayer inGamePlayer = new InGamePlayer(player, UUID.randomUUID());

        InGamePlayerDTO dto = mapper.toDto(inGamePlayer);

        assertThat(dto.player()).isNotNull();
        assertThat(dto.player().scores()).isNotNull();
        assertThat(dto.player().scores().threePtScore()).isCloseTo(77.0, within(0.0001));
        assertThat(dto.player().scores().threePtDefenseScore()).isCloseTo(44.0, within(0.0001));
        assertThat(dto.player().scores().twoPtScore()).isCloseTo(63.75, within(0.0001));
        assertThat(dto.player().scores().twoPtDefenseScore()).isCloseTo(52.0, within(0.0001));
        assertThat(dto.player().scores().driveScore()).isCloseTo(61.15, within(0.0001));
        assertThat(dto.player().scores().driveDefenseScore()).isCloseTo(50.2, within(0.0001));
        assertThat(dto.player().scores().manToManPlaymakingOffScore()).isCloseTo(67.55, within(0.0001));
        assertThat(dto.player().scores().manToManPlaymakingDefScore()).isCloseTo(41.4, within(0.0001));
        assertThat(dto.player().scores().zonePlaymakingOffScore()).isCloseTo(65.5, within(0.0001));
        assertThat(dto.player().scores().zonePlaymakingDefScore()).isCloseTo(43.0, within(0.0001));
        assertThat(dto.player().scores().zone23DefenseScore()).isCloseTo(38.25, within(0.0001));
        assertThat(dto.player().scores().zone32DefenseScore()).isCloseTo(40.3, within(0.0001));
        assertThat(dto.player().scores().zone212DefenseScore()).isCloseTo(37.15, within(0.0001));
        assertThat(dto.player().scores().reboundScore()).isCloseTo(56.6, within(0.0001));
        assertThat(dto.player().scores().stealScore()).isCloseTo(38.25, within(0.0001));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
