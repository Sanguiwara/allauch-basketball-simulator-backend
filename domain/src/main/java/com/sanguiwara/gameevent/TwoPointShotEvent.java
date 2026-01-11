package com.sanguiwara.gameevent;

import java.util.UUID;

public record TwoPointShotEvent(
        UUID shooterId,
        int shotNumber,
        boolean assisted,
        UUID assisterId,
        double shotPct,
        boolean made,
        double advantage2pt
) {}