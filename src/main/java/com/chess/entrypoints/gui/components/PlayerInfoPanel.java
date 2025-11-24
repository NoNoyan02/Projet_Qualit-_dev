package com.chess.entrypoints.gui.components;

import com.chess.core.entities.Color;
import com.chess.core.entities.player.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panneau affichant les informations d'un joueur.
 */
public class PlayerInfoPanel extends JPanel {
    private final Color playerColor;
    private JLabel nameLabel;
    private JLabel typeLabel;
    private JLabel capturedPiecesLabel;

    private Player player;

    public PlayerInfoPanel(Color playerColor) {
        this.playerColor = playerColor;

        setLayout(new BorderLayout(5, 5));
        setBackground(new java.awt.Color(38, 36, 33));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(230, 100));

        initializeComponents();
    }

    private void initializeComponents() {
        // Panneau principal
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Nom du joueur
        nameLabel = new JLabel("Joueur");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(java.awt.Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Type (Humain / IA)
        typeLabel = new JLabel("Humain");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        typeLabel.setForeground(new java.awt.Color(150, 150, 150));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Pièces capturées
        capturedPiecesLabel = new JLabel("");
        capturedPiecesLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
        capturedPiecesLabel.setForeground(java.awt.Color.WHITE);
        capturedPiecesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(typeLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(capturedPiecesLabel);

        add(infoPanel, BorderLayout.CENTER);

        // Indicateur de couleur
        JPanel colorIndicator = new JPanel();
        colorIndicator.setPreferredSize(new Dimension(10, 0));
        colorIndicator.setBackground(playerColor == Color.WHITE ?
                java.awt.Color.WHITE : java.awt.Color.BLACK);
        add(colorIndicator, BorderLayout.WEST);
    }

    public void setPlayer(Player player) {
        this.player = player;

        if (player != null) {
            nameLabel.setText(player.getName());
            typeLabel.setText(player.isAI() ? "Ordinateur" : "Humain");
        }
    }

    /**
     * Met à jour les pièces capturées affichées.
     */
    public void updateCapturedPieces(String pieces) {
        capturedPiecesLabel.setText(pieces);
    }
}