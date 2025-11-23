package com.chess.core.usecases;

import com.chess.core.entities.game.GameState;
import com.chess.core.ports.ChessEngine;

import java.util.List;

/**
 * Use case pour analyser une position.
 */
public class AnalyzePositionUseCase {
    private final ChessEngine engine;

    public AnalyzePositionUseCase(ChessEngine engine) {
        this.engine = engine;
    }

    /**
     * Analyse une position et retourne une évaluation.
     */
    public PositionAnalysis execute(GameState gameState) {
        if (!engine.isReady()) {
            engine.start();
        }

        String fen = gameState.toFen();
        double evaluation = engine.evaluatePosition(fen);
        List<String> legalMoves = engine.getLegalMoves(fen);
        String bestMove = engine.getBestMove(fen, 2000);

        return new PositionAnalysis(evaluation, bestMove, legalMoves);
    }

    /**
     * Résultat de l'analyse d'une position.
     */
    public static class PositionAnalysis {
        private final double evaluation; // En centipawns
        private final String bestMove;
        private final List<String> legalMoves;

        public PositionAnalysis(double evaluation, String bestMove, List<String> legalMoves) {
            this.evaluation = evaluation;
            this.bestMove = bestMove;
            this.legalMoves = legalMoves;
        }

        /**
         * Retourne l'évaluation en format lisible.
         */
        public String getEvaluationDescription() {
            if (Math.abs(evaluation) > 1000) {
                return evaluation > 0 ? "Les blancs gagnent" : "Les noirs gagnent";
            }

            double pawns = evaluation / 100.0;
            if (Math.abs(pawns) < 0.5) {
                return "Position équilibrée";
            } else if (pawns > 0) {
                return String.format("Les blancs ont un avantage de %.1f pions", pawns);
            } else {
                return String.format("Les noirs ont un avantage de %.1f pions", Math.abs(pawns));
            }
        }

        public double getEvaluation() {
            return evaluation;
        }

        public String getBestMove() {
            return bestMove;
        }

        public List<String> getLegalMoves() {
            return legalMoves;
        }

        public int getLegalMoveCount() {
            return legalMoves.size();
        }
    }
}