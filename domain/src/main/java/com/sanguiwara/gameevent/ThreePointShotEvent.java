package com.sanguiwara.gameevent;

import java.util.UUID;

public record ThreePointShotEvent(
        UUID shooterId,
        int index,
        boolean assisted,
        UUID assisterId,
        double successPct,
        boolean made,
        double advantageMatchup

) implements ShotEvent {
}
