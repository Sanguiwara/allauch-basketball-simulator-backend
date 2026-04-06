package com.sanguiwara.dto;

import com.sanguiwara.factory.PlayerArchetype;

public record CreatePlayerForTeamRequestDTO(
        PlayerArchetype archetype
) {
}

