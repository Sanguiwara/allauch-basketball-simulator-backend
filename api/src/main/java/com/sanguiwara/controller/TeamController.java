package com.sanguiwara.controller;

import com.sanguiwara.dto.TeamDTO;
import com.sanguiwara.mapper.TeamDTOMapper;
import com.sanguiwara.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

}

