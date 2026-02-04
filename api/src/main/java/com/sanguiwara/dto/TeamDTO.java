package com.sanguiwara.dto;

import com.sanguiwara.baserecords.AgeCategory;
import com.sanguiwara.baserecords.Gender;

import java.util.List;
import java.util.UUID;

public record TeamDTO(
        UUID id,
        String name,
        AgeCategory category,
        Gender gender,
        UUID clubId,
        List<PlayerDTO> players
) {
}

