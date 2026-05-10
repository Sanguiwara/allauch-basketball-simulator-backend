package com.sanguiwara.dto;

import java.util.List;
import java.util.UUID;

public record ApplyGamePlanToUpcomingResponse(
        UUID sourceGamePlanId,
        int updatedGamePlanCount,
        List<UUID> updatedGamePlanIds
) {
}
