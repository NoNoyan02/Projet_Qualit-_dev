package com.chess.entrypoints.gui;

import com.chess.core.entities.game.GameSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Dialogue de configuration complet avec tous les paramètres.
 */
public class SettingsDialog extends JDialog {
    private final GameSettings settings;

    // Composants de paramètres
    private JComboBox<String> coordinateDisplayCombo;
    private JCheckBox highlightMovesCheck;
    private JCheckBox showLegalMovesCheck;
    private JComboBox<String> moveModeCombo;
    private JCheckBox enableAnimationsCheck;
    private JComboBox<String> animationSpeedCombo;
    private JCheckBox celebrateVictoryCheck;
    private JComboBox<String> celebrationStyleCombo;
    private JCheckBox soundEnabledCheck;
    private JComboBox<String> soundThemeCombo;
    private JCheckBox enablePremovesCheck;
    private JCheckBox autoPromoteQueenCheck;
    private JCheckBox confirmResignDrawCheck;
    private JCheckBox lowTimeAlertCheck;
    private JCheckBox distractionFreeModeCheck;
    private JCheckBox whiteAlwaysBottomCheck;
    private JCheckBox showEngineEvalCheck;
    private JCheckBox showCoachCommentsCheck;
    private JCheckBox showTimestampCheck;
    private JCheckBox showMoveIconsCheck;
    private JComboBox<String> boardThemeCombo;
    private JComboBox<String> pieceSetCombo;

    public SettingsDialog(JFrame parent, GameSettings settings) {
        super(parent, "Paramètres", true);
        this.settings = settings;

        setSize(800, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initializeComponents();
        loadSettings();
    }

    private void initializeComponents() {
        // Panel principal avec scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(java.awt.Color.WHITE);

        // Sections de paramètres
        mainPanel.add(createDisplaySection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createMovementSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createAnimationSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createSoundSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createGameplaySection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createAnalysisSection());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createThemeSection());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Sauvegarder");
        saveButton.addActionListener(e -> {
            saveSettings();
            dispose();
        });

        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createDisplaySection() {
        JPanel panel = createSection("Affichage");

        coordinateDisplayCombo = new JComboBox<>(new String[]{
                "Désactivé", "À l'intérieur", "À l'extérieur"
        });
        addRow(panel, "Afficher les coordonnées:", coordinateDisplayCombo);

        highlightMovesCheck = new JCheckBox("Surligner les coups");
        panel.add(highlightMovesCheck);

        showLegalMovesCheck = new JCheckBox("Montrer les coups légaux");
        panel.add(showLegalMovesCheck);

        return panel;
    }

    private JPanel createMovementSection() {
        JPanel panel = createSection("Déplacement des pièces");

        moveModeCombo = new JComboBox<>(new String[]{
                "Faire glisser", "Cliquer sur les cases", "Les deux"
        });
        addRow(panel, "Mode de déplacement:", moveModeCombo);

        return panel;
    }

    private JPanel createAnimationSection() {
        JPanel panel = createSection("Animations");

        enableAnimationsCheck = new JCheckBox("Activer les animations");
        panel.add(enableAnimationsCheck);

        animationSpeedCombo = new JComboBox<>(new String[]{
                "Lent", "Moyen", "Rapide", "Instantané"
        });
        addRow(panel, "Vitesse d'animation:", animationSpeedCombo);

        celebrateVictoryCheck = new JCheckBox("Célébrer la victoire");
        panel.add(celebrateVictoryCheck);

        celebrationStyleCombo = new JComboBox<>(new String[]{
                "Confettis", "Feux d'artifice", "Aucun"
        });
        addRow(panel, "Style de célébration:", celebrationStyleCombo);

        return panel;
    }

    private JPanel createSoundSection() {
        JPanel panel = createSection("Sons");

        soundEnabledCheck = new JCheckBox("Activer les sons");
        panel.add(soundEnabledCheck);

        soundThemeCombo = new JComboBox<>(new String[]{
                "Mode de jeu", "Classique", "Moderne", "Silencieux"
        });
        addRow(panel, "Thème sonore:", soundThemeCombo);

        return panel;
    }

    private JPanel createGameplaySection() {
        JPanel panel = createSection("Mode de jeu");

        enablePremovesCheck = new JCheckBox("Activer les coups anticipés");
        panel.add(enablePremovesCheck);

        autoPromoteQueenCheck = new JCheckBox("Toujours promouvoir en dame");
        panel.add(autoPromoteQueenCheck);

        confirmResignDrawCheck = new JCheckBox("Confirmer l'abandon/la nulle");
        panel.add(confirmResignDrawCheck);

        lowTimeAlertCheck = new JCheckBox("Alerte de manque de temps");
        panel.add(lowTimeAlertCheck);

        distractionFreeModeCheck = new JCheckBox("Mode sans distraction");
        panel.add(distractionFreeModeCheck);

        whiteAlwaysBottomCheck = new JCheckBox("Les blancs toujours en bas");
        panel.add(whiteAlwaysBottomCheck);

        return panel;
    }

    private JPanel createAnalysisSection() {
        JPanel panel = createSection("Analyse");

        showEngineEvalCheck = new JCheckBox("Évaluation du moteur d'analyse");
        panel.add(showEngineEvalCheck);

        showCoachCommentsCheck = new JCheckBox("Afficher les commentaires après la partie");
        panel.add(showCoachCommentsCheck);

        showTimestampCheck = new JCheckBox("Afficher l'horodatage");
        panel.add(showTimestampCheck);

        showMoveIconsCheck = new JCheckBox("Afficher les icônes de classification des coups");
        panel.add(showMoveIconsCheck);

        return panel;
    }

    private JPanel createThemeSection() {
        JPanel panel = createSection("Thèmes");

        boardThemeCombo = new JComboBox<>(new String[]{
                "Vert", "Marron", "Bleu", "Gris", "Bois", "Marbre"
        });
        addRow(panel, "Thème de l'échiquier:", boardThemeCombo);

        pieceSetCombo = new JComboBox<>(new String[]{
                "Neo", "Classique", "Moderne", "Alpha", "Staunty"
        });
        addRow(panel, "Style des pièces:", pieceSetCombo);

        return panel;
    }

    private JPanel createSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(java.awt.Color.GRAY),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        panel.setBackground(java.awt.Color.WHITE);
        return panel;
    }

