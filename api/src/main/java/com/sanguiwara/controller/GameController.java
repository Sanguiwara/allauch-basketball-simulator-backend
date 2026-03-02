package com.sanguiwara.controller;

import com.sanguiwara.dto.GameDTO;
import com.sanguiwara.dto.SimplifiedGameDTO;
import com.sanguiwara.mapper.GameDTOMapper;
import com.sanguiwara.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@CrossOrigin(origins = "http://localhost:4201")
@Controller
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final GameDTOMapper gameDTOMapper;

    @GetMapping("/teamID/{teamId}")
    public ResponseEntity<List<GameDTO>> getGameForATeam(@PathVariable UUID teamId) {

        return ResponseEntity.of(Optional.of(gameService.getAllGamesForATeam(teamId).stream().map(gameDTOMapper::toDto).toList()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTO> getGame(@PathVariable UUID id) {
        return ResponseEntity.of(Optional.of(gameDTOMapper.toDto(gameService.getGameById(id))));
    }

    @GetMapping()
    public ResponseEntity<List<SimplifiedGameDTO>> getAllGames() {

        return ResponseEntity.of(Optional.of(gameService.getAllGames().stream().map(gameDTOMapper::toSimplifiedDto).toList()));

    }



}
