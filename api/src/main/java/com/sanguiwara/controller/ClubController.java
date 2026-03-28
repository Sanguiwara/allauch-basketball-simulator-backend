package com.sanguiwara.controller;

import com.sanguiwara.dto.ClubDTO;
import com.sanguiwara.dto.UpdateNameRequestDTO;
import com.sanguiwara.mapper.ClubDTOMapper;
import com.sanguiwara.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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

    @PutMapping("/{id}/name")
    public ClubDTO updateClubName(@PathVariable UUID id, @RequestBody UpdateNameRequestDTO request) {
        String name = request == null ? null : request.name();
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        try {
            return clubDTOMapper.toDto(clubService.updateName(id, name));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Club not found", e);
        }
    }
}