    private void addRow(JPanel panel, String label, JComponent component) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        row.add(new JLabel(label));
        row.add(component);
        panel.add(row);
    }

    private void loadSettings() {
        // Chargement des paramètres actuels
        coordinateDisplayCombo.setSelectedIndex(settings.getCoordinateDisplay().ordinal());
        highlightMovesCheck.setSelected(settings.isHighlightMoves());
        showLegalMovesCheck.setSelected(settings.isShowLegalMoves());
        moveModeCombo.setSelectedIndex(settings.getMoveMode().ordinal());
        enableAnimationsCheck.setSelected(settings.isEnableAnimations());
        animationSpeedCombo.setSelectedIndex(settings.getAnimationSpeed().ordinal());
        celebrateVictoryCheck.setSelected(settings.isCelebrateVictory());
        celebrationStyleCombo.setSelectedIndex(settings.getCelebrationStyle().ordinal());
        soundEnabledCheck.setSelected(settings.isSoundEnabled());
        soundThemeCombo.setSelectedIndex(settings.getSoundTheme().ordinal());
        enablePremovesCheck.setSelected(settings.isEnablePremoves());
        autoPromoteQueenCheck.setSelected(settings.isAutoPromoteToQueen());
        confirmResignDrawCheck.setSelected(settings.isConfirmResignDraw());
        lowTimeAlertCheck.setSelected(settings.isLowTimeAlert());
        distractionFreeModeCheck.setSelected(settings.isDistractionFreeMode());
        whiteAlwaysBottomCheck.setSelected(settings.isWhiteAlwaysBottom());
        showEngineEvalCheck.setSelected(settings.isShowEngineEvaluation());
        showCoachCommentsCheck.setSelected(settings.isShowCoachComments());
        showTimestampCheck.setSelected(settings.isShowTimestamp());
        showMoveIconsCheck.setSelected(settings.isShowMoveClassificationIcons());
        boardThemeCombo.setSelectedIndex(settings.getBoardTheme().ordinal());
        pieceSetCombo.setSelectedIndex(settings.getPieceSet().ordinal());
    }

    private void saveSettings() {
        // Sauvegarde des paramètres
        settings.setCoordinateDisplay(GameSettings.CoordinateDisplay.values()[coordinateDisplayCombo.getSelectedIndex()]);
        settings.setHighlightMoves(highlightMovesCheck.isSelected());
        settings.setShowLegalMoves(showLegalMovesCheck.isSelected());
        settings.setMoveMode(GameSettings.MoveMode.values()[moveModeCombo.getSelectedIndex()]);
        settings.setEnableAnimations(enableAnimationsCheck.isSelected());
        settings.setAnimationSpeed(GameSettings.AnimationSpeed.values()[animationSpeedCombo.getSelectedIndex()]);
        settings.setCelebrateVictory(celebrateVictoryCheck.isSelected());
        settings.setCelebrationStyle(GameSettings.CelebrationStyle.values()[celebrationStyleCombo.getSelectedIndex()]);
        settings.setSoundEnabled(soundEnabledCheck.isSelected());
        settings.setSoundTheme(GameSettings.SoundTheme.values()[soundThemeCombo.getSelectedIndex()]);
        settings.setEnablePremoves(enablePremovesCheck.isSelected());
        settings.setAutoPromoteToQueen(autoPromoteQueenCheck.isSelected());
        settings.setConfirmResignDraw(confirmResignDrawCheck.isSelected());
        settings.setLowTimeAlert(lowTimeAlertCheck.isSelected());
        settings.setDistractionFreeMode(distractionFreeModeCheck.isSelected());
        settings.setWhiteAlwaysBottom(whiteAlwaysBottomCheck.isSelected());
        settings.setShowEngineEvaluation(showEngineEvalCheck.isSelected());
        settings.setShowCoachComments(showCoachCommentsCheck.isSelected());
        settings.setShowTimestamp(showTimestampCheck.isSelected());
        settings.setShowMoveClassificationIcons(showMoveIconsCheck.isSelected());
        settings.setBoardTheme(GameSettings.BoardTheme.values()[boardThemeCombo.getSelectedIndex()]);
        settings.setPieceSet(GameSettings.PieceSet.values()[pieceSetCombo.getSelectedIndex()]);
    }
}