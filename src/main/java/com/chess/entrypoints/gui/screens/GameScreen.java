package com.chess.entrypoints.gui.screens;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.*;
import com.chess.core.entities.player.Player;
import com.chess.entrypoints.gui.GuiController;
import com.chess.entrypoints.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Écran principal de jeu avec plateau, horloge, historique et contrôles.
 */
public class GameScreen extends JPanel {
    private final GuiController controller;
    private final GameState gameState;
    private final GameClock gameClock;
    private final GameSettings settings;

    // Composants principaux
    private BoardPanel boardPanel;
    private MoveListPanel moveListPanel;
    private ClockPanel whiteClockPanel;
    private ClockPanel blackClockPanel;
    private EvaluationBar evaluationBar;
    private PlayerInfoPanel whitePlayerPanel;
    private PlayerInfoPanel blackPlayerPanel;

    // Boutons de contrôle
    private JButton resignButton;
    private JButton drawButton;
    private JButton settingsButton;

    // Indicateurs
    private JLabel aiThinkingLabel;
    private JLabel lowTimeAlertLabel;

    private Player whitePlayer;
    private Player blackPlayer;

    public GameScreen(GuiController controller, GameState gameState,
                      GameClock gameClock, GameSettings settings) {
        this.controller = controller;
        this.gameState = gameState;
        this.gameClock = gameClock;
        this.settings = settings;

        setLayout(new BorderLayout(10, 10));
        setBackground(new java.awt.Color(49, 46, 43));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initializeComponents();
    }

    private void initializeComponents() {
        // Panel gauche (horloge + info joueur noir)
        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        // Panel central (échiquier)
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Panel droit (historique + contrôles)
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new java.awt.Color(38, 36, 33));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Info joueur noir (en haut)
        blackPlayerPanel = new PlayerInfoPanel(Color.BLACK);
        panel.add(blackPlayerPanel);
        panel.add(Box.createVerticalStrut(10));

        // Horloge noir
        blackClockPanel = new ClockPanel(Color.BLACK, gameClock);
        panel.add(blackClockPanel);

        // Espace flexible
        panel.add(Box.createVerticalGlue());

        // Horloge blanc
        whiteClockPanel = new ClockPanel(Color.WHITE, gameClock);
        panel.add(whiteClockPanel);
        panel.add(Box.createVerticalStrut(10));

