package com.sanguiwara.controller;

import com.sanguiwara.dto.TrainingDTO;
import com.sanguiwara.dto.UpdateTrainingRequestDTO;
import com.sanguiwara.mapper.TrainingDTOMapper;
import com.sanguiwara.service.TrainingService;
import com.sanguiwara.timeevent.EventManager;
import com.sanguiwara.timeevent.TimeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4201")
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
        eventManager.listAllOrdered().forEach(TimeEvent::execute);
        return ResponseEntity.ok().build();
    }
}
