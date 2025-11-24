package com.chess.core.entities.game;

/**
 * Gestion du contrôle du temps (Bullet, Blitz, Rapide, etc.).
 */
public class TimeControl {
    private final TimeControlType type;
    private final int initialTimeSeconds;
    private final int incrementSeconds;

    public TimeControl(TimeControlType type, int initialTimeSeconds, int incrementSeconds) {
        this.type = type;
        this.initialTimeSeconds = initialTimeSeconds;
        this.incrementSeconds = incrementSeconds;
    }

    public static TimeControl bullet() {
        return new TimeControl(TimeControlType.BULLET, 60, 0); // 1+0
    }

    public static TimeControl blitz() {
        return new TimeControl(TimeControlType.BLITZ, 180, 2); // 3+2
    }

    public static TimeControl rapid() {
        return new TimeControl(TimeControlType.RAPID, 600, 0); // 10+0
    }

    public static TimeControl custom(int minutes, int increment) {
        int seconds = minutes * 60;
        TimeControlType type = determineType(seconds, increment);
        return new TimeControl(type, seconds, increment);
    }

    private static TimeControlType determineType(int seconds, int increment) {
        int totalTime = seconds + increment * 40; // Estimation pour 40 coups

        if (totalTime < 180) {
            return TimeControlType.BULLET;
        } else if (totalTime < 600) {
            return TimeControlType.BLITZ;
        } else if (totalTime < 1800) {
            return TimeControlType.RAPID;
        } else {
            return TimeControlType.CLASSICAL;
        }
    }

    public TimeControlType getType() {
        return type;
    }

    public int getInitialTimeSeconds() {
        return initialTimeSeconds;
    }

    public int getIncrementSeconds() {
        return incrementSeconds;
    }

    public String getDisplayName() {
        int minutes = initialTimeSeconds / 60;
        int seconds = initialTimeSeconds % 60;

        if (seconds == 0) {
            return minutes + "+" + incrementSeconds;
        } else {
            return minutes + ":" + String.format("%02d", seconds) + "+" + incrementSeconds;
        }
    }

    @Override
    public String toString() {
        return type.getLabel() + " (" + getDisplayName() + ")";
    }

    public enum TimeControlType {
        BULLET("Bullet"),
        BLITZ("Blitz"),
        RAPID("Rapide"),
        CLASSICAL("Classique"),
        CUSTOM("Personnalisé");

        private final String label;

        TimeControlType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}