package com.sanguiwara.baserecords;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class InGamePlayerTest {

    @Test
    void recalculateScores_updatesAllStoredScores() {
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

        inGamePlayer.recalculateScores();

        assertThat(inGamePlayer.getThreePtScore()).isCloseTo(77.0, within(0.0001));
        assertThat(inGamePlayer.getThreePtDefenseScore()).isCloseTo(44.0, within(0.0001));
        assertThat(inGamePlayer.getTwoPtScore()).isCloseTo(63.75, within(0.0001));
        assertThat(inGamePlayer.getTwoPtDefenseScore()).isCloseTo(52.0, within(0.0001));
        assertThat(inGamePlayer.getDriveScore()).isCloseTo(61.15, within(0.0001));
        assertThat(inGamePlayer.getDriveDefenseScore()).isCloseTo(50.2, within(0.0001));
        assertThat(inGamePlayer.getManToManPlaymakingOffScore()).isCloseTo(67.55, within(0.0001));
        assertThat(inGamePlayer.getManToManPlaymakingDefScore()).isCloseTo(41.4, within(0.0001));
        assertThat(inGamePlayer.getZonePlaymakingOffScore()).isCloseTo(65.5, within(0.0001));
        assertThat(inGamePlayer.getZonePlaymakingDefScore()).isCloseTo(43.0, within(0.0001));
        assertThat(inGamePlayer.getZone23DefenseScore()).isCloseTo(38.25, within(0.0001));
        assertThat(inGamePlayer.getZone32DefenseScore()).isCloseTo(40.3, within(0.0001));
        assertThat(inGamePlayer.getZone212DefenseScore()).isCloseTo(37.15, within(0.0001));
        assertThat(inGamePlayer.getReboundScore()).isCloseTo(56.6, within(0.0001));
        assertThat(inGamePlayer.getStealScore()).isCloseTo(38.25, within(0.0001));
    }
}
