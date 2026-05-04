package com.sanguiwara.controller;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Player;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.dto.PlayerDTO;
import com.sanguiwara.dto.PlayerScoresDTO;
import com.sanguiwara.dto.TeamDTO;
import com.sanguiwara.factory.PlayerArchetype;
import com.sanguiwara.mapper.PlayerDTOMapper;
import com.sanguiwara.mapper.TeamDTOMapper;
import com.sanguiwara.roster.TeamRosterService;
import com.sanguiwara.service.TeamService;
import com.sanguiwara.timeevent.EventManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private TeamDTOMapper teamDTOMapper;

    @MockitoBean
    private TeamRosterService teamRosterService;

    @MockitoBean
    private PlayerDTOMapper playerDTOMapper;

    @MockitoBean
    private EventManager eventManager;

    @Test
    void updateTeamName_returnsOk() throws Exception {
        UUID teamId = UUID.randomUUID();

        Team team = new Team(teamId, AgeCategory.U18, Gender.MALE, "old");
        TeamDTO dto = new TeamDTO(teamId, "new", AgeCategory.U18, Gender.MALE, null, List.of());

        when(teamService.updateName(eq(teamId), eq("new"))).thenReturn(team);
        when(teamDTOMapper.toDto(eq(team))).thenReturn(dto);

        mockMvc.perform(put("/teams/{id}/name", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"new\"}"))
                .andExpect(status().isOk());

        verify(teamService).updateName(eq(teamId), eq("new"));
    }

    @Test
    void updateTeamName_returnsBadRequest_whenBlankName() throws Exception {
        UUID teamId = UUID.randomUUID();

        mockMvc.perform(put("/teams/{id}/name", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(teamService);
    }

    @Test
    void updateTeamName_returnsNotFound_whenTeamDoesNotExist() throws Exception {
        UUID teamId = UUID.randomUUID();

        when(teamService.updateName(eq(teamId), eq("new"))).thenThrow(new NoSuchElementException("Team not found"));

        mockMvc.perform(put("/teams/{id}/name", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"new\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPlayerForTeam_returnsCreated() throws Exception {
        UUID teamId = UUID.randomUUID();

        Player created = Player.builder()
                .id(UUID.randomUUID())
                .name("test")
                .birthDate(19990101)
                .teamsID(new HashSet<>(List.of(teamId)))
                .clubID(null)
                .badgeIds(new HashSet<>())
                .injured(false)
                .tir3Pts(50)
                .tir2Pts(50)
                .lancerFranc(50)
                .floater(50)
                .finitionAuCercle(50)
                .speed(50)
                .ballhandling(50)
                .size(50)
                .weight(50)
                .agressivite(50)
                .defExterieur(50)
                .defPoste(50)
                .protectionCercle(50)
                .timingRebond(50)
                .agressiviteRebond(50)
                .steal(50)
                .timingBlock(50)
                .physique(50)
                .basketballIqOff(50)
                .basketballIqDef(50)
                .passingSkills(50)
                .iq(50)
                .endurance(50)
                .solidite(50)
                .potentielSkill(50)
                .potentielPhysique(50)
                .coachability(50)
                .ego(50)
                .softSkills(50)
                .leadership(50)
                .morale(50)
                .build();

        PlayerDTO dto = new PlayerDTO(
                created.getId(),
                created.getName(),
                created.getBirthDate(),
                created.getTir3Pts(),
                created.getTir2Pts(),
                created.getLancerFranc(),
                created.getFloater(),
                created.getFinitionAuCercle(),
                created.getSpeed(),
                created.getBallhandling(),
                created.getSize(),
                created.getWeight(),
                created.getAgressivite(),
                created.getDefExterieur(),
                created.getDefPoste(),
                created.getProtectionCercle(),
                created.getTimingRebond(),
                created.getAgressiviteRebond(),
                created.getSteal(),
                created.getTimingBlock(),
                created.getPhysique(),
                created.getBasketballIqOff(),
                created.getBasketballIqDef(),
                created.getPassingSkills(),
                created.getIq(),
                created.getEndurance(),
                created.getSolidite(),
                created.getPotentielSkill(),
                created.getPotentielPhysique(),
                created.getCoachability(),
                created.getEgo(),
                created.getSoftSkills(),
                created.getLeadership(),
                created.getMorale(),
                List.of(),
                created.getClubID(),
                created.getTeamsID(),
                new PlayerScoresDTO(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        );

        when(teamRosterService.createPlayerForTeam(eq(teamId), eq(PlayerArchetype.ALL_STAR))).thenReturn(created);
        when(playerDTOMapper.toDto(eq(created))).thenReturn(dto);

        mockMvc.perform(post("/teams/{teamId}/players", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"archetype\":\"ALL_STAR\"}"))
                .andExpect(status().isCreated());

        verify(teamRosterService).createPlayerForTeam(eq(teamId), eq(PlayerArchetype.ALL_STAR));
    }

    @Test
    void createPlayerForTeam_returnsBadRequest_whenArchetypeMissing() throws Exception {
        UUID teamId = UUID.randomUUID();

        mockMvc.perform(post("/teams/{teamId}/players", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(teamRosterService);
    }

    @Test
    void createPlayerForTeam_returnsNotFound_whenTeamDoesNotExist() throws Exception {
        UUID teamId = UUID.randomUUID();

        when(teamRosterService.createPlayerForTeam(eq(teamId), eq(PlayerArchetype.ALL_STAR)))
                .thenThrow(new NoSuchElementException("Team not found"));

        mockMvc.perform(post("/teams/{teamId}/players", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"archetype\":\"ALL_STAR\"}"))
                .andExpect(status().isNotFound());
    }
}

