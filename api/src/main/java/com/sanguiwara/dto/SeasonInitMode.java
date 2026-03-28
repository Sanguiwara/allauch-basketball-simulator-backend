package com.sanguiwara.dto;

/**
 * Admin/dev-only: different season scheduling presets.
 */
public enum SeasonInitMode {
    /**
     * 1 round per day, with trainings; games at 20:00 Europe/Paris starting from startDay.
     */
    DAILY_MATCH_AND_TRAINING_FROM_DAY,
    /**
     * Fast iteration: 1 round every 10 minutes starting from now.
     */
    TEN_MINUTES_FROM_NOW,
    /**
     * 1 round per day starting from a month ago, then replay all events immediately.
     */
    DAILY_FROM_MONTH_AGO_REPLAY
}

