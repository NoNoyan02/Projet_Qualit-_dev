package com.chess.core.entities.game;

/**
 * Classe contenant tous les paramètres de jeu personnalisables.
 */
public class GameSettings {
    // Paramètres d'affichage
    private CoordinateDisplay coordinateDisplay = CoordinateDisplay.INSIDE;
    private boolean highlightMoves = true;
    private boolean showLegalMoves = true;

    // Paramètres de déplacement
    private MoveMode moveMode = MoveMode.DRAG_AND_DROP;

    // Animations
    private boolean enableAnimations = true;
    private AnimationSpeed animationSpeed = AnimationSpeed.MEDIUM;
    private boolean celebrateVictory = true;
    private CelebrationStyle celebrationStyle = CelebrationStyle.CONFETTI;

    // Sons
    private boolean soundEnabled = true;
    private SoundTheme soundTheme = SoundTheme.GAME_MODE;

    // Gameplay
    private boolean enablePremoves = false;
    private boolean autoPromoteToQueen = true;
    private boolean confirmResignDraw = true;
    private boolean lowTimeAlert = true;
    private boolean distractionFreeMode = false;
    private boolean whiteAlwaysBottom = true;

    // Analyse
    private boolean showEngineEvaluation = true;
    private boolean showCoachComments = true;
    private boolean showTimestamp = true;
    private boolean showMoveClassificationIcons = true;

    // Thèmes
    private BoardTheme boardTheme = BoardTheme.GREEN;
    private PieceSet pieceSet = PieceSet.NEO;

    // Getters et Setters
    public CoordinateDisplay getCoordinateDisplay() { return coordinateDisplay; }
    public void setCoordinateDisplay(CoordinateDisplay coordinateDisplay) {
        this.coordinateDisplay = coordinateDisplay;
    }

    public boolean isHighlightMoves() { return highlightMoves; }
    public void setHighlightMoves(boolean highlightMoves) {
        this.highlightMoves = highlightMoves;
    }

    public boolean isShowLegalMoves() { return showLegalMoves; }
    public void setShowLegalMoves(boolean showLegalMoves) {
        this.showLegalMoves = showLegalMoves;
    }

    public MoveMode getMoveMode() { return moveMode; }
    public void setMoveMode(MoveMode moveMode) {
        this.moveMode = moveMode;
    }

    public boolean isEnableAnimations() { return enableAnimations; }
    public void setEnableAnimations(boolean enableAnimations) {
        this.enableAnimations = enableAnimations;
    }

    public AnimationSpeed getAnimationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(AnimationSpeed animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public boolean isCelebrateVictory() { return celebrateVictory; }
    public void setCelebrateVictory(boolean celebrateVictory) {
        this.celebrateVictory = celebrateVictory;
    }

    public CelebrationStyle getCelebrationStyle() { return celebrationStyle; }
    public void setCelebrationStyle(CelebrationStyle celebrationStyle) {
        this.celebrationStyle = celebrationStyle;
    }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public SoundTheme getSoundTheme() { return soundTheme; }
    public void setSoundTheme(SoundTheme soundTheme) {
        this.soundTheme = soundTheme;
    }

    public boolean isEnablePremoves() { return enablePremoves; }
    public void setEnablePremoves(boolean enablePremoves) {
        this.enablePremoves = enablePremoves;
    }

    public boolean isAutoPromoteToQueen() { return autoPromoteToQueen; }
    public void setAutoPromoteToQueen(boolean autoPromoteToQueen) {
        this.autoPromoteToQueen = autoPromoteToQueen;
    }

    public boolean isConfirmResignDraw() { return confirmResignDraw; }
    public void setConfirmResignDraw(boolean confirmResignDraw) {
        this.confirmResignDraw = confirmResignDraw;
    }

    public boolean isLowTimeAlert() { return lowTimeAlert; }
    public void setLowTimeAlert(boolean lowTimeAlert) {
        this.lowTimeAlert = lowTimeAlert;
    }

    public boolean isDistractionFreeMode() { return distractionFreeMode; }
    public void setDistractionFreeMode(boolean distractionFreeMode) {
        this.distractionFreeMode = distractionFreeMode;
    }

    public boolean isWhiteAlwaysBottom() { return whiteAlwaysBottom; }
    public void setWhiteAlwaysBottom(boolean whiteAlwaysBottom) {
        this.whiteAlwaysBottom = whiteAlwaysBottom;
    }

    public boolean isShowEngineEvaluation() { return showEngineEvaluation; }
    public void setShowEngineEvaluation(boolean showEngineEvaluation) {
        this.showEngineEvaluation = showEngineEvaluation;
    }

    public boolean isShowCoachComments() { return showCoachComments; }
    public void setShowCoachComments(boolean showCoachComments) {
        this.showCoachComments = showCoachComments;
    }

    public boolean isShowTimestamp() { return showTimestamp; }
    public void setShowTimestamp(boolean showTimestamp) {
        this.showTimestamp = showTimestamp;
    }

    public boolean isShowMoveClassificationIcons() { return showMoveClassificationIcons; }
    public void setShowMoveClassificationIcons(boolean showMoveClassificationIcons) {
        this.showMoveClassificationIcons = showMoveClassificationIcons;
    }

    public BoardTheme getBoardTheme() { return boardTheme; }
    public void setBoardTheme(BoardTheme boardTheme) {
        this.boardTheme = boardTheme;
    }

    public PieceSet getPieceSet() { return pieceSet; }
    public void setPieceSet(PieceSet pieceSet) {
        this.pieceSet = pieceSet;
    }

    // Enums
    public enum CoordinateDisplay {
        NONE("Désactivé"),
        INSIDE("À l'intérieur"),
        OUTSIDE("À l'extérieur");

        private final String label;
        CoordinateDisplay(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum MoveMode {
        DRAG_AND_DROP("Faire glisser"),
        CLICK("Cliquer sur les cases"),
        BOTH("Les deux");

        private final String label;
        MoveMode(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum AnimationSpeed {
        SLOW(800),
        MEDIUM(400),
        FAST(200),
        INSTANT(0);

        private final int durationMs;
        AnimationSpeed(int durationMs) { this.durationMs = durationMs; }
        public int getDurationMs() { return durationMs; }
    }

    public enum CelebrationStyle {
        CONFETTI("Confettis"),
        FIREWORKS("Feux d'artifice"),
        NONE("Aucun");

        private final String label;
        CelebrationStyle(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum SoundTheme {
        GAME_MODE("Mode de jeu"),
        CLASSIC("Classique"),
        MODERN("Moderne"),
        MUTE("Silencieux");

        private final String label;
        SoundTheme(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum BoardTheme {
        GREEN("Vert"),
        BROWN("Marron"),
        BLUE("Bleu"),
        GRAY("Gris"),
        WOOD("Bois"),
        MARBLE("Marbre");

        private final String label;
        BoardTheme(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum PieceSet {
        NEO("Neo"),
        CLASSIC("Classique"),
        MODERN("Moderne"),
        ALPHA("Alpha"),
        STAUNTY("Staunty");

        private final String label;
        PieceSet(String label) { this.label = label; }
        public String getLabel() { return label; }
    }
}