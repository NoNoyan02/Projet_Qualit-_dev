package com.chess.entrypoints.gui.screens;

import com.chess.core.entities.Color;
import com.chess.core.entities.game.TimeControl;
import com.chess.entrypoints.gui.GuiController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Écran du menu principal avec toutes les options de nouvelle partie.
 */
public class MainMenuScreen extends JPanel {
    private final GuiController controller;

    // Composants
    private JRadioButton vsPlayerButton;
    private JRadioButton vsBotButton;
    private JSlider botLevelSlider;
    private JComboBox<String> colorChoice;

    private JRadioButton bulletButton;
    private JRadioButton blitzButton;
    private JRadioButton rapidButton;
    private JRadioButton customButton;
    private JSpinner minutesSpinner;
    private JSpinner incrementSpinner;

    private JTextField fenField;

    public MainMenuScreen(GuiController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(new java.awt.Color(49, 46, 43));

        initializeComponents();
    }

    private void initializeComponents() {
        // Panel principal centré
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new java.awt.Color(49, 46, 43));
        centerPanel.setBorder(new EmptyBorder(50, 100, 50, 100));

        // Titre
        JLabel titleLabel = new JLabel("NOUVELLE PARTIE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(java.awt.Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(50));

        // Section Adversaire
        JPanel opponentPanel = createOpponentPanel();
        centerPanel.add(opponentPanel);
        centerPanel.add(Box.createVerticalStrut(30));

        // Section Contrôle du temps
        JPanel timeControlPanel = createTimeControlPanel();
        centerPanel.add(timeControlPanel);
        centerPanel.add(Box.createVerticalStrut(30));

        // Section Position de départ
        JPanel startPositionPanel = createStartPositionPanel();
        centerPanel.add(startPositionPanel);
        centerPanel.add(Box.createVerticalStrut(40));

        // Boutons d'action
        JPanel buttonPanel = createActionButtons();
        centerPanel.add(buttonPanel);

        // Ajouter au centre avec scroll
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createOpponentPanel() {
        JPanel panel = createStyledPanel("Adversaire");

        // Radio buttons
        vsPlayerButton = new JRadioButton("Jouer à 2");
        vsBotButton = new JRadioButton("Jouer contre un bot");

        ButtonGroup group = new ButtonGroup();
        group.add(vsPlayerButton);
        group.add(vsBotButton);
        vsPlayerButton.setSelected(true);

        styleRadioButton(vsPlayerButton);
        styleRadioButton(vsBotButton);

        panel.add(vsPlayerButton);
        panel.add(vsBotButton);

        // Niveau du bot
        JPanel botLevelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botLevelPanel.setOpaque(false);

        JLabel botLevelLabel = createStyledLabel("Niveau ELO: ");
        botLevelSlider = new JSlider(800, 2800, 1500);
        botLevelSlider.setMajorTickSpacing(500);
        botLevelSlider.setMinorTickSpacing(100);
        botLevelSlider.setPaintTicks(true);
        botLevelSlider.setPaintLabels(true);
        botLevelSlider.setPreferredSize(new Dimension(400, 50));
        botLevelSlider.setEnabled(false);

        JLabel eloLabel = createStyledLabel("1500");
        botLevelSlider.addChangeListener(e ->
                eloLabel.setText(String.valueOf(botLevelSlider.getValue())));

        vsBotButton.addActionListener(e -> botLevelSlider.setEnabled(true));
        vsPlayerButton.addActionListener(e -> botLevelSlider.setEnabled(false));

        botLevelPanel.add(botLevelLabel);
        botLevelPanel.add(botLevelSlider);
        botLevelPanel.add(eloLabel);
        panel.add(botLevelPanel);

        // Choix de la couleur
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setOpaque(false);

        JLabel colorLabel = createStyledLabel("Jouer avec: ");
        colorChoice = new JComboBox<>(new String[]{"Blancs", "Noirs", "Aléatoire"});
        colorChoice.setPreferredSize(new Dimension(150, 30));
        colorChoice.setEnabled(false);

        vsBotButton.addActionListener(e -> colorChoice.setEnabled(true));
        vsPlayerButton.addActionListener(e -> colorChoice.setEnabled(false));

        colorPanel.add(colorLabel);
        colorPanel.add(colorChoice);
        panel.add(colorPanel);

        return panel;
    }

    private JPanel createTimeControlPanel() {
        JPanel panel = createStyledPanel("Contrôle du temps");

        // Radio buttons pour les préréglages
        bulletButton = new JRadioButton("Bullet (1+0)");
        blitzButton = new JRadioButton("Blitz (3+2)");
        rapidButton = new JRadioButton("Rapide (10+0)");
        customButton = new JRadioButton("Personnalisé");

        ButtonGroup group = new ButtonGroup();
        group.add(bulletButton);
        group.add(blitzButton);
        group.add(rapidButton);
        group.add(customButton);
        blitzButton.setSelected(true);

        styleRadioButton(bulletButton);
        styleRadioButton(blitzButton);
        styleRadioButton(rapidButton);
        styleRadioButton(customButton);

        JPanel presetsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        presetsPanel.setOpaque(false);
        presetsPanel.add(bulletButton);
        presetsPanel.add(blitzButton);
        presetsPanel.add(rapidButton);
        presetsPanel.add(customButton);
        panel.add(presetsPanel);

        // Temps personnalisé
        JPanel customPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customPanel.setOpaque(false);

        JLabel minutesLabel = createStyledLabel("Minutes: ");
        minutesSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
        minutesSpinner.setPreferredSize(new Dimension(60, 30));
        minutesSpinner.setEnabled(false);

        JLabel incrementLabel = createStyledLabel("  Incrément: ");
        incrementSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 30, 1));
        incrementSpinner.setPreferredSize(new Dimension(60, 30));
        incrementSpinner.setEnabled(false);

