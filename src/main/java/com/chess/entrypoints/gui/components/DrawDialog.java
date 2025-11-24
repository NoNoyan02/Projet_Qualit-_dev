package com.chess.entrypoints.gui.components;

import com.chess.core.usecases.DrawDetectorUseCase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialogue pour gérer les propositions et réclamations de nulle.
 */
public class DrawDialog extends JDialog {

    public enum DrawAction {
        OFFER,      // Proposer la nulle
        CLAIM,      // Réclamer la nulle
        ACCEPT,     // Accepter la proposition
        DECLINE     // Refuser la proposition
    }

    private DrawAction selectedAction;

    /**
     * Dialogue pour proposer la nulle.
     */
    public static DrawAction showOfferDrawDialog(JFrame parent) {
        DrawDialog dialog = new DrawDialog(parent,
                "Proposer la nulle",
                "Voulez-vous proposer la nulle à votre adversaire ?",
                new String[]{"Proposer", "Annuler"});

        dialog.setVisible(true);
        return dialog.selectedAction;
    }

    /**
     * Dialogue pour accepter/refuser une proposition de nulle.
     */
    public static DrawAction showDrawOfferReceivedDialog(JFrame parent, String opponentName) {
        DrawDialog dialog = new DrawDialog(parent,
                "Proposition de nulle",
                opponentName + " propose la nulle. Acceptez-vous ?",
                new String[]{"Accepter", "Refuser"});

        dialog.setVisible(true);
        return dialog.selectedAction;
    }

    /**
     * Dialogue pour réclamer la nulle (50 coups ou triple répétition).
     */
    public static DrawAction showClaimDrawDialog(JFrame parent, DrawDetectorUseCase.DrawType drawType) {
        String message = buildClaimMessage(drawType);

        DrawDialog dialog = new DrawDialog(parent,
                "Réclamer la nulle",
                message,
                new String[]{"Réclamer", "Continuer"});

        dialog.setVisible(true);
        return dialog.selectedAction;
    }

    /**
     * Notification de nulle automatique.
     */
    public static void showAutomaticDrawDialog(JFrame parent, DrawDetectorUseCase.DrawType drawType) {
        String message = buildAutomaticDrawMessage(drawType);

        JOptionPane.showMessageDialog(parent,
                message,
                "Partie nulle",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private DrawDialog(JFrame parent, String title, String message, String[] options) {
        super(parent, title, true);

        setLayout(new BorderLayout(10, 10));
        setSize(450, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeComponents(message, options);
    }

    private void initializeComponents(String message, String[] options) {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Icône
        JLabel iconLabel = new JLabel("⚖️");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(iconLabel, BorderLayout.WEST);

        // Message
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setBackground(Color.WHITE);
        messageArea.setBorder(null);
        mainPanel.add(messageArea, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        for (int i = 0; i < options.length; i++) {
            final int index = i;
            JButton button = new JButton(options[i]);
            button.setPreferredSize(new Dimension(120, 35));

            if (i == 0) {
                // Bouton principal (vert)
                button.setBackground(new java.awt.Color(129, 182, 76));
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Arial", Font.BOLD, 13));
            } else {
                // Bouton secondaire (gris)
                button.setBackground(new java.awt.Color(150, 150, 150));
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Arial", Font.PLAIN, 13));
            }

            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addActionListener(e -> {
                selectedAction = mapButtonToAction(index, options.length);
                dispose();
            });

            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private DrawAction mapButtonToAction(int buttonIndex, int buttonCount) {
        if (buttonCount == 2) {
            // Deux boutons : Proposer/Réclamer/Accepter vs Annuler/Continuer/Refuser
            if (buttonIndex == 0) {
                // Premier bouton cliqué
                String title = getTitle();
                if (title.contains("Proposer")) {
                    return DrawAction.OFFER;
                } else if (title.contains("Réclamer")) {
                    return DrawAction.CLAIM;
                } else {
                    return DrawAction.ACCEPT;
                }
            } else {
                // Deuxième bouton cliqué (annuler/refuser)
                String title = getTitle();
                if (title.contains("Proposition")) {
                    return DrawAction.DECLINE;
                }
                return null; // Annuler
            }
        }
        return null;
    }

    private static String buildClaimMessage(DrawDetectorUseCase.DrawType drawType) {
        StringBuilder sb = new StringBuilder();

        switch (drawType) {
            case FIFTY_MOVE_RULE:
                sb.append("⚠️ Règle des 50 coups\n\n");
                sb.append("Aucune capture ni mouvement de pion depuis 50 coups.\n");
                sb.append("Vous pouvez réclamer la nulle.\n\n");
                sb.append("Voulez-vous réclamer la nulle ou continuer la partie ?");
                break;

            case THREEFOLD_REPETITION:
                sb.append("⚠️ Triple répétition\n\n");
                sb.append("La même position s'est répétée 3 fois.\n");
                sb.append("Vous pouvez réclamer la nulle.\n\n");
                sb.append("Voulez-vous réclamer la nulle ou continuer la partie ?");
                break;

            default:
                sb.append("Vous pouvez réclamer la nulle.");
                break;
        }

        return sb.toString();
    }

    private static String buildAutomaticDrawMessage(DrawDetectorUseCase.DrawType drawType) {
        StringBuilder sb = new StringBuilder();
        sb.append("Partie nulle !\n\n");

        switch (drawType) {
            case STALEMATE:
                sb.append("🔒 PAT\n\n");
                sb.append("Le joueur actif n'a aucun coup légal\n");
                sb.append("sans être en échec.");
                break;

            case INSUFFICIENT_MATERIAL:
                sb.append("👑 MATÉRIEL INSUFFISANT\n\n");
                sb.append("Il est impossible de faire mat\n");
                sb.append("avec le matériel restant sur l'échiquier.");
                break;

            case DEAD_POSITION:
                sb.append("⚰️ POSITION MORTE\n\n");
                sb.append("Aucune suite de coups légaux\n");
                sb.append("ne peut mener au mat.");
                break;

            case SEVENTY_FIVE_MOVE_RULE:
                sb.append("⏱️ RÈGLE DES 75 COUPS\n\n");
                sb.append("75 coups sans capture ni mouvement de pion.\n");
                sb.append("Nulle automatique.");
                break;

            case FIVEFOLD_REPETITION:
                sb.append("🔄 QUINTUPLE RÉPÉTITION\n\n");
                sb.append("La même position s'est répétée 5 fois.\n");
                sb.append("Nulle automatique.");
                break;

            default:
                sb.append(drawType.getDescription());
                break;
        }

        return sb.toString();
    }
}