package com.sanguiwara.baserecords;

/**
 * Read-model used for "list games" endpoints.
 * Keeps only what the UI needs and avoids loading large object graphs.
 */
public record GameResultSummary(
        int homeThreePtAttempts,
        int homeThreePtMade,
        int homeDriveAttempts,
        int homeDriveMade,
        int homeTwoPtAttempts,
        int homeTwoPtMade,
        int awayThreePtAttempts,
        int awayThreePtMade,
        int awayDriveAttempts,
        int awayDriveMade,
        int awayTwoPtAttempts,
        int awayTwoPtMade
) {
}

