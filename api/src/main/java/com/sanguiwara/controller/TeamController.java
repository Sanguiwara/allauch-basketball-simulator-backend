package com.sanguiwara.controller;

import com.sanguiwara.dto.TeamDTO;
import com.sanguiwara.mapper.TeamDTOMapper;
import com.sanguiwara.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4201")

@RestController
@RequestMapping("/teams")

@RequiredArgsConstructor

public class TeamController {
    private final TeamDTOMapper teamDTOMapper;
    private final TeamService teamService;

    @GetMapping("/{id}")
    public TeamDTO getPlayer(@PathVariable UUID id) {
        return teamDTOMapper.toDto(teamService.getTeam(id));
    }

}
