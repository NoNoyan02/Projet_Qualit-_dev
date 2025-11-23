package com.chess.core.entities.pieces;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un fou.
 */
public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public List<Position> getLegalMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();

        // Directions diagonales: haut-droite, haut-gauche, bas-droite, bas-gauche
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

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
        // Le fou se déplace en diagonale
        if (!from.isDiagonal(to)) {
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