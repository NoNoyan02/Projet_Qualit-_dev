package com.chess.core.entities.player;

import com.chess.core.entities.Color;
import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.ports.ChessEngine;

/**
 * Représente un joueur IA utilisant un moteur d'échecs.
 */
public class AIPlayer extends Player {
    private final ChessEngine engine;
    private final int skillLevel; // 1-20 pour Stockfish

    public AIPlayer(String name, Color color, ChessEngine engine) {
        this(name, color, engine, 10);
    }

    public AIPlayer(String name, Color color, ChessEngine engine, int skillLevel) {
        super(name, color, PlayerType.AI);
        this.engine = engine;
        this.skillLevel = Math.max(1, Math.min(20, skillLevel));
    }

    /**
     * Calcule le meilleur coup à jouer.
     */
    public Move calculateBestMove(GameState gameState, int thinkingTime) {
        String fen = gameState.toFen();
        String bestMove = engine.getBestMove(fen, thinkingTime);

        if (bestMove == null || bestMove.isEmpty()) {
            return null;
        }

        return Move.fromAlgebraic(bestMove, gameState.getBoard());
    }

    /**
     * Évalue la position actuelle.
     */
    public double evaluatePosition(GameState gameState) {
        String fen = gameState.toFen();
        return engine.evaluatePosition(fen);
    }

    public ChessEngine getEngine() {
        return engine;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public String toString() {
        return getName() + " (" + getColor() + ", AI Level " + skillLevel + ")";
    }
}