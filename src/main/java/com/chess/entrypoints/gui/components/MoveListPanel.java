package com.chess.entrypoints.gui.components;

import com.chess.core.entities.game.GameSettings;
import com.chess.core.entities.game.Move;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panneau affichant l'historique des coups avec détection d'ouverture.
 */
public class MoveListPanel extends JPanel {
    private GameSettings settings;
    private JTextArea moveListArea;
    private JLabel openingLabel;
    private List<Move> moves;

    public MoveListPanel(GameSettings settings) {
        this.settings = settings;
        this.moves = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(new java.awt.Color(38, 36, 33));

        initializeComponents();
    }

    private void initializeComponents() {
        // Panneau supérieur avec l'ouverture détectée
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new java.awt.Color(28, 26, 23));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel openingTitleLabel = new JLabel("Ouverture:");
        openingTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        openingTitleLabel.setForeground(new java.awt.Color(150, 150, 150));

        openingLabel = new JLabel("Position de départ");
        openingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        openingLabel.setForeground(java.awt.Color.WHITE);

        topPanel.add(openingTitleLabel, BorderLayout.NORTH);
        topPanel.add(openingLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Zone de texte pour les coups
        moveListArea = new JTextArea();
        moveListArea.setEditable(false);
        moveListArea.setBackground(new java.awt.Color(28, 26, 23));
        moveListArea.setForeground(java.awt.Color.WHITE);
        moveListArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        moveListArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(moveListArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMove(Move move, int moveNumber) {
        moves.add(move);
        updateMoveList();
        detectOpening();
    }

    private void updateMoveList() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);

            // Numéro de coup (tous les deux coups)
            if (i % 2 == 0) {
                sb.append(String.format("%d. ", (i / 2) + 1));
            }

            // Notation du coup
            String notation = move.toAlgebraic();

            // Ajouter des symboles si activé
            if (settings.isShowMoveClassificationIcons()) {
                notation = addMoveClassification(notation, move);
            }

            sb.append(notation);

            // Timestamp si activé
            if (settings.isShowTimestamp()) {
                sb.append(" [0:00]"); // TODO: Ajouter le temps réel
            }

            sb.append("   ");

            // Nouvelle ligne après le coup noir
            if (i % 2 == 1) {
                sb.append("\n");
            }
        }

        moveListArea.setText(sb.toString());

        // Scroll automatique vers le bas
        moveListArea.setCaretPosition(moveListArea.getDocument().getLength());
    }

    private String addMoveClassification(String notation, Move move) {
        // TODO: Analyser le coup avec le moteur pour classifier
        // Pour l'instant, classification basique

        if (move.isCapture()) {
            return notation; // Les captures sont déjà marquées
        }

        // Exemples de classification (à améliorer avec analyse)
        // !! = Coup brillant
        // ! = Bon coup
        // ? = Coup douteux
        // ?? = Gaffe

        return notation;
    }

    private void detectOpening() {
        if (moves.size() < 3) {
            openingLabel.setText("Position de départ");
            return;
        }

        // Détection d'ouvertures courantes basée sur les premiers coups
        String opening = detectOpeningFromMoves();
        openingLabel.setText(opening);
    }

    private String detectOpeningFromMoves() {
        if (moves.isEmpty()) return "Position de départ";

        // Convertir les coups en notation simple
        List<String> moveStrings = new ArrayList<>();
        for (Move move : moves) {
            moveStrings.add(move.toAlgebraic());
        }

        // Détection basique des ouvertures principales
        if (moves.size() >= 2) {
            String firstMove = moveStrings.get(0);
            String secondMove = moveStrings.get(1);

            // 1.e4
            if (firstMove.equals("e2e4")) {
                if (secondMove.equals("e7e5")) {
                    if (moves.size() >= 4 && moveStrings.get(2).equals("g1f3")) {
                        if (moveStrings.get(3).equals("b8c6")) {
                            return "Partie Italienne / Espagnole";
                        }
                    }
                    return "Ouverture du pion roi";
                } else if (secondMove.equals("c7c5")) {
                    return "Défense Sicilienne";
                } else if (secondMove.equals("e7e6")) {
                    return "Défense Française";
                } else if (secondMove.equals("c7c6")) {
                    return "Défense Caro-Kann";
                }
            }
            // 1.d4
            else if (firstMove.equals("d2d4")) {
                if (secondMove.equals("d7d5")) {
                    return "Gambit Dame";
                } else if (secondMove.equals("g8f6")) {
                    return "Défense Indienne";
                }
            }
            // 1.c4
            else if (firstMove.equals("c2c4")) {
                return "Ouverture Anglaise";
            }
            // 1.Nf3
            else if (firstMove.equals("g1f3")) {
                return "Ouverture Réti";
            }
        }

        return "Ouverture personnalisée";
    }

    public void updateSettings(GameSettings settings) {
        this.settings = settings;
        updateMoveList();
    }

    public void clear() {
        moves.clear();
        moveListArea.setText("");
        openingLabel.setText("Position de départ");
    }
}