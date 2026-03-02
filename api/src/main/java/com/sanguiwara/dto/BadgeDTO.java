package com.sanguiwara.dto;

import com.sanguiwara.badges.BadgeType;

import java.util.Set;

public record BadgeDTO(
        long id,
        String name,
        double dropRate,
        Set<BadgeType> types
) {
}
