package com.sanguiwara.controller;

import com.sanguiwara.baserecords.TrainingType;
import com.sanguiwara.dto.TrainingProgressionDTO;
import com.sanguiwara.dto.TrainingProgressionImpactDTO;
import com.sanguiwara.mapper.TrainingDTOMapper;
import com.sanguiwara.mapper.TrainingProgressionDTOMapper;
import com.sanguiwara.progression.ProgressionSkillGroup;
import com.sanguiwara.progression.training.TrainablePlayerStat;
import com.sanguiwara.progression.training.TrainingProgression;
import com.sanguiwara.progression.training.TrainingProgressions;
import com.sanguiwara.service.TrainingService;
import com.sanguiwara.timeevent.EventManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingService trainingService;

    @MockitoBean
    private TrainingDTOMapper trainingDTOMapper;

    @MockitoBean
    private TrainingProgressionDTOMapper trainingProgressionDTOMapper;

    @MockitoBean
    private EventManager eventManager;

    @Test
    void getTrainingCatalog_returnsAvailableTrainingProgressions() throws Exception {
        TrainingProgression shooting = TrainingProgressions.defaultFor(TrainingType.SHOOTING);
        TrainingProgression threePointFocus = TrainingProgressions.defaultFor(TrainingType.THREE_POINT_FOCUS);

        TrainingProgressionDTO shootingDTO = new TrainingProgressionDTO(
                TrainingType.SHOOTING,
                List.of(new TrainingProgressionImpactDTO(
                        TrainablePlayerStat.TIR_3_PTS,
                        "tir3Pts",
                        ProgressionSkillGroup.THREE_POINT,
                        1,
                        2,
                        1.0
                )),
                List.of(),
                1.0,
                List.of()
        );
        TrainingProgressionDTO focusDTO = new TrainingProgressionDTO(
                TrainingType.THREE_POINT_FOCUS,
                List.of(),
                List.of(),
                0.0,
                List.of()
        );

        when(trainingService.getAvailableTrainingProgressions()).thenReturn(List.of(shooting, threePointFocus));
        when(trainingProgressionDTOMapper.toDto(shooting)).thenReturn(shootingDTO);
        when(trainingProgressionDTOMapper.toDto(threePointFocus)).thenReturn(focusDTO);

        mockMvc.perform(get("/trainings/catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("SHOOTING"))
                .andExpect(jsonPath("$[0].statImpacts[0].stat").value("TIR_3_PTS"))
                .andExpect(jsonPath("$[0].statImpacts[0].playerField").value("tir3Pts"))
                .andExpect(jsonPath("$[0].statImpacts[0].progressionMultiplier").value(1.0))
                .andExpect(jsonPath("$[1].type").value("THREE_POINT_FOCUS"))
                .andExpect(jsonPath("$[1].statImpacts").isEmpty());

        verify(trainingService).getAvailableTrainingProgressions();
        verify(trainingProgressionDTOMapper).toDto(shooting);
        verify(trainingProgressionDTOMapper).toDto(threePointFocus);
    }
}
