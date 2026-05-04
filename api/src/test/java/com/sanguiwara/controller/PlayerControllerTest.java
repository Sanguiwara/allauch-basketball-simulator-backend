package com.sanguiwara.controller;

import com.sanguiwara.baserecords.Player;
import com.sanguiwara.mapper.PlayerDTOMapper;
import com.sanguiwara.service.PlayerService;
import com.sanguiwara.timeevent.EventManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlayerController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    @MockitoBean
    private PlayerDTOMapper playerDTOMapper;

    // Required because Application defines an ApplicationRunner bean depending on EventManager.
    @MockitoBean
    private EventManager eventManager;

    @Test
    void removePlayerFromAllTeams_returnsNoContent_andClearsTeamIds() throws Exception {
        UUID playerId = UUID.randomUUID();

        Player existing = Player.builder()
                .id(playerId)
                .name("P")
                .birthDate(20000101)
                .teamsID(new HashSet<>(Set.of(UUID.randomUUID())))
                .clubID(null)
                .badgeIds(new HashSet<>())
                .injured(false)
                .tir3Pts(0)
                .tir2Pts(0)
                .lancerFranc(0)
                .floater(0)
                .finitionAuCercle(0)
                .speed(0)
                .ballhandling(0)
                .size(0)
                .weight(0)
                .agressivite(0)
                .defExterieur(0)
                .defPoste(0)
                .protectionCercle(0)
                .timingRebond(0)
                .agressiviteRebond(0)
                .steal(0)
                .timingBlock(0)
                .physique(0)
                .basketballIqOff(0)
                .basketballIqDef(0)
                .passingSkills(0)
                .iq(0)
                .endurance(0)
                .solidite(0)
                .potentielSkill(0)
                .potentielPhysique(0)
                .coachability(0)
                .ego(0)
                .softSkills(0)
                .leadership(0)
                .morale(0)
                .build();

        when(playerService.getPlayer(eq(playerId))).thenReturn(existing);
        when(playerService.savePlayer(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(delete("/players/{id}/teams", playerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(playerService).savePlayer(captor.capture());
        assertThat(captor.getValue().getTeamsID()).isEmpty();
    }

    @Test
    void removePlayerFromAllTeams_returnsNotFound_whenPlayerDoesNotExist() throws Exception {
        UUID playerId = UUID.randomUUID();
        when(playerService.getPlayer(eq(playerId))).thenReturn(null);

        mockMvc.perform(delete("/players/{id}/teams", playerId))
                .andExpect(status().isNotFound());

        verify(playerService, never()).savePlayer(any());
    }
}

