package com.sanguiwara.gameevent;

import com.sanguiwara.type.ShotType;

import java.util.UUID;

public record ThreePointShotEvent(
        UUID shooterId,
        int index,
        boolean assisted,
        UUID assisterId,
        double successPct,
        boolean made,
        double advantageMatchup,
        boolean blocked,
        ShotType shotType

) implements ShotEvent {
}
