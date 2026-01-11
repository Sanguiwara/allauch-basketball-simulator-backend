package com.sanguiwara.controller;

import com.sanguiwara.baserecords.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sanguiwara.service.PlayerService;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4201")

@RestController
@RequestMapping("/players")

@RequiredArgsConstructor
public class PlayerController
{
    private final PlayerService playerService;

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable UUID id) {
        return playerService.getPlayer(id);
    }

    @PostMapping("/generate")
    public ResponseEntity<Void> generatePlayers() {
        playerService.generate100Players();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllPlayers() {
        playerService.deleteAllPlayers();
        return ResponseEntity.noContent().build();
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable UUID id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }




}