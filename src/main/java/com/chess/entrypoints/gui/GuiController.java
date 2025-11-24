package com.chess.entrypoints.gui;

import com.chess.configuration.AppConfig;
import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.*;
import com.chess.core.entities.player.AIPlayer;
import com.chess.core.entities.player.Player;
import com.chess.core.usecases.AnalyzePositionUseCase;
import com.chess.core.usecases.GetBestMoveUseCase;
import com.chess.core.usecases.MovePieceUseCase;
import com.chess.entrypoints.gui.components.BoardPanel;
import com.chess.entrypoints.gui.screens.MainMenuScreen;
import com.chess.entrypoints.gui.screens.GameScreen;
import com.chess.entrypoints.gui.screens.AnalysisScreen;

import javax.swing.*;
import java.awt.*;

/**
 * Contrôleur principal de l'interface graphique.
 * Gère la navigation entre les écrans et l'état global.
 */
public class GuiController {
    private final AppConfig config;
    private final MovePieceUseCase movePieceUseCase;
    private final GetBestMoveUseCase getBestMoveUseCase;
    private final AnalyzePositionUseCase analyzePositionUseCase;

    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private GameSettings settings;
    private GameState currentGame;
    private GameClock gameClock;
    private Player whitePlayer;
    private Player blackPlayer;

    // Écrans
    private MainMenuScreen mainMenuScreen;
    private GameScreen gameScreen;
    private AnalysisScreen analysisScreen;

    public GuiController(AppConfig config) {
        this.config = config;
        this.movePieceUseCase = config.getMovePieceUseCase();
        this.getBestMoveUseCase = config.getGetBestMoveUseCase();
        this.analyzePositionUseCase = config.getAnalyzePositionUseCase();
        this.settings = new GameSettings();

        // Initialisation de la fenêtre principale
        frame = new JFrame("Chess Game - Groupe 207");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 900);
        frame.setLocationRelativeTo(null);

