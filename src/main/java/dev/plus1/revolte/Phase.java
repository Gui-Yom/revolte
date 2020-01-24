package dev.plus1.revolte;

import java.time.Duration;
import java.util.HashMap;

public enum Phase {

    FINISHED,
    DAY,
    NIGHT_END,
    NIGHT,
    /**
     * A game start with the JOIN phase
     */
    JOIN;

    public static HashMap<String, Duration> getDefaultPhasesDuration() {
        HashMap<String, Duration> phasesDuration = new HashMap<>(5);

        return phasesDuration;
    }

    public Phase next() {
        // No forward reference allowed :(
        switch (this) {
            case JOIN:
            case DAY:
                return NIGHT;
            case NIGHT:
                return NIGHT_END;
            case NIGHT_END:
                return DAY;
            default:
                return null;
        }
    }
}
