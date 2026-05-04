package com.sanguiwara.controller;

import com.sanguiwara.baserecords.Club;
import com.sanguiwara.dto.ClubDTO;
import com.sanguiwara.mapper.ClubDTOMapper;
import com.sanguiwara.service.ClubService;
import com.sanguiwara.timeevent.EventManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

@WebMvcTest(controllers = ClubController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClubService clubService;

    @MockitoBean
    private ClubDTOMapper clubDTOMapper;

    @MockitoBean
    private EventManager eventManager;

    @Test
    void updateClubName_returnsOk() throws Exception {
        UUID clubId = UUID.randomUUID();

        Club club = new Club("old");
        club.setId(clubId);

        ClubDTO dto = new ClubDTO(clubId, "new", List.of(), null);

        when(clubService.updateName(eq(clubId), eq("new"))).thenReturn(club);
        when(clubDTOMapper.toDto(eq(club))).thenReturn(dto);

        mockMvc.perform(put("/clubs/{id}/name", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"new\"}"))
                .andExpect(status().isOk());

        verify(clubService).updateName(eq(clubId), eq("new"));
    }

    @Test
    void updateClubName_returnsBadRequest_whenBlankName() throws Exception {
        UUID clubId = UUID.randomUUID();

        mockMvc.perform(put("/clubs/{id}/name", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(clubService);
    }

    @Test
    void updateClubName_returnsNotFound_whenClubDoesNotExist() throws Exception {
        UUID clubId = UUID.randomUUID();

        when(clubService.updateName(eq(clubId), eq("new"))).thenThrow(new NoSuchElementException("Club not found"));

        mockMvc.perform(put("/clubs/{id}/name", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"new\"}"))
                .andExpect(status().isNotFound());
    }
}

