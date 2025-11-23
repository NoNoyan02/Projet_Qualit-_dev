package com.chess.core.ports;

import com.chess.core.entities.game.GameState;

import java.util.List;
import java.util.Optional;

/**
 * Interface pour la persistance des parties.
 * Port de sortie de la Clean Architecture.
 */
public interface GameRepository {

    /**
     * Sauvegarde une partie.
     *
     * @param gameId identifiant unique de la partie
     * @param gameState état de la partie à sauvegarder
     */
    void save(String gameId, GameState gameState);

    /**
     * Charge une partie.
     *
     * @param gameId identifiant de la partie
     * @return la partie si elle existe
     */
    Optional<GameState> load(String gameId);

    /**
     * Supprime une partie.
     *
     * @param gameId identifiant de la partie
     * @return true si la suppression a réussi
     */
    boolean delete(String gameId);

    /**
     * Liste toutes les parties sauvegardées.
     *
     * @return liste des identifiants de parties
     */
    List<String> listAllGames();

    /**
     * Vérifie si une partie existe.
     *
     * @param gameId identifiant de la partie
     * @return true si la partie existe
     */
    boolean exists(String gameId);

    /**
     * Sauvegarde automatiquement après chaque coup.
     *
     * @param gameId identifiant de la partie
     * @param gameState état actuel de la partie
     */
    void autoSave(String gameId, GameState gameState);
}