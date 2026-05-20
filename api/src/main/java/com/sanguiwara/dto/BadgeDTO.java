package com.sanguiwara.dto;

import com.sanguiwara.badges.ModifierType;

import java.util.Set;

public record BadgeDTO(
        long id,
        String name,
        double dropRate,
        Set<ModifierType> types
) {
}
