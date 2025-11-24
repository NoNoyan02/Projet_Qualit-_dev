package com.chess.entrypoints.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Barre d'évaluation affichant l'avantage d'un camp.
 */
public class EvaluationBar extends JPanel {
    private static final int BAR_WIDTH = 30;
    private static final int MAX_EVAL = 1000; // Centipawns max pour la barre

    private int evaluation; // En centipawns (100 = 1 pion)
    private JLabel evalLabel;

    public EvaluationBar() {
        this.evaluation = 0;

        setPreferredSize(new Dimension(BAR_WIDTH + 30, 0));
        setBackground(new java.awt.Color(49, 46, 43));
        setLayout(new BorderLayout());

        initializeComponents();
    }

    private void initializeComponents() {
        // Label d'évaluation
        evalLabel = new JLabel("0.0");
        evalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        evalLabel.setForeground(java.awt.Color.WHITE);
        evalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(evalLabel, BorderLayout.NORTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int height = getHeight() - 30; // Espace pour le label
        int x = 15;
        int y = 25;

        // Calcul de la proportion blanc/noir
        float ratio = Math.min(Math.max(evaluation, -MAX_EVAL), MAX_EVAL) / (float) MAX_EVAL;
        int whiteHeight = (int) ((ratio + 1) / 2 * height);
        int blackHeight = height - whiteHeight;

        // Dessiner la partie blanche (en bas)
        g2d.setColor(java.awt.Color.WHITE);
        g2d.fillRect(x, y + blackHeight, BAR_WIDTH, whiteHeight);

        // Dessiner la partie noire (en haut)
        g2d.setColor(java.awt.Color.BLACK);
        g2d.fillRect(x, y, BAR_WIDTH, blackHeight);

        // Bordure
        g2d.setColor(new java.awt.Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, BAR_WIDTH, height);

        // Ligne médiane
        g2d.setColor(new java.awt.Color(150, 150, 150));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(x, y + height/2, x + BAR_WIDTH, y + height/2);
    }

    /**
     * Met à jour l'évaluation.
     * @param centipawns évaluation en centipawns (100 = 1 pion d'avantage blanc)
     */
    public void setEvaluation(int centipawns) {
        this.evaluation = centipawns;

        // Mise à jour du label
        double pawns = centipawns / 100.0;
        if (Math.abs(centipawns) > 2000) {
            evalLabel.setText(centipawns > 0 ? "+M" : "-M");
        } else {
            evalLabel.setText(String.format("%+.1f", pawns));
        }

        // Couleur du label selon l'avantage
        if (Math.abs(pawns) < 0.3) {
            evalLabel.setForeground(java.awt.Color.WHITE);
        } else if (centipawns > 0) {
            evalLabel.setForeground(new java.awt.Color(200, 200, 255));
        } else {
            evalLabel.setForeground(new java.awt.Color(255, 200, 200));
        }

        repaint();
    }
}