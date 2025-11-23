package com.chess.configuration;

import com.chess.core.ports.ChessEngine;
import com.chess.core.ports.GameRepository;
import com.chess.core.ports.MoveLogger;
import com.chess.core.usecases.AnalyzePositionUseCase;
import com.chess.core.usecases.GetBestMoveUseCase;
import com.chess.core.usecases.MovePieceInteractor;
import com.chess.core.usecases.MovePieceUseCase;
import com.chess.dataproviders.file.FileGameRepository;
import com.chess.dataproviders.file.FileMoveLogger;
import com.chess.dataproviders.stockfish.StockfishEngine;

/**
 * Configuration centralisée de l'application.
 * Implémente l'injection de dépendances manuelle (Clean Architecture).
 */
public class AppConfig {
    private static final String SAVE_DIRECTORY = "./saves";
    private static final String LOG_DIRECTORY = "./logs";

    private final GameRepository gameRepository;
    private final MoveLogger moveLogger;
    private final ChessEngine chessEngine;

    private final MovePieceUseCase movePieceUseCase;
    private final GetBestMoveUseCase getBestMoveUseCase;
    private final AnalyzePositionUseCase analyzePositionUseCase;

    /**
     * Constructeur qui initialise toutes les dépendances.
     */
    public AppConfig() {
        // Dataproviders (Adapters)
        this.gameRepository = new FileGameRepository(SAVE_DIRECTORY);
        this.moveLogger = new FileMoveLogger(LOG_DIRECTORY);
        this.chessEngine = new StockfishEngine();

        // Use Cases
        this.movePieceUseCase = new MovePieceInteractor(moveLogger);
        this.getBestMoveUseCase = new GetBestMoveUseCase(chessEngine);
        this.analyzePositionUseCase = new AnalyzePositionUseCase(chessEngine);
    }

    /**
     * Configuration pour les tests (avec dépendances mockées).
     */
    public AppConfig(GameRepository gameRepository,
                     MoveLogger moveLogger,
                     ChessEngine chessEngine) {
        this.gameRepository = gameRepository;
        this.moveLogger = moveLogger;
        this.chessEngine = chessEngine;

        this.movePieceUseCase = new MovePieceInteractor(moveLogger);
        this.getBestMoveUseCase = new GetBestMoveUseCase(chessEngine);
        this.analyzePositionUseCase = new AnalyzePositionUseCase(chessEngine);
    }

    // Getters pour les use cases

    public MovePieceUseCase getMovePieceUseCase() {
        return movePieceUseCase;
    }

    public GetBestMoveUseCase getGetBestMoveUseCase() {
        return getBestMoveUseCase;
    }

    public AnalyzePositionUseCase getAnalyzePositionUseCase() {
        return analyzePositionUseCase;
    }

    // Getters pour les repositories

    public GameRepository getGameRepository() {
        return gameRepository;
    }

    public MoveLogger getMoveLogger() {
        return moveLogger;
    }

    public ChessEngine getChessEngine() {
        return chessEngine;
    }

    /**
     * Initialise les ressources nécessaires.
     */
    public void initialize() {
        // Démarrage du moteur d'échecs si nécessaire
        if (!chessEngine.isReady()) {
            chessEngine.start();
        }
    }

    /**
     * Libère les ressources.
     */
    public void shutdown() {
        if (chessEngine.isReady()) {
            chessEngine.stop();
        }
    }
}