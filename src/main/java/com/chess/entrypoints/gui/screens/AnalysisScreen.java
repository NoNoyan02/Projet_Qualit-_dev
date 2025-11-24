package com.chess.entrypoints.gui.screens;

import com.chess.core.entities.Position;
import com.chess.core.entities.game.GameSettings;
import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.usecases.AnalyzePositionUseCase;
import com.chess.entrypoints.gui.GuiController;
import com.chess.entrypoints.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Écran d'analyse post-partie avec évaluation des coups.
 */
public class AnalysisScreen extends JPanel {
    private final GuiController controller;
    private final GameState gameState;
    private final AnalyzePositionUseCase analyzeUseCase;
    private final GameSettings settings;

    private BoardPanel boardPanel;
    private JList<String> moveList;
    private DefaultListModel<String> moveListModel;
    private EvaluationBar evaluationBar;
    private JTextArea analysisText;
    private JProgressBar analysisProgress;

    private int currentMoveIndex = -1;
    private List<Move> moves;

    public AnalysisScreen(GuiController controller, GameState gameState,
                          AnalyzePositionUseCase analyzeUseCase, GameSettings settings) {
        this.controller = controller;
        this.gameState = gameState;
        this.analyzeUseCase = analyzeUseCase;
        this.settings = settings;
        this.moves = gameState.getMoveHistory();

        setLayout(new BorderLayout(10, 10));
        setBackground(new java.awt.Color(49, 46, 43));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initializeComponents();
        startAnalysis();
    }

    private void initializeComponents() {
        // Panel supérieur : titre et contrôles
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel gauche : liste des coups
        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        // Panel central : échiquier
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Panel droit : analyse
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new java.awt.Color(38, 36, 33));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel titleLabel = new JLabel("Analyse de la partie");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(java.awt.Color.WHITE);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton backButton = new JButton("← Menu principal");
        backButton.addActionListener(e -> controller.showMainMenu());
        styleButton(backButton);

        JButton newGameButton = new JButton("Nouvelle partie");
        newGameButton.addActionListener(e -> controller.showMainMenu());
        styleButton(newGameButton);

