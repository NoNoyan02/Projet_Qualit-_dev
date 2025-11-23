package com.chess.core.entities.game;

import com.chess.core.entities.Position;
import com.chess.core.entities.pieces.Piece;

import java.util.Objects;

/**
 * Représente une case du plateau d'échecs.
 */
public class Tile {
    private final Position position;
    private Piece piece;

    public Tile(Position position) {
        this.position = Objects.requireNonNull(position, "La position ne peut pas être null");
        this.piece = null;
    }

    public Tile(Position position, Piece piece) {
        this.position = Objects.requireNonNull(position, "La position ne peut pas être null");
        this.piece = piece;
    }

    /**
     * Vérifie si la case est occupée.
     */
    public boolean isOccupied() {
        return piece != null;
    }

    /**
     * Vérifie si la case est vide.
     */
    public boolean isEmpty() {
        return piece == null;
    }

    public Position getPosition() {
        return position;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Retire la pièce de la case.
     */
    public Piece removePiece() {
        Piece removedPiece = this.piece;
        this.piece = null;
        return removedPiece;
    }

    /**
     * Vérifie si la case est blanche ou noire (pour l'affichage).
     */
    public boolean isLight() {
        return (position.getRow() + position.getCol()) % 2 == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Objects.equals(position, tile.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public String toString() {
        return position.toString() + (isOccupied() ? " [" + piece + "]" : " [empty]");
    }
}