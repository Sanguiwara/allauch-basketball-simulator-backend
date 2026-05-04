package com.sanguiwara.controller;

import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.initializer.SeasonInitializer;
import com.sanguiwara.mapper.GamePlanDTOMapper;
import com.sanguiwara.service.GamePlanService;
import com.sanguiwara.timeevent.EventManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GamePlanController.class)
@AutoConfigureMockMvc(addFilters = false)
class GamePlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GamePlanService gamePlanService;

    @MockitoBean
    private GamePlanDTOMapper gamePlanDTOMapper;

    @MockitoBean
    private SeasonInitializer seasonInitializer;

    @MockitoBean
    private EventManager eventManager;

    @Test
    void saveGamePlan_returnsConflictWhenMatchIsFinished() throws Exception {
        GamePlan gamePlan = mock(GamePlan.class);
        when(gamePlanDTOMapper.toDomain(any())).thenReturn(gamePlan);
        when(gamePlanService.update(gamePlan)).thenThrow(new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Game plan can no longer be updated because the match is finished"
        ));

        String body = """
                {
                  "id": "%s",
                  "ownerTeam": null,
                  "opponentTeam": null,
                  "activePlayers": [],
                  "matchups": {},
                  "positions": {},
                  "threePointAttemptShare": 0.33,
                  "midRangeAttemptShare": 0.33,
                  "driveAttemptShare": 0.34,
                  "totalShotNumber": 75,
                  "defenseType": "MAN_TO_MAN"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/gameplans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());

        verify(gamePlanDTOMapper).toDomain(any());
        verify(gamePlanService).update(gamePlan);
    }
}
