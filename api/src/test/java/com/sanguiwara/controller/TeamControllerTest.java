package com.sanguiwara.controller;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Gender;
import com.sanguiwara.baserecords.Team;
import com.sanguiwara.dto.TeamDTO;
import com.sanguiwara.mapper.TeamDTOMapper;
import com.sanguiwara.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @MockBean
    private TeamDTOMapper teamDTOMapper;

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
}

