package com.sanguiwara.controller;

import com.sanguiwara.dto.TrainingDTO;
import com.sanguiwara.dto.UpdateTrainingRequestDTO;
import com.sanguiwara.mapper.TrainingDTOMapper;
import com.sanguiwara.service.TrainingService;
import com.sanguiwara.timeevent.EventManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingDTOMapper trainingDTOMapper;
    private final EventManager eventManager;

    @GetMapping("/teamID/{teamId}")
    public ResponseEntity<List<TrainingDTO>> getTrainingsForATeam(@PathVariable UUID teamId) {
        var list = trainingService.getAllTrainingsForATeam(teamId).stream().map(trainingDTOMapper::toDto).toList();
        return ResponseEntity.of(Optional.of(list));
    }

    @GetMapping("/teamID/{teamId}/next")
    public ResponseEntity<TrainingDTO> getNextTrainingForATeam(@PathVariable UUID teamId) {
        return ResponseEntity.of(trainingService.getNextTrainingForATeam(teamId).map(trainingDTOMapper::toDto));
    }

    @GetMapping("/clubID/{clubId}/next")
    public ResponseEntity<TrainingDTO> getNextTrainingForAClub(@PathVariable UUID clubId) {
        return ResponseEntity.of(trainingService.getNextTrainingForAClub(clubId).map(trainingDTOMapper::toDto));
    }

    @GetMapping("/userSub/{sub}/next")
    public ResponseEntity<TrainingDTO> getNextTrainingForAUserSub(@PathVariable String sub) {
        return ResponseEntity.of(trainingService.getNextTrainingForAUserSub(sub).map(trainingDTOMapper::toDto));
    }

    @GetMapping("/userSub/{sub}")
    public ResponseEntity<List<TrainingDTO>> getTrainingsForAUserSub(@PathVariable String sub) {
        return ResponseEntity.of(trainingService.getAllTrainingsForAUserSub(sub)
                .map(list -> list.stream().map(trainingDTOMapper::toDto).toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingDTO> getTraining(@PathVariable UUID id) {
        return ResponseEntity.of(Optional.of(trainingDTOMapper.toDto(trainingService.getTrainingById(id))));
    }

    @GetMapping()
    public ResponseEntity<List<TrainingDTO>> getAllTrainings() {
        var list = trainingService.getAllTrainings().stream().map(trainingDTOMapper::toDto).toList();
        return ResponseEntity.of(Optional.of(list));
    }


    @PostMapping("/{id}/execute")
    public ResponseEntity<Void> executeTraining(@PathVariable UUID id) {
        trainingService.executeTraining(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingDTO> updateTraining(@PathVariable UUID id, @RequestBody UpdateTrainingRequestDTO request) {
        var updated = trainingService.updateTraining(id, request.trainingType());
        return ResponseEntity.ok(trainingDTOMapper.toDto(updated));
    }

    @GetMapping("/applyTrainings")
    public ResponseEntity<Void> testAllTrainingsEffects() {
        // Execute all due events now (and delete them from DB via EventManager policy).
        eventManager.runDueEvents(Instant.now());
        return ResponseEntity.ok().build();
    }
}

