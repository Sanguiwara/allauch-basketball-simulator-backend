package com.sanguiwara.controller;

import com.sanguiwara.dto.TeamDTO;
import com.sanguiwara.dto.UpdateNameRequestDTO;
import com.sanguiwara.mapper.TeamDTOMapper;
import com.sanguiwara.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamDTOMapper teamDTOMapper;
    private final TeamService teamService;

    @GetMapping("/{id}")
    public TeamDTO getTeamById(@PathVariable UUID id) {
        return teamDTOMapper.toDto(teamService.getTeam(id));
    }

    @GetMapping
    public List<TeamDTO> getAllTeams() {
        return teamService.getAllTeams().stream().map(teamDTOMapper::toDto).toList();
    }

    @PutMapping("/{id}/name")
    public TeamDTO updateTeamName(@PathVariable UUID id, @RequestBody UpdateNameRequestDTO request) {
        String name = request == null ? null : request.name();
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        try {
            return teamDTOMapper.toDto(teamService.updateName(id, name));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found", e);
        }
    }

}
