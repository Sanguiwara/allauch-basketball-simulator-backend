package com.sanguiwara.controller;

import com.sanguiwara.dto.PlayerDTO;
import com.sanguiwara.dto.PlayerSeasonStateDTO;
import com.sanguiwara.mapper.PlayerDTOMapper;
import com.sanguiwara.mapper.PlayerSeasonStateDTOMapper;
import com.sanguiwara.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/players")

@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;
    private final PlayerDTOMapper playerDTOMapper;
    private final PlayerSeasonStateDTOMapper playerSeasonStateDTOMapper;

    @GetMapping("/{id}")
    public PlayerDTO getPlayer(@PathVariable UUID id) {
        return playerDTOMapper.toDto(playerService.getPlayer(id));
    }


    @GetMapping
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers().stream().map(playerDTOMapper::toDto).toList();
    }

    @GetMapping("/{id}/season-state")
    public PlayerSeasonStateDTO getPlayerSeasonState(
            @PathVariable UUID id
    ) {
        var state = playerService.getPlayerSeasonState(id);
        if (state == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player season state not found");
        }
        return playerSeasonStateDTOMapper.toDto(state);
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

    /**
     * Removes the player from all teams (free agent). This only clears team links (team_players join table).
     */
    @DeleteMapping("/{id}/teams")
    public ResponseEntity<Void> removePlayerFromAllTeams(@PathVariable UUID id) {
        var player = playerService.getPlayer(id);
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
        }

        player.setTeamsID(new HashSet<>());
        playerService.savePlayer(player);
        return ResponseEntity.noContent().build();
    }

}

