package com.sanguiwara.progression.manager;

final class MoraleDeltaScaler {

    private static final double MIN_MORALE_DELTA_MULTIPLIER = 0.5;
    private static final double MAX_MORALE_DELTA_MULTIPLIER = 1.5;

    private MoraleDeltaScaler() {
    }

    static int applyDelta(int currentMorale, int delta, int minMorale, int maxMorale) {
        return Math.clamp(currentMorale + scaleDelta(currentMorale, delta, minMorale, maxMorale), minMorale, maxMorale);
    }

    private static int scaleDelta(int currentMorale, int delta, int minMorale, int maxMorale) {
        if (delta == 0) {
            return 0;
        }

        double normalizedMorale = (currentMorale - minMorale) / (double) (maxMorale - minMorale);
        double multiplier = delta > 0
                ? MAX_MORALE_DELTA_MULTIPLIER - normalizedMorale
                : MIN_MORALE_DELTA_MULTIPLIER + normalizedMorale;

        int scaledMagnitude = Math.max(1, (int) Math.round(Math.abs(delta) * multiplier));
        return delta > 0 ? scaledMagnitude : -scaledMagnitude;
    }
}
