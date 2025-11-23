package com.chess.core.usecases;

import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.ports.ChessEngine;

/**
 * Use case pour obtenir le meilleur coup selon le moteur d'échecs.
 */
public class GetBestMoveUseCase {
    private final ChessEngine engine;
    private final int defaultThinkingTime;

    public GetBestMoveUseCase(ChessEngine engine) {
        this(engine, 1000); // 1 seconde par défaut
    }

    public GetBestMoveUseCase(ChessEngine engine, int defaultThinkingTime) {
        this.engine = engine;
        this.defaultThinkingTime = defaultThinkingTime;
    }

    /**
     * Calcule le meilleur coup pour l'état actuel de la partie.
     */
    public Move execute(GameState gameState) {
        return execute(gameState, defaultThinkingTime);
    }

    /**
     * Calcule le meilleur coup avec un temps de réflexion spécifique.
     */
    public Move execute(GameState gameState, int thinkingTimeMs) {
        if (!engine.isReady()) {
            engine.start();
        }

        String fen = gameState.toFen();
        String bestMoveStr = engine.getBestMove(fen, thinkingTimeMs);

        if (bestMoveStr == null || bestMoveStr.isEmpty()) {
            throw new NoMoveFoundException("Le moteur n'a pas trouvé de coup");
        }

        return Move.fromAlgebraic(bestMoveStr, gameState.getBoard());
    }

    /**
     * Exception levée quand aucun coup n'est trouvé.
     */
    public static class NoMoveFoundException extends RuntimeException {
        public NoMoveFoundException(String message) {
            super(message);
        }
    }
}