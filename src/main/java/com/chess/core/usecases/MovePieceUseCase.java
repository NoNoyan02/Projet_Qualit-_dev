package com.chess.core.usecases;

import com.chess.core.entities.Position;
import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.entities.pieces.PieceType;

/**
 * Interface définissant le cas d'usage pour déplacer une pièce.
 */
public interface MovePieceUseCase {

    /**
     * Exécute un mouvement sur le plateau.
     *
     * @param gameState l'état actuel de la partie
     * @param from position de départ
     * @param to position d'arrivée
     * @return le mouvement effectué
     * @throws IllegalMoveException si le mouvement est illégal
     */
    Move execute(GameState gameState, Position from, Position to);

    /**
     * Exécute un mouvement avec promotion.
     *
     * @param gameState l'état actuel de la partie
     * @param from position de départ
     * @param to position d'arrivée
     * @param promotionPiece type de pièce pour la promotion
     * @return le mouvement effectué
     * @throws IllegalMoveException si le mouvement est illégal
     */
    Move execute(GameState gameState, Position from, Position to, PieceType promotionPiece);

    /**
     * Vérifie si un mouvement est légal.
     *
     * @param gameState l'état actuel de la partie
     * @param from position de départ
     * @param to position d'arrivée
     * @return true si le mouvement est légal
     */
    boolean isLegalMove(GameState gameState, Position from, Position to);

    /**
     * Exception levée lors d'un mouvement illégal.
     */
    class IllegalMoveException extends RuntimeException {
        public IllegalMoveException(String message) {
            super(message);
        }

        public IllegalMoveException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}