        // CardLayout pour gérer les différents écrans
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initializeScreens();
        frame.add(mainPanel);
    }

    private void initializeScreens() {
        // Écran principal
        mainMenuScreen = new MainMenuScreen(this);
        mainPanel.add(mainMenuScreen, "MENU");

        // Écran de jeu (sera initialisé lors du démarrage d'une partie)
        // gameScreen sera créé dynamiquement

        // Écran d'analyse
        // analysisScreen sera créé dynamiquement
    }

    /**
     * Lance l'application.
     */
    public void start() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            showMainMenu();
        });
    }

    /**
     * Affiche le menu principal.
     */
    public void showMainMenu() {
        cardLayout.show(mainPanel, "MENU");
    }

    /**
     * Démarre une nouvelle partie.
     */
    public void startNewGame(GameConfig gameConfig) {
        // Initialisation de la partie
        currentGame = new GameState();

        if (gameConfig.getStartingFen() != null && !gameConfig.getStartingFen().isEmpty()) {
            currentGame.initializeFromFen(gameConfig.getStartingFen());
        } else {
            currentGame.initializeGame();
        }

        // Configuration des joueurs
        setupPlayers(gameConfig);

        // Configuration de l'horloge
        if (gameConfig.getTimeControl() != null) {
            gameClock = new GameClock(gameConfig.getTimeControl());
            setupClockCallbacks();
        }

        // Création de l'écran de jeu
        gameScreen = new GameScreen(this, currentGame, gameClock, settings);
        gameScreen.setWhitePlayer(whitePlayer);
        gameScreen.setBlackPlayer(blackPlayer);

        mainPanel.add(gameScreen, "GAME");
        cardLayout.show(mainPanel, "GAME");

        // Démarrage de l'horloge
        if (gameClock != null) {
            gameClock.start();
        }

        // Si l'IA joue les blancs, faire jouer le premier coup
        if (whitePlayer.isAI() && currentGame.getActivePlayer() == Color.WHITE) {
            SwingUtilities.invokeLater(() -> makeAIMove());
        }
    }

    private void setupPlayers(GameConfig config) {
        if (config.isPlayAgainstBot()) {
            if (config.getPlayerColor() == Color.WHITE) {
                whitePlayer = new Player("Vous", Color.WHITE);
                blackPlayer = new AIPlayer("Bot", Color.BLACK, config.getBotElo() / 100);
            } else {
                whitePlayer = new AIPlayer("Bot", Color.WHITE, config.getBotElo() / 100);
                blackPlayer = new Player("Vous", Color.BLACK);
            }
        } else {
            whitePlayer = new Player("Joueur Blanc", Color.WHITE);
            blackPlayer = new Player("Joueur Noir", Color.BLACK);
        }
    }

    private void setupClockCallbacks() {
        gameClock.setOnTimeUpdate(color -> {
            if (gameScreen != null) {
                gameScreen.updateClock();
            }
        });

        gameClock.setOnTimeExpired(color -> {
            SwingUtilities.invokeLater(() -> {
                gameClock.stop();
                String winner = color == Color.WHITE ? "Les Noirs" : "Les Blancs";
                JOptionPane.showMessageDialog(frame,
                        winner + " gagnent au temps !",
                        "Partie terminée",
                        JOptionPane.INFORMATION_MESSAGE);
                currentGame.updateStatus(GameState.GameStatus.TIMEOUT);
                if (gameScreen != null) {
                    gameScreen.gameEnded();
                }
            });
        });

        gameClock.setOnLowTime(color -> {
            if (settings.isLowTimeAlert() && gameScreen != null) {
                gameScreen.showLowTimeAlert(color);
            }
        });
    }

    /**
     * Tente de jouer un coup.
     */
    public void attemptMove(Position from, Position to) {
        try {
            Move move = movePieceUseCase.execute(currentGame, from, to);

            // Mise à jour de l'interface
            if (gameScreen != null) {
                gameScreen.onMoveExecuted(move);
            }

            // Changement de joueur sur l'horloge
            if (gameClock != null && gameClock.isRunning()) {
                gameClock.switchPlayer();
            }

            // Vérification de la fin de partie
            if (currentGame.isGameOver()) {
                handleGameOver();
            } else {
                // Si c'est au tour de l'IA, jouer
                Player currentPlayer = getCurrentPlayer();
                if (currentPlayer.isAI()) {
                    SwingUtilities.invokeLater(() -> makeAIMove());
                }
            }

        } catch (MovePieceUseCase.IllegalMoveException e) {
            JOptionPane.showMessageDialog(frame,
                    "Coup illégal : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fait jouer l'IA.
     */
    private void makeAIMove() {
        if (currentGame.isGameOver()) return;

        Player currentPlayer = getCurrentPlayer();
        if (!currentPlayer.isAI()) return;

        AIPlayer aiPlayer = (AIPlayer) currentPlayer;

        // Afficher un indicateur de réflexion
        if (gameScreen != null) {
            gameScreen.showAIThinking(true);
        }

        // Calculer le coup dans un thread séparé
        new Thread(() -> {
            try {
                Thread.sleep(300); // Petit délai pour l'effet visuel
                Move bestMove = getBestMoveUseCase.execute(currentGame);

                if (bestMove != null) {
                    SwingUtilities.invokeLater(() -> {
                        if (gameScreen != null) {
                            gameScreen.showAIThinking(false);
                        }
                        attemptMove(bestMove.getFrom(), bestMove.getTo());
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    if (gameScreen != null) {
                        gameScreen.showAIThinking(false);
                    }
                    JOptionPane.showMessageDialog(frame,
                            "Erreur de l'IA : " + e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    /**
     * Gère la fin de partie.
     */
    private void handleGameOver() {
        if (gameClock != null) {
            gameClock.stop();
        }

        String message = getGameOverMessage();

        int choice = JOptionPane.showOptionDialog(frame,
                message,
                "Partie terminée",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Analyser", "Menu principal"},
                "Analyser");

        if (choice == 0) {
            showAnalysis();
        } else {
            showMainMenu();
        }
    }

    private String getGameOverMessage() {
        return switch (currentGame.getStatus()) {
            case CHECKMATE -> {
                Color winner = currentGame.getActivePlayer().opposite();
                yield "Échec et mat ! Les " + (winner == Color.WHITE ? "Blancs" : "Noirs") + " gagnent !";
            }
            case STALEMATE -> "Pat ! Match nul.";
            case DRAW -> "Match nul.";
            case RESIGNED -> "Abandon.";
            case TIMEOUT -> "Défaite au temps.";
            default -> "Partie terminée.";
        };
    }

    /**
     * Abandonne la partie.
     */
    public void resign() {
        if (settings.isConfirmResignDraw()) {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Voulez-vous vraiment abandonner ?",
                    "Confirmer l'abandon",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        if (gameClock != null) {
            gameClock.stop();
        }

        currentGame.updateStatus(GameState.GameStatus.RESIGNED);
        handleGameOver();
    }

    /**
     * Propose une nulle.
     */
    public void offerDraw() {
        if (settings.isConfirmResignDraw()) {
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "Proposer la nulle ?",
                    "Proposition de nulle",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Dans un jeu à 2 joueurs, demander acceptation
                int accept = JOptionPane.showConfirmDialog(frame,
                        "Accepter la proposition de nulle ?",
                        "Nulle proposée",
                        JOptionPane.YES_NO_OPTION);

                if (accept == JOptionPane.YES_OPTION) {
                    if (gameClock != null) {
                        gameClock.stop();
                    }
                    currentGame.updateStatus(GameState.GameStatus.DRAW);
                    handleGameOver();
                }
            }
        }
    }

    /**
     * Affiche l'écran d'analyse.
     */
    public void showAnalysis() {
        analysisScreen = new AnalysisScreen(this, currentGame, analyzePositionUseCase, settings);
        mainPanel.add(analysisScreen, "ANALYSIS");
        cardLayout.show(mainPanel, "ANALYSIS");
    }

    /**
     * Ouvre les paramètres.
     */
    public void openSettings() {
        SettingsDialog dialog = new SettingsDialog(frame, settings);
        dialog.setVisible(true);

        // Appliquer les nouveaux paramètres
        if (gameScreen != null) {
            gameScreen.applySettings(settings);
        }
    }

    // Getters
    public Player getCurrentPlayer() {
        return currentGame.getActivePlayer() == Color.WHITE ? whitePlayer : blackPlayer;
    }

    public GameState getCurrentGame() {
        return currentGame;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public JFrame getFrame() {
        return frame;
    }

    /**
     * Configuration d'une nouvelle partie.
     */
    public static class GameConfig {
        private TimeControl timeControl;
        private boolean playAgainstBot;
        private Color playerColor;
        private int botElo;
        private String startingFen;

        public GameConfig() {
            this.playAgainstBot = false;
            this.playerColor = Color.WHITE;
            this.botElo = 1500;
        }

        // Getters et setters
        public TimeControl getTimeControl() { return timeControl; }
        public void setTimeControl(TimeControl timeControl) { this.timeControl = timeControl; }

        public boolean isPlayAgainstBot() { return playAgainstBot; }
        public void setPlayAgainstBot(boolean playAgainstBot) { this.playAgainstBot = playAgainstBot; }

        public Color getPlayerColor() { return playerColor; }
        public void setPlayerColor(Color playerColor) { this.playerColor = playerColor; }

        public int getBotElo() { return botElo; }
        public void setBotElo(int botElo) { this.botElo = botElo; }

        public String getStartingFen() { return startingFen; }
        public void setStartingFen(String startingFen) { this.startingFen = startingFen; }
    }
}