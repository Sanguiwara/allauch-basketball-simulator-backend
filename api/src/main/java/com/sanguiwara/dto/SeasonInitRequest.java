package com.sanguiwara.dto;

import java.time.LocalDate;

public record SeasonInitRequest(
        SeasonInitMode mode,
        LocalDate startDay
) {
}

