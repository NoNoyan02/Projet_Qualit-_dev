package com.chess.core.entities.game;

import com.chess.core.entities.Position;
import com.chess.core.entities.pieces.Piece;
import com.chess.core.entities.pieces.PieceType;

import java.util.Objects;

/**
 * Représente un mouvement d'une pièce sur le plateau.
 */
public class Move {
    private final Position from;
    private final Position to;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final MoveType moveType;
    private final PieceType promotionPiece;

    private Move(Builder builder) {
        this.from = Objects.requireNonNull(builder.from, "Position de départ requise");
        this.to = Objects.requireNonNull(builder.to, "Position d'arrivée requise");
        this.movedPiece = Objects.requireNonNull(builder.movedPiece, "Pièce déplacée requise");
        this.capturedPiece = builder.capturedPiece;
        this.moveType = builder.moveType != null ? builder.moveType : MoveType.NORMAL;
        this.promotionPiece = builder.promotionPiece;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public boolean isCapture() {
        return capturedPiece != null || moveType == MoveType.EN_PASSANT;
    }

    public boolean isPromotion() {
        return moveType == MoveType.PROMOTION;
    }

    public boolean isCastling() {
        return moveType == MoveType.CASTLING_KING_SIDE || moveType == MoveType.CASTLING_QUEEN_SIDE;
    }

    /**
     * Convertit le mouvement en notation algébrique (ex: e2e4, e7e8q pour promotion).
     */
    public String toAlgebraic() {
        StringBuilder notation = new StringBuilder();
        notation.append(from.toAlgebraic()).append(to.toAlgebraic());

        if (isPromotion() && promotionPiece != null) {
            notation.append(Character.toLowerCase(promotionPiece.toFen(movedPiece.getColor())));
        }

        return notation.toString();
    }

    /**
     * Crée un Move à partir de la notation algébrique (ex: "e2e4" ou "e7e8q").
     */
    public static Move fromAlgebraic(String notation, Board board) {
        if (notation.length() < 4) {
            throw new IllegalArgumentException("Notation invalide: " + notation);
        }

        Position from = Position.fromAlgebraic(notation.substring(0, 2));
        Position to = Position.fromAlgebraic(notation.substring(2, 4));
        Piece piece = board.getPieceAt(from);

        if (piece == null) {
            throw new IllegalArgumentException("Aucune pièce à la position " + from);
        }

        Builder builder = new Builder(from, to, piece);

        // Détection du type de mouvement
        Piece capturedPiece = board.getPieceAt(to);
        if (capturedPiece != null) {
            builder.withCapturedPiece(capturedPiece);
        }

        // Promotion
        if (notation.length() == 5) {
            char promoChar = notation.charAt(4);
            PieceType promotionType = PieceType.fromFen(Character.toUpperCase(promoChar));
            builder.withPromotion(promotionType);
        }

        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(from, move.from) &&
                Objects.equals(to, move.to) &&
                Objects.equals(movedPiece, move.movedPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, movedPiece);
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }

    // Builder pattern
    public static class Builder {
        private final Position from;
        private final Position to;
        private final Piece movedPiece;
        private Piece capturedPiece;
        private MoveType moveType;
        private PieceType promotionPiece;

        public Builder(Position from, Position to, Piece movedPiece) {
            this.from = from;
            this.to = to;
            this.movedPiece = movedPiece;
        }

        public Builder withCapturedPiece(Piece capturedPiece) {
            this.capturedPiece = capturedPiece;
            return this;
        }

        public Builder withMoveType(MoveType moveType) {
            this.moveType = moveType;
            return this;
        }

        public Builder withPromotion(PieceType promotionPiece) {
            this.moveType = MoveType.PROMOTION;
            this.promotionPiece = promotionPiece;
            return this;
        }

        public Move build() {
            return new Move(this);
        }
    }

    /**
     * Types de mouvements spéciaux.
     */
    public enum MoveType {
        NORMAL,
        CAPTURE,
        EN_PASSANT,
        CASTLING_KING_SIDE,
        CASTLING_QUEEN_SIDE,
        PROMOTION
    }
}