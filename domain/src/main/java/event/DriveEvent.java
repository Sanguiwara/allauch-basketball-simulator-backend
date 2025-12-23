package event;

public record DriveEvent(
        long attackerId,
        int driveNumber,
        boolean assisted,
        Long assisterId,
        double successPct,
        boolean made,
        boolean foulDrawn,
        double advantageDrive
) {}
