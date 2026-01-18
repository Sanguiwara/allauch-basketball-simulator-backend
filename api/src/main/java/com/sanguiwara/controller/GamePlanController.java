package com.sanguiwara.controller;


import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.dto.GamePlanDTO;
import com.sanguiwara.mapper.GamePlanDTOMapper;
import com.sanguiwara.service.GamePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4201")

@RestController
@RequestMapping("/gameplans")

@RequiredArgsConstructor
public class GamePlanController {

    private final GamePlanService gamePlanService;
    private final GamePlanDTOMapper gamePlanDTOMapper;

    @GetMapping("/{id}")
    public ResponseEntity<GamePlanDTO> getGamePlan(@PathVariable UUID id) {

        return ResponseEntity.of(gamePlanService.getGamePlan(id).map(gamePlanDTOMapper::toDTO));
    }

    @PostMapping("/generate")
    public ResponseEntity<GamePlan> generateGamePlan() {
        GamePlan gameplan = gamePlanService.generateGamePlan();
        return ResponseEntity.of(Optional.of(gameplan));
    }

    @PostMapping()
    public ResponseEntity<Void> saveGamePlan( @RequestBody GamePlanDTO gamePlanDTO) {

        gamePlanService.save(gamePlanDTOMapper.toDomain(gamePlanDTO));
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();


    }


}
