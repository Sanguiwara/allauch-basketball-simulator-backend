package com.sanguiwara.controller;

import com.sanguiwara.dto.ClubDTO;
import com.sanguiwara.mapper.ClubDTOMapper;
import com.sanguiwara.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4201")
@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final ClubDTOMapper clubDTOMapper;

    @GetMapping("/{id}")
    public ClubDTO getClub(@PathVariable UUID id) {
        return clubDTOMapper.toDto(clubService.getClub(id));
    }

    @GetMapping
    public List<ClubDTO> getAllClubs() {
        return clubService.getAllClubs().stream().map(clubDTOMapper::toDto).toList();
    }
}

