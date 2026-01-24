package com.sanguiwara.gameevent;

import java.util.UUID;

public record TwoPointShotEvent  (
        UUID shooterId,
        int index,
        boolean assisted,
        UUID assisterId,
        double successPct,
        boolean made,
        double advantageMatchup,
        boolean blocked
)  implements ShotEvent
{}