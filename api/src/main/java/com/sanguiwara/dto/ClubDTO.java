package com.sanguiwara.dto;

import java.util.List;
import java.util.UUID;

public record ClubDTO(
        UUID id,
        String name,
        List<TeamDTO> teams,
        List<PlayerDTO> players
) {
}
