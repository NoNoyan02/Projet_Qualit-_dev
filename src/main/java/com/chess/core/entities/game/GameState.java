package com.chess.core.entities.game;

import com.chess.core.entities.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente l'état actuel de la partie d'échecs.
 */
public class GameState {
    private final Board board;
    private Color activePlayer;
    private GameStatus status;
    private final List<Move> moveHistory;
    private Move lastMove;

    public GameState() {
        this.board = new Board();
        this.activePlayer = Color.WHITE;
        this.status = GameStatus.IN_PROGRESS;
        this.moveHistory = new ArrayList<>();
    }

    public GameState(Board board, Color activePlayer) {
        this.board = board;
        this.activePlayer = activePlayer;
        this.status = GameStatus.IN_PROGRESS;
        this.moveHistory = new ArrayList<>();
    }

    /**
     * Initialise la partie avec la position standard.
     */
    public void initializeGame() {
        board.setupInitialPosition();
        activePlayer = Color.WHITE;
        status = GameStatus.IN_PROGRESS;
        moveHistory.clear();
    }

    /**
     * Initialise la partie à partir d'une position FEN.
     */
    public void initializeFromFen(String fen) {
        board.setupFromFen(fen);
        String[] parts = fen.split(" ");
        if (parts.length >= 2) {
            activePlayer = Color.fromFen(parts[1].charAt(0));
        }
        status = GameStatus.IN_PROGRESS;
        moveHistory.clear();
    }

    /**
     * Enregistre un mouvement dans l'historique.
     */
    public void recordMove(Move move) {
        moveHistory.add(move);
        lastMove = move;
    }

    /**
     * Change le joueur actif.
     */
    public void switchPlayer() {
        activePlayer = activePlayer.opposite();
    }

    /**
     * Met à jour le statut de la partie.
     */
    public void updateStatus(GameStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * Vérifie si la partie est terminée.
     */
    public boolean isGameOver() {
        return status == GameStatus.CHECKMATE ||
                status == GameStatus.STALEMATE ||
                status == GameStatus.DRAW;
    }

    /**
     * Retourne la notation FEN de la position actuelle.
     */
    public String toFen() {
        return board.toFen(activePlayer);
    }

    /**
     * Retourne l'historique des coups en notation algébrique.
     */
    public List<String> getMoveHistoryAlgebraic() {
        List<String> algebraic = new ArrayList<>();
        for (Move move : moveHistory) {
            algebraic.add(move.toAlgebraic());
        }
        return algebraic;
    }

    // Getters
    public Board getBoard() {
        return board;
    }

    public Color getActivePlayer() {
        return activePlayer;
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public Move getLastMove() {
        return lastMove;
    }

    public int getMoveCount() {
        return moveHistory.size();
    }

    /**
     * Enum représentant le statut de la partie.
     */
    public enum GameStatus {
        IN_PROGRESS,
        CHECK,
        CHECKMATE,
        STALEMATE,
        DRAW,
        RESIGNED,
        TIMEOUT
    }
}