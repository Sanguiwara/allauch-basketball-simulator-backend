package com.sanguiwara.controller;


import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.dto.GamePlanDTO;
import com.sanguiwara.initializer.SeasonInitializer;
import com.sanguiwara.mapper.GamePlanDTOMapper;
import com.sanguiwara.service.GamePlanService;
import com.sanguiwara.timeevent.EventManager;
import com.sanguiwara.timeevent.TimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4201")

@RestController
@RequestMapping("/gameplans")

@RequiredArgsConstructor
public class GamePlanController {

    private final GamePlanService gamePlanService;
    private final GamePlanDTOMapper gamePlanDTOMapper;
    private final SeasonInitializer seasonInitializer;
    private final EventManager eventManager;

    @GetMapping("/{id}")
    public ResponseEntity<GamePlanDTO> getGamePlan(@PathVariable UUID id) {

        return ResponseEntity.of(gamePlanService.getGamePlan(id).map(gamePlanDTOMapper::toDTO));
    }

    @PostMapping("/generate")
    public ResponseEntity<GamePlan> generateGamePlan() {
        GamePlan gameplan = gamePlanService.generateGamePlan();
        return ResponseEntity.of(Optional.of(gameplan));
    }

    @PostMapping("/init")
    public ResponseEntity<GamePlan> init() {
        seasonInitializer.createSeason(Instant.now());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping()
    public ResponseEntity<Void> saveGamePlan(@RequestBody GamePlanDTO gamePlanDTO) {

        gamePlanService.update(gamePlanDTOMapper.toDomain(gamePlanDTO));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();


    }


}
