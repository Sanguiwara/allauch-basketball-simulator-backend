package com.sanguiwara.controller;


import com.sanguiwara.baserecords.GamePlan;
import com.sanguiwara.service.GamePlanService;
import lombok.RequiredArgsConstructor;
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


    @GetMapping("/{id}")
    public ResponseEntity<GamePlan> getGamePlan(@PathVariable UUID id) {

        return ResponseEntity.of(gamePlanService.getGamePlan(id));
    }

    @PostMapping("/generate")
    public ResponseEntity<GamePlan> generateGamePlan() {
        GamePlan gameplan = gamePlanService.generateGamePlan();
        return ResponseEntity.of(Optional.of(gameplan));
    }


}
