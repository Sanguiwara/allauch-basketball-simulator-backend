package com.sanguiwara.controller;


import com.sanguiwara.dto.GamePlanDTO;
import com.sanguiwara.dto.SeasonInitMode;
import com.sanguiwara.dto.SeasonInitRequest;
import com.sanguiwara.initializer.SeasonInitializer;
import com.sanguiwara.mapper.GamePlanDTOMapper;
import com.sanguiwara.service.GamePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/gameplans")

@RequiredArgsConstructor
public class GamePlanController {

    private final GamePlanService gamePlanService;
    private final GamePlanDTOMapper gamePlanDTOMapper;
    private final SeasonInitializer seasonInitializer;

    @GetMapping("/{id}")
    public ResponseEntity<GamePlanDTO> getGamePlan(@PathVariable UUID id) {

        return ResponseEntity.of(gamePlanService.getGamePlan(id).map(gamePlanDTOMapper::toDTO));
    }


    @PostMapping("/init")
    public ResponseEntity<Void> init(@RequestBody(required = false) SeasonInitRequest request) {
        SeasonInitMode mode = request == null || request.mode() == null
                ? SeasonInitMode.TEN_MINUTES_FROM_NOW
                : request.mode();

        Instant now = Instant.now();

        switch (mode) {
            case DAILY_MATCH_AND_TRAINING_FROM_DAY -> {
                LocalDate startDay = request.startDay();
                if (startDay == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "startDay is required for mode=DAILY_MATCH_AND_TRAINING_FROM_DAY"
                    );
                }
                seasonInitializer.createSeasonDailyMatchAndTrainingFromDay(startDay);
            }
            case TEN_MINUTES_FROM_NOW -> seasonInitializer.createSeasonEvery10MinutesFromNow(now);
            case DAILY_FROM_MONTH_AGO_REPLAY -> seasonInitializer.createSeasonDailyFromMonthAgoAndReplay(now);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping()
    public ResponseEntity<GamePlanDTO> saveGamePlan(@RequestBody GamePlanDTO gamePlanDTO) {
        var savedGamePlan = gamePlanService.update(gamePlanDTOMapper.toDomain(gamePlanDTO));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(gamePlanDTOMapper.toDTO(savedGamePlan));
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<GamePlanDTO> getNextGameForAClub(@PathVariable UUID clubId) {

        return ResponseEntity.of(gamePlanService.getNextUpcomingGamePlanForClub(clubId).map(gamePlanDTOMapper::toDTO));
    }

    @GetMapping("/userSub/{sub}/next")
    public ResponseEntity<GamePlanDTO> getNextGameForAUser(@PathVariable String sub) {
        return ResponseEntity.of(gamePlanService.getNextUpcomingGamePlanForAUserSub(sub).map(gamePlanDTOMapper::toDTO));
    }


}

