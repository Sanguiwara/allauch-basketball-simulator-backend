package com.sanguiwara.gameevent;

import java.util.UUID;

public record ThreePointShotEvent(
        UUID shooterId,
        int shotIndex,
        boolean assisted,
        UUID assisterPlayerId,  // -1 si pas assisté
        double shotPct,
        boolean made
) {}
