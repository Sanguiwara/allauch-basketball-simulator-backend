package com.sanguiwara.gameevent;

import java.util.UUID;

public record DriveEvent(
        UUID attackerId,
        int driveNumber,
        boolean assisted,
        UUID assisterId,
        double successPct,
        boolean made,
        boolean foulDrawn,
        double advantageDrive
) {}