        // Info joueur blanc (en bas)
        whitePlayerPanel = new PlayerInfoPanel(Color.WHITE);
        panel.add(whitePlayerPanel);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new java.awt.Color(49, 46, 43));

        // Barre d'évaluation
        if (settings.isShowEngineEvaluation()) {
            evaluationBar = new EvaluationBar();
            panel.add(evaluationBar, BorderLayout.WEST);
        }

        // Échiquier
        boardPanel = new BoardPanel(gameState.getBoard(), settings);
        boardPanel.setOnMoveAttempt((from, to) -> controller.attemptMove(from, to));

        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(new java.awt.Color(49, 46, 43));
        boardContainer.add(boardPanel);

        panel.add(boardContainer, BorderLayout.CENTER);

        // Indicateur IA en train de réfléchir
        aiThinkingLabel = new JLabel("🤔 L'IA réfléchit...");
        aiThinkingLabel.setForeground(java.awt.Color.YELLOW);
        aiThinkingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        aiThinkingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aiThinkingLabel.setVisible(false);
        panel.add(aiThinkingLabel, BorderLayout.NORTH);

        // Alerte temps faible
        lowTimeAlertLabel = new JLabel("⏰ Attention au temps !");
        lowTimeAlertLabel.setForeground(java.awt.Color.RED);
        lowTimeAlertLabel.setFont(new Font("Arial", Font.BOLD, 16));
        lowTimeAlertLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lowTimeAlertLabel.setVisible(false);
        panel.add(lowTimeAlertLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new java.awt.Color(38, 36, 33));
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel titleLabel = new JLabel("Historique des coups");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(java.awt.Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Liste des coups
        moveListPanel = new MoveListPanel(settings);
        panel.add(moveListPanel, BorderLayout.CENTER);

        // Boutons de contrôle
        JPanel controlPanel = createControlPanel();
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Boutons principaux
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonsPanel.setOpaque(false);

        resignButton = createControlButton("Abandonner", new java.awt.Color(200, 50, 50));
        resignButton.addActionListener(e -> controller.resign());

        drawButton = createControlButton("Nulle", new java.awt.Color(100, 100, 150));
        drawButton.addActionListener(e -> controller.offerDraw());

        settingsButton = createControlButton("Paramètres", new java.awt.Color(100, 100, 100));
        settingsButton.addActionListener(e -> controller.openSettings());

        JButton menuButton = createControlButton("Menu", new java.awt.Color(80, 80, 80));
        menuButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Quitter la partie en cours ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.showMainMenu();
            }
        });

        buttonsPanel.add(resignButton);
        buttonsPanel.add(drawButton);
        buttonsPanel.add(settingsButton);
        buttonsPanel.add(menuButton);

        panel.add(buttonsPanel);

        return panel;
    }

    private JButton createControlButton(String text, java.awt.Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(java.awt.Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Appelé quand un coup est joué.
     */
    public void onMoveExecuted(Move move) {
        // Mise à jour du plateau
        boardPanel.updateBoard(gameState.getBoard());
        boardPanel.setLastMove(move);

        // Mise à jour de l'historique
        moveListPanel.addMove(move, gameState.getMoveCount());

        // Mise à jour de l'évaluation si activée
        if (evaluationBar != null && settings.isShowEngineEvaluation()) {
            updateEvaluation();
        }

        // Effacer l'alerte de temps faible
        lowTimeAlertLabel.setVisible(false);
    }

    /**
     * Met à jour l'horloge.
     */
    public void updateClock() {
        if (whiteClockPanel != null) {
            whiteClockPanel.updateTime();
        }
        if (blackClockPanel != null) {
            blackClockPanel.updateTime();
        }
    }

    /**
     * Affiche/cache l'indicateur de réflexion de l'IA.
     */
    public void showAIThinking(boolean thinking) {
        aiThinkingLabel.setVisible(thinking);
    }

    /**
     * Affiche l'alerte de temps faible.
     */
    public void showLowTimeAlert(Color color) {
        lowTimeAlertLabel.setVisible(true);

        // Clignotement
        Timer timer = new Timer(500, null);
        final int[] count = {0};
        timer.addActionListener(e -> {
            lowTimeAlertLabel.setVisible(!lowTimeAlertLabel.isVisible());
            count[0]++;
            if (count[0] >= 6) { // 3 clignotements
                timer.stop();
                lowTimeAlertLabel.setVisible(false);
            }
        });
        timer.start();
    }

    /**
     * Met à jour l'évaluation de la position.
     */
    private void updateEvaluation() {
        // TODO: Appeler le moteur en arrière-plan pour évaluer
        // Pour l'instant, évaluation simpliste basée sur le matériel
        if (evaluationBar != null) {
            int materialBalance = calculateMaterialBalance();
            evaluationBar.setEvaluation(materialBalance);
        }
    }

    private int calculateMaterialBalance() {
        int balance = 0;
        Board board = gameState.getBoard();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                var piece = board.getPieceAt(new Position(row, col));
                if (piece != null) {
                    int value = switch (piece.getType()) {
                        case PAWN -> 100;
                        case KNIGHT, BISHOP -> 300;
                        case ROOK -> 500;
                        case QUEEN -> 900;
                        case KING -> 0;
                    };

                    balance += piece.getColor() == Color.WHITE ? value : -value;
                }
            }
        }

        return balance;
    }

    /**
     * Appelé quand la partie se termine.
     */
    public void gameEnded() {
        resignButton.setEnabled(false);
        drawButton.setEnabled(false);
    }

    /**
     * Applique les nouveaux paramètres.
     */
    public void applySettings(GameSettings settings) {
        boardPanel.updateBoard(gameState.getBoard());
        moveListPanel.updateSettings(settings);

        // Réafficher la barre d'évaluation si nécessaire
        if (settings.isShowEngineEvaluation() && evaluationBar == null) {
            evaluationBar = new EvaluationBar();
            // Réorganiser le layout
        }
    }

    // Setters
    public void setWhitePlayer(Player player) {
        this.whitePlayer = player;
        if (whitePlayerPanel != null) {
            whitePlayerPanel.setPlayer(player);
        }
    }

    public void setBlackPlayer(Player player) {
        this.blackPlayer = player;
        if (blackPlayerPanel != null) {
            blackPlayerPanel.setPlayer(player);
        }
    }
}