package com.chess.core.ports;

import java.util.List;

/**
 * Interface pour un moteur d'échecs (ex: Stockfish).
 * Port de sortie de la Clean Architecture.
 */
public interface ChessEngine {

    /**
     * Démarre le moteur d'échecs.
     */
    void start();

    /**
     * Arrête le moteur d'échecs.
     */
    void stop();

    /**
     * Retourne le meilleur coup pour une position FEN donnée.
     *
     * @param fen la position en notation FEN
     * @param thinkingTimeMs temps de réflexion en millisecondes
     * @return le meilleur coup en notation algébrique (ex: "e2e4")
     */
    String getBestMove(String fen, int thinkingTimeMs);

    /**
     * Évalue la position actuelle.
     *
     * @param fen la position en notation FEN
     * @return l'évaluation en centipawns (100 = 1 pion d'avantage)
     */
    double evaluatePosition(String fen);

    /**
     * Retourne tous les coups légaux pour une position donnée.
     *
     * @param fen la position en notation FEN
     * @return liste des coups légaux en notation algébrique
     */
    List<String> getLegalMoves(String fen);

    /**
     * Configure le niveau de l'IA (1-20 pour Stockfish).
     *
     * @param level niveau de difficulté
     */
    void setSkillLevel(int level);

    /**
     * Retourne le nom et la version du moteur.
     */
    String getEngineInfo();

    /**
     * Vérifie si le moteur est prêt.
     */
    boolean isReady();
}