package com.chess.core.ports;

import com.chess.core.entities.game.Move;

import java.util.List;

/**
 * Interface pour la journalisation des coups.
 * Port de sortie de la Clean Architecture.
 */
public interface MoveLogger {

    /**
     * Enregistre un coup joué.
     *
     * @param gameId identifiant de la partie
     * @param move le coup joué
     * @param moveNumber numéro du coup
     */
    void logMove(String gameId, Move move, int moveNumber);

    /**
     * Retourne l'historique complet des coups d'une partie.
     *
     * @param gameId identifiant de la partie
     * @return liste des coups en notation algébrique
     */
    List<String> getMoveHistory(String gameId);

    /**
     * Efface l'historique d'une partie.
     *
     * @param gameId identifiant de la partie
     */
    void clearHistory(String gameId);

    /**
     * Exporte l'historique au format PGN (Portable Game Notation).
     *
     * @param gameId identifiant de la partie
     * @param whitePlayer nom du joueur blanc
     * @param blackPlayer nom du joueur noir
     * @return la partie au format PGN
     */
    String exportToPGN(String gameId, String whitePlayer, String blackPlayer);

    /**
     * Enregistre un événement de la partie (début, fin, etc.).
     *
     * @param gameId identifiant de la partie
     * @param event description de l'événement
     */
    void logEvent(String gameId, String event);
}