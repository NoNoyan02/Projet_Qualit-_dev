package com.chess.core.entities.game;

import com.chess.core.entities.Color;

import javax.swing.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Horloge de jeu d'échecs avec incréments.
 */
public class GameClock {
    private final Map<Color, Integer> remainingTime; // En millisecondes
    private final int incrementMs;
    private Color activeColor;
    private Timer timer;
    private long lastUpdateTime;
    private boolean running;

    private Consumer<Color> onTimeUpdate;
    private Consumer<Color> onTimeExpired;
    private Consumer<Color> onLowTime; // Alerte temps faible

    public GameClock(TimeControl timeControl) {
        this.remainingTime = new HashMap<>();
        remainingTime.put(Color.WHITE, timeControl.getInitialTimeSeconds() * 1000);
        remainingTime.put(Color.BLACK, timeControl.getInitialTimeSeconds() * 1000);
        this.incrementMs = timeControl.getIncrementSeconds() * 1000;
        this.activeColor = Color.WHITE;
        this.running = false;
    }

    public void start() {
        if (running) return;

        running = true;
        lastUpdateTime = System.currentTimeMillis();

        timer = new Timer(100, e -> update());
        timer.start();
    }

    public void pause() {
        if (!running) return;

        running = false;
        if (timer != null) {
            timer.stop();
        }
        update(); // Dernière mise à jour
    }

    public void stop() {
        pause();
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    public void switchPlayer() {
        if (!running) return;

        // Ajoute l'incrément au joueur qui vient de jouer
        int currentTime = remainingTime.get(activeColor);
        remainingTime.put(activeColor, currentTime + incrementMs);

        // Change de joueur
        activeColor = activeColor.opposite();
        lastUpdateTime = System.currentTimeMillis();

        if (onTimeUpdate != null) {
            onTimeUpdate.accept(activeColor);
        }
    }

    private void update() {
        if (!running) return;

        long now = System.currentTimeMillis();
        long elapsed = now - lastUpdateTime;
        lastUpdateTime = now;

        int currentTime = remainingTime.get(activeColor);
        int newTime = Math.max(0, currentTime - (int) elapsed);
        remainingTime.put(activeColor, newTime);

        if (onTimeUpdate != null) {
            onTimeUpdate.accept(activeColor);
        }

        // Alerte temps faible (< 10 secondes)
        if (newTime < 10000 && newTime > 0 && onLowTime != null) {
            onLowTime.accept(activeColor);
        }

        // Temps écoulé
        if (newTime == 0) {
            stop();
            if (onTimeExpired != null) {
                onTimeExpired.accept(activeColor);
            }
        }
    }

    public int getRemainingTime(Color color) {
        return remainingTime.get(color);
    }

    public String getFormattedTime(Color color) {
        int timeMs = remainingTime.get(color);
        int totalSeconds = timeMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        int deciseconds = (timeMs % 1000) / 100;

        if (totalSeconds >= 60) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.format("%d.%d", seconds, deciseconds);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    // Callbacks
    public void setOnTimeUpdate(Consumer<Color> callback) {
        this.onTimeUpdate = callback;
    }

    public void setOnTimeExpired(Consumer<Color> callback) {
        this.onTimeExpired = callback;
    }

    public void setOnLowTime(Consumer<Color> callback) {
        this.onLowTime = callback;
    }
}