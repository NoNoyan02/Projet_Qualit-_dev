package com.chess.core.entities;

/**
 * Représente la couleur d'une pièce d'échecs.
 */
public enum Color {
    WHITE,
    BLACK;

    /**
     * Retourne la couleur opposée.
     */
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    /**
     * Convertit la couleur en notation FEN (w/b).
     */
    public char toFen() {
        return this == WHITE ? 'w' : 'b';
    }

    /**
     * Crée une couleur à partir de la notation FEN.
     */
    public static Color fromFen(char c) {
        return c == 'w' ? WHITE : BLACK;
    }
}