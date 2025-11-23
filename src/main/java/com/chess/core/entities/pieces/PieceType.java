package com.chess.core.entities.pieces;

/**
 * Représente les différents types de pièces d'échecs.
 */
public enum PieceType {
    KING('K', 'k'),
    QUEEN('Q', 'q'),
    ROOK('R', 'r'),
    BISHOP('B', 'b'),
    KNIGHT('N', 'n'),
    PAWN('P', 'p');

    private final char whiteSymbol;
    private final char blackSymbol;

    PieceType(char whiteSymbol, char blackSymbol) {
        this.whiteSymbol = whiteSymbol;
        this.blackSymbol = blackSymbol;
    }

    /**
     * Retourne le symbole FEN de la pièce (majuscule pour blanc, minuscule pour noir).
     */
    public char toFen(com.chess.core.entities.Color color) {
        return color == com.chess.core.entities.Color.WHITE ? whiteSymbol : blackSymbol;
    }

    /**
     * Crée un type de pièce à partir du symbole FEN.
     */
    public static PieceType fromFen(char symbol) {
        for (PieceType type : values()) {
            if (type.whiteSymbol == symbol || type.blackSymbol == symbol) {
                return type;
            }
        }
        throw new IllegalArgumentException("Symbole FEN invalide: " + symbol);
    }

    public char getWhiteSymbol() {
        return whiteSymbol;
    }

    public char getBlackSymbol() {
        return blackSymbol;
    }
}