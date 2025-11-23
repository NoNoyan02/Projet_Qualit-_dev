package com.chess.core.usecases;

import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.ports.ChessEngine;

/**
 * Use case pour obtenir le meilleur coup selon le moteur d'échecs.
 */
public class GetBestMoveUseCase {
    private final ChessEngine engine;

    public GetBestMoveUseCase(ChessEngine engine) {
        this.engine = engine;
    }

    public Move execute(GameState gameState) {
        if (!engine.isReady()) {
            engine.start();
        }
        String fen = gameState.toFen();
        String bestMoveStr = engine.getBestMove(fen, 1000); // 1 seconde de réflexion
        // Convertir bestMoveStr en objet Move
        return parseMove(bestMoveStr,gameState);
    }

    private Move parseMove(String bestMoveStr,GameState gameState) {
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