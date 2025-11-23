package com.chess.core.entities.pieces;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;

import java.util.List;
import java.util.Objects;

/**
 * Classe abstraite représentant une pièce d'échecs.
 */
public abstract class Piece {
    protected final Color color;
    protected final PieceType type;
    protected boolean hasMoved;

    protected Piece(Color color, PieceType type) {
        this.color = Objects.requireNonNull(color, "La couleur ne peut pas être null");
        this.type = Objects.requireNonNull(type, "Le type ne peut pas être null");
        this.hasMoved = false;
    }

    /**
     * Calcule tous les mouvements légaux possibles pour cette pièce.
     */
    public abstract List<Position> getLegalMoves(Position from, Board board);

    /**
     * Vérifie si un mouvement est valide pour cette pièce.
     */
    public abstract boolean isValidMove(Position from, Position to, Board board);

    /**
     * Retourne le symbole FEN de la pièce.
     */
    public char toFen() {
        return type.toFen(color);
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Vérifie si le chemin entre deux positions est libre.
     */
    protected boolean isPathClear(Position from, Position to, Board board) {
        int rowDiff = Integer.compare(to.getRow() - from.getRow(), 0);
        int colDiff = Integer.compare(to.getCol() - from.getCol(), 0);

        Position current = new Position(from.getRow() + rowDiff, from.getCol() + colDiff);

        while (!current.equals(to)) {
            if (board.getPieceAt(current) != null) {
                return false;
            }
            current = new Position(current.getRow() + rowDiff, current.getCol() + colDiff);
        }

        return true;
    }

    /**
     * Vérifie si la position cible est occupée par une pièce ennemie.
     */
    protected boolean isEnemyPiece(Position position, Board board) {
        Piece piece = board.getPieceAt(position);
        return piece != null && piece.getColor() != this.color;
    }

    /**
     * Vérifie si la position cible est libre ou occupée par un ennemi.
     */
    protected boolean isValidTarget(Position position, Board board) {
        Piece piece = board.getPieceAt(position);
        return piece == null || piece.getColor() != this.color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return color == piece.color && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    @Override
    public String toString() {
        return color + " " + type;
    }
}