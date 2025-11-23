package com.chess.core.entities.pieces;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une reine (combine les mouvements de la tour et du fou).
 */
public class Queen extends Piece {

    public Queen(Color color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public List<Position> getLegalMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();

        // Directions: horizontales, verticales et diagonales
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},  // Rook-like
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Bishop-like
        };

        for (int[] dir : directions) {
            int row = from.getRow();
            int col = from.getCol();

            while (true) {
                row += dir[0];
                col += dir[1];

                if (row < 0 || row > 7 || col < 0 || col > 7) {
                    break;
                }

                Position target = new Position(row, col);
                Piece targetPiece = board.getPieceAt(target);

                if (targetPiece == null) {
                    moves.add(target);
                } else {
                    if (targetPiece.getColor() != this.color) {
                        moves.add(target);
                    }
                    break;
                }
            }
        }

        return moves;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        // La reine se déplace comme une tour ou un fou
        boolean isStraight = from.getRow() == to.getRow() || from.getCol() == to.getCol();
        boolean isDiagonal = from.isDiagonal(to);

        if (!isStraight && !isDiagonal) {
            return false;
        }

        // Vérifie que le chemin est libre
        if (!isPathClear(from, to, board)) {
            return false;
        }

        // Vérifie la case de destination
        return isValidTarget(to, board);
    }
}