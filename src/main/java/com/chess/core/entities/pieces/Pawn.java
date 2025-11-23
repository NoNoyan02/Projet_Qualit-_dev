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
        int oneForwardRow = from.getRow() + direction;
        int oneForwardCol = from.getCol();
        if (isValidCoordinate(oneForwardRow, oneForwardCol)) {
            Position oneForward = new Position(oneForwardRow, oneForwardCol);
            if (board.getPieceAt(oneForward) == null) {
                moves.add(oneForward);

                // Avance de deux cases (premier mouvement)
                if (!hasMoved) {
                    int startRow = color == Color.WHITE ? 1 : 6;
                    if (from.getRow() == startRow) {
                        int twoForwardRow = from.getRow() + 2 * direction;
                        int twoForwardCol = from.getCol();
                        if (isValidCoordinate(twoForwardRow, twoForwardCol)) {
                            Position twoForward = new Position(twoForwardRow, twoForwardCol);
                            if (board.getPieceAt(twoForward) == null) {
                                moves.add(twoForward);
                            }
                        }
                    }
                }
            }
        }

        // Captures diagonales
        int[] captureOffsets = {-1, 1};
        for (int offset : captureOffsets) {
            int captureRow = from.getRow() + direction;
            int captureCol = from.getCol() + offset;
            if (isValidCoordinate(captureRow, captureCol)) {
                Position capture = new Position(captureRow, captureCol);
                if (isEnemyPiece(capture, board)) {
                    moves.add(capture);
                }
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

    /**
     * Vérifie si des coordonnées sont valides avant de créer une Position.
     */
    private boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }
}