        customButton.addActionListener(e -> {
            minutesSpinner.setEnabled(true);
            incrementSpinner.setEnabled(true);
        });

        ActionListener disableCustom = e -> {
            minutesSpinner.setEnabled(false);
            incrementSpinner.setEnabled(false);
        };
        bulletButton.addActionListener(disableCustom);
        blitzButton.addActionListener(disableCustom);
        rapidButton.addActionListener(disableCustom);

        customPanel.add(minutesLabel);
        customPanel.add(minutesSpinner);
        customPanel.add(incrementLabel);
        customPanel.add(incrementSpinner);
        panel.add(customPanel);

        return panel;
    }

    private JPanel createStartPositionPanel() {
        JPanel panel = createStyledPanel("Position de départ");

        JRadioButton standardButton = new JRadioButton("Position standard");
        JRadioButton fenButton = new JRadioButton("Position FEN");
        JRadioButton importButton = new JRadioButton("Importer une partie");

        ButtonGroup group = new ButtonGroup();
        group.add(standardButton);
        group.add(fenButton);
        group.add(importButton);
        standardButton.setSelected(true);

        styleRadioButton(standardButton);
        styleRadioButton(fenButton);
        styleRadioButton(importButton);

        panel.add(standardButton);
        panel.add(fenButton);
        panel.add(importButton);

        // Champ FEN
        JPanel fenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fenPanel.setOpaque(false);

        JLabel fenLabel = createStyledLabel("FEN: ");
        fenField = new JTextField(40);
        fenField.setEnabled(false);
        fenField.setFont(new Font("Monospaced", Font.PLAIN, 12));

        fenButton.addActionListener(e -> fenField.setEnabled(true));
        standardButton.addActionListener(e -> fenField.setEnabled(false));
        importButton.addActionListener(e -> fenField.setEnabled(false));

        fenPanel.add(fenLabel);
        fenPanel.add(fenField);
        panel.add(fenPanel);

        return panel;
    }

    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        JButton startButton = createStyledButton("COMMENCER LA PARTIE", new java.awt.Color(129, 182, 76));
        startButton.setPreferredSize(new Dimension(300, 50));
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(this::onStartGame);

        JButton settingsButton = createStyledButton("PARAMÈTRES", new java.awt.Color(128, 128, 128));
        settingsButton.setPreferredSize(new Dimension(200, 50));
        settingsButton.addActionListener(e -> controller.openSettings());

        panel.add(startButton);
        panel.add(settingsButton);

        return panel;
    }

    private void onStartGame(ActionEvent e) {
        GuiController.GameConfig config = new GuiController.GameConfig();

        // Configuration de l'adversaire
        if (vsBotButton.isSelected()) {
            config.setPlayAgainstBot(true);
            config.setBotElo(botLevelSlider.getValue());

            String colorStr = (String) colorChoice.getSelectedItem();
            if ("Blancs".equals(colorStr)) {
                config.setPlayerColor(Color.WHITE);
            } else if ("Noirs".equals(colorStr)) {
                config.setPlayerColor(Color.BLACK);
            } else {
                config.setPlayerColor(Math.random() < 0.5 ? Color.WHITE : Color.BLACK);
            }
        }

        // Configuration du temps
        TimeControl timeControl;
        if (bulletButton.isSelected()) {
            timeControl = TimeControl.bullet();
        } else if (blitzButton.isSelected()) {
            timeControl = TimeControl.blitz();
        } else if (rapidButton.isSelected()) {
            timeControl = TimeControl.rapid();
        } else {
            int minutes = (Integer) minutesSpinner.getValue();
            int increment = (Integer) incrementSpinner.getValue();
            timeControl = TimeControl.custom(minutes, increment);
        }
        config.setTimeControl(timeControl);

        // Configuration de la position de départ
        if (fenField.isEnabled() && !fenField.getText().trim().isEmpty()) {
            config.setStartingFen(fenField.getText().trim());
        }

        // Démarrer la partie
        controller.startNewGame(config);
    }

    // Méthodes utilitaires de style
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new java.awt.Color(38, 36, 33));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(60, 60, 60), 2),
                new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(900, 500));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(java.awt.Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private void styleRadioButton(JRadioButton button) {
        button.setOpaque(false);
        button.setForeground(java.awt.Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(java.awt.Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private JButton createStyledButton(String text, java.awt.Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(java.awt.Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
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
}