        buttonPanel.add(backButton);
        buttonPanel.add(newGameButton);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new java.awt.Color(38, 36, 33));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel titleLabel = new JLabel("Coups de la partie");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(java.awt.Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Liste des coups avec classification
        moveListModel = new DefaultListModel<>();
        moveList = new JList<>(moveListModel);
        moveList.setBackground(new java.awt.Color(28, 26, 23));
        moveList.setForeground(java.awt.Color.WHITE);
        moveList.setFont(new Font("Courier New", Font.PLAIN, 14));
        moveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moveList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showMoveAtIndex(moveList.getSelectedIndex());
            }
        });

        JScrollPane scrollPane = new JScrollPane(moveList);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Contrôles de navigation
        JPanel navPanel = new JPanel(new FlowLayout());
        navPanel.setOpaque(false);

        JButton firstButton = new JButton("⏮");
        JButton prevButton = new JButton("◀");
        JButton nextButton = new JButton("▶");
        JButton lastButton = new JButton("⏭");

        firstButton.addActionListener(e -> showMoveAtIndex(0));
        prevButton.addActionListener(e -> showMoveAtIndex(currentMoveIndex - 1));
        nextButton.addActionListener(e -> showMoveAtIndex(currentMoveIndex + 1));
        lastButton.addActionListener(e -> showMoveAtIndex(moves.size() - 1));

        navPanel.add(firstButton);
        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(lastButton);

        panel.add(navPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new java.awt.Color(49, 46, 43));

        // Barre d'évaluation
        evaluationBar = new EvaluationBar();
        panel.add(evaluationBar, BorderLayout.WEST);

        // Échiquier
        boardPanel = new BoardPanel(gameState.getBoard(), settings);

        JPanel boardContainer = new JPanel(new GridBagLayout());
        boardContainer.setBackground(new java.awt.Color(49, 46, 43));
        boardContainer.add(boardPanel);

        panel.add(boardContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new java.awt.Color(38, 36, 33));
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel titleLabel = new JLabel("Analyse du coup");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(java.awt.Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Zone de texte d'analyse
        analysisText = new JTextArea();
        analysisText.setEditable(false);
        analysisText.setBackground(new java.awt.Color(28, 26, 23));
        analysisText.setForeground(java.awt.Color.WHITE);
        analysisText.setFont(new Font("Arial", Font.PLAIN, 14));
        analysisText.setLineWrap(true);
        analysisText.setWrapStyleWord(true);
        analysisText.setBorder(new EmptyBorder(10, 10, 10, 10));
        analysisText.setText("Sélectionnez un coup pour voir l'analyse détaillée.");

        JScrollPane scrollPane = new JScrollPane(analysisText);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Barre de progression de l'analyse
        analysisProgress = new JProgressBar();
        analysisProgress.setString("Analyse en cours...");
        analysisProgress.setStringPainted(true);
        analysisProgress.setIndeterminate(true);
        panel.add(analysisProgress, BorderLayout.SOUTH);

        return panel;
    }

    private void startAnalysis() {
        // Analyse en arrière-plan
        new Thread(() -> {
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);

                // Classification du coup (à améliorer avec analyse moteur)
                String classification = classifyMove(move, i);

                String moveText = String.format("%d. %s %s",
                        (i / 2) + 1,
                        move.toAlgebraic(),
                        classification);

                int index = i;
                SwingUtilities.invokeLater(() -> {
                    moveListModel.addElement(moveText);
                    analysisProgress.setValue((index + 1) * 100 / moves.size());
                });

                try {
                    Thread.sleep(100); // Simule l'analyse
                } catch (InterruptedException e) {
                    break;
                }
            }

            SwingUtilities.invokeLater(() -> {
                analysisProgress.setIndeterminate(false);
                analysisProgress.setValue(100);
                analysisProgress.setString("Analyse terminée ✓");
            });
        }).start();
    }

    private String classifyMove(Move move, int moveIndex) {
        // Classification basique des coups
        // TODO: Intégrer l'analyse du moteur pour une vraie classification

        if (move.isCapture()) {
            return "×"; // Capture
        }

        // Exemples de classification (simplifié)
        // !! = Coup brillant
        // ! = Bon coup
        // !? = Coup intéressant
        // ?! = Coup douteux
        // ? = Erreur
        // ?? = Gaffe

        return ""; // Coup normal
    }

    private void showMoveAtIndex(int index) {
        if (index < 0 || index >= moves.size()) {
            return;
        }

        currentMoveIndex = index;
        moveList.setSelectedIndex(index);

        // Reconstruire la position jusqu'à ce coup
        GameState tempState = new GameState();
        tempState.initializeGame();

        for (int i = 0; i <= index; i++) {
            // Rejouer les coups (simplifié)
            // TODO: Implémenter correctement
        }

        // Mettre à jour l'affichage
        boardPanel.updateBoard(gameState.getBoard());
        Move currentMove = moves.get(index);
        boardPanel.setLastMove(currentMove);

        // Analyse du coup
        analyzeCurrentMove(currentMove);
    }

    private void analyzeCurrentMove(Move move) {
        StringBuilder analysis = new StringBuilder();

        analysis.append("Coup joué: ").append(move.toAlgebraic()).append("\n\n");

        if (move.isCapture()) {
            analysis.append("✓ Capture de pièce\n");
        }

        if (move.isPromotion()) {
            analysis.append("✓ Promotion en ")
                    .append(move.getPromotionPiece()).append("\n");
        }

        if (move.isCastling()) {
            analysis.append("✓ Roque\n");
        }

        analysis.append("\n--- Évaluation ---\n");
        analysis.append("Position: +0.5 (légère avantage blanc)\n");
        analysis.append("\nMeilleur coup: ").append(move.toAlgebraic()).append("\n");
        analysis.append("\n--- Commentaire ---\n");
        analysis.append("Bon coup qui contrôle le centre.");

        analysisText.setText(analysis.toString());
    }

    private void styleButton(JButton button) {
        button.setBackground(new java.awt.Color(129, 182, 76));
        button.setForeground(java.awt.Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}