package com.chess.core.entities.pieces;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un pion.
 */
public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Position> getLegalMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = color == Color.WHITE ? 1 : -1;

        // Avance d'une case
        Position oneForward = new Position(from.getRow() + direction, from.getCol());
        if (oneForward.isValid() && board.getPieceAt(oneForward) == null) {
            moves.add(oneForward);

            // Avance de deux cases (premier mouvement)
            if (!hasMoved) {
                int startRow = color == Color.WHITE ? 1 : 6;
                if (from.getRow() == startRow) {
                    Position twoForward = new Position(from.getRow() + 2 * direction, from.getCol());
                    if (twoForward.isValid() && board.getPieceAt(twoForward) == null) {
                        moves.add(twoForward);
                    }
                }
            }
        }

        // Captures diagonales
        int[] captureOffsets = {-1, 1};
        for (int offset : captureOffsets) {
            Position capture = new Position(from.getRow() + direction, from.getCol() + offset);
            if (capture.isValid() && isEnemyPiece(capture, board)) {
                moves.add(capture);
            }
        }

        // Prise en passant
        if (board.getEnPassantTarget() != null) {
            Position enPassant = board.getEnPassantTarget();
            if (Math.abs(from.getCol() - enPassant.getCol()) == 1 &&
                    from.getRow() + direction == enPassant.getRow()) {
                moves.add(enPassant);
            }
        }

        return moves;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        return getLegalMoves(from, board).contains(to);
    }

    /**
     * Vérifie si le pion peut être promu.
     */
    public boolean canPromote(Position position) {
        int promotionRow = color == Color.WHITE ? 7 : 0;
        return position.getRow() == promotionRow;
    }
}