package com.sanguiwara.controller;

import com.sanguiwara.baserecords.Game;
import com.sanguiwara.game.GameSchedulingService;
import com.sanguiwara.mapper.GameDTOMapper;
import com.sanguiwara.service.GameService;
import com.sanguiwara.timeevent.EventManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerScheduleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private GameDTOMapper gameDTOMapper;

    @MockBean
    private GameSchedulingService gameSchedulingService;

    @MockBean
    private EventManager eventManager;

    @Test
    void scheduleGame_returnsCreated_andGameId() throws Exception {
        UUID homeTeamId = UUID.randomUUID();
        UUID awayTeamId = UUID.randomUUID();
        LocalDateTime localDateTime = LocalDateTime.parse("2026-04-08T15:00:00");
        String zoneId = "America/Chicago";
        Instant executeAt = ZonedDateTime.of(localDateTime, ZoneId.of(zoneId)).toInstant();
        UUID leagueSeasonId = UUID.randomUUID();

        UUID gameId = UUID.randomUUID();
        Game created = new Game(gameId, null, null, null, executeAt);

        when(gameSchedulingService.scheduleGame(eq(homeTeamId), eq(awayTeamId), eq(executeAt), eq(leagueSeasonId)))
                .thenReturn(created);

        String body = """
                {
                  "homeTeamId": "%s",
                  "awayTeamId": "%s",
                  "localDateTime": "%s",
                  "zoneId": "%s",
                  "leagueSeasonId": "%s"
                }
                """.formatted(homeTeamId, awayTeamId, localDateTime, zoneId, leagueSeasonId);

        mockMvc.perform(post("/games/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().string('"' + gameId.toString() + '"'));

        verify(gameSchedulingService).scheduleGame(eq(homeTeamId), eq(awayTeamId), eq(executeAt), eq(leagueSeasonId));
    }

    @Test
    void scheduleGame_returnsBadRequest_whenLocalDateTimeMissing() throws Exception {
        UUID homeTeamId = UUID.randomUUID();
        UUID awayTeamId = UUID.randomUUID();

        String body = """
                {
                  "homeTeamId": "%s",
                  "awayTeamId": "%s"
                }
                """.formatted(homeTeamId, awayTeamId);

        mockMvc.perform(post("/games/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void scheduleGame_returnsBadRequest_whenZoneIdMissing() throws Exception {
        UUID homeTeamId = UUID.randomUUID();
        UUID awayTeamId = UUID.randomUUID();
        LocalDateTime localDateTime = LocalDateTime.parse("2026-04-08T15:00:00");
        UUID leagueSeasonId = UUID.randomUUID();

        String body = """
                {
                  "homeTeamId": "%s",
                  "awayTeamId": "%s",
                  "localDateTime": "%s",
                  "leagueSeasonId": "%s"
                }
                """.formatted(homeTeamId, awayTeamId, localDateTime, leagueSeasonId);

        mockMvc.perform(post("/games/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void scheduleGame_returnsBadRequest_whenLeagueSeasonIdMissing() throws Exception {
        UUID homeTeamId = UUID.randomUUID();
        UUID awayTeamId = UUID.randomUUID();
        LocalDateTime localDateTime = LocalDateTime.parse("2026-04-08T15:00:00");
        String zoneId = "America/Chicago";

        String body = """
                {
                  "homeTeamId": "%s",
                  "awayTeamId": "%s",
                  "localDateTime": "%s",
                  "zoneId": "%s"
                }
                """.formatted(homeTeamId, awayTeamId, localDateTime, zoneId);

        mockMvc.perform(post("/games/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
