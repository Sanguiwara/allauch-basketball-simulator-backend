package com.sanguiwara.controller;

import com.sanguiwara.dto.PlayerDTO;
import com.sanguiwara.mapper.PlayerDTOMapper;
import com.sanguiwara.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/players")

@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerDTOMapper playerDTOMapper;

    @GetMapping("/{id}")
    public PlayerDTO getPlayer(@PathVariable UUID id) {
        return playerDTOMapper.toDto(playerService.getPlayer(id));
    }


    @GetMapping
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers().stream().map(playerDTOMapper::toDto).toList();
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

