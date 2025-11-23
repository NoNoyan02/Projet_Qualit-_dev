package com.chess.core.entities;

import java.util.Objects;

/**
 * Représente une position sur le plateau d'échecs (ex: e4, a1, h8).
 */
public class Position {
    private final int row; // 0-7 (1-8 sur l'échiquier)
    private final int col; // 0-7 (a-h sur l'échiquier)

    public Position(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Position invalide: row=" + row + ", col=" + col);
        }
        this.row = row;
        this.col = col;
    }

    /**
     * Crée une position à partir de la notation algébrique (ex: "e4").
     */
    public static Position fromAlgebraic(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Notation invalide: " + notation);
        }
        char colChar = notation.charAt(0);
        char rowChar = notation.charAt(1);

        if (colChar < 'a' || colChar > 'h' || rowChar < '1' || rowChar > '8') {
            throw new IllegalArgumentException("Notation invalide: " + notation);
        }

        int col = colChar - 'a';
        int row = rowChar - '1';
        return new Position(row, col);
    }

    /**
     * Convertit la position en notation algébrique (ex: "e4").
     */
    public String toAlgebraic() {
        char colChar = (char) ('a' + col);
        char rowChar = (char) ('1' + row);
        return "" + colChar + rowChar;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /**
     * Vérifie si la position est valide sur le plateau.
     */
    public boolean isValid() {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }

    /**
     * Calcule la distance Manhattan entre deux positions.
     */
    public int manhattanDistance(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }

    /**
     * Vérifie si deux positions sont sur la même diagonale.
     */
    public boolean isDiagonal(Position other) {
        return Math.abs(this.row - other.row) == Math.abs(this.col - other.col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}