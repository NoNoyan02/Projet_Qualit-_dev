package com.chess.entrypoints.gui.components;

import com.chess.core.entities.Color;
import com.chess.core.entities.game.GameClock;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Composant affichant l'horloge d'un joueur.
 */
public class ClockPanel extends JPanel {
    private final Color playerColor;
    private final GameClock gameClock;
    private JLabel timeLabel;
    private boolean isActive;

    public ClockPanel(Color playerColor, GameClock gameClock) {
        this.playerColor = playerColor;
        this.gameClock = gameClock;
        this.isActive = false;

        setLayout(new BorderLayout());
        setBackground(new java.awt.Color(28, 26, 23));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(60, 60, 60), 2),
                new EmptyBorder(15, 20, 15, 20)
        ));
        setPreferredSize(new Dimension(230, 80));

        initializeComponents();
        updateTime();
    }

    private void initializeComponents() {
        timeLabel = new JLabel("--:--");
        timeLabel.setFont(new Font("Courier New", Font.BOLD, 36));
        timeLabel.setForeground(java.awt.Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(timeLabel, BorderLayout.CENTER);
    }

    public void updateTime() {
        if (gameClock == null) {
            timeLabel.setText("∞");
            return;
        }

        String time = gameClock.getFormattedTime(playerColor);
        timeLabel.setText(time);

        // Mise à jour du style en fonction de l'activité
        isActive = gameClock.isRunning() && gameClock.getActiveColor() == playerColor;

        if (isActive) {
            setBackground(new java.awt.Color(129, 182, 76)); // Vert actif
            timeLabel.setForeground(java.awt.Color.BLACK);
        } else {
            setBackground(new java.awt.Color(28, 26, 23));
            timeLabel.setForeground(java.awt.Color.WHITE);
        }

        // Alerte temps faible (< 10 secondes)
        int remainingTime = gameClock.getRemainingTime(playerColor);
        if (remainingTime < 10000 && remainingTime > 0) {
            timeLabel.setForeground(java.awt.Color.RED);
        }
    }
}