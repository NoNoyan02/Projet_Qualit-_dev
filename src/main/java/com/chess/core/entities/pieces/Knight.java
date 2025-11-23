package com.chess.core.entities.pieces;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un cavalier.
 */
public class Knight extends Piece {

    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Position> getLegalMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();

        // Tous les mouvements en L possibles du cavalier
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : knightMoves) {
            int newRow = from.getRow() + move[0];
            int newCol = from.getCol() + move[1];

            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                Position target = new Position(newRow, newCol);
                if (isValidTarget(target, board)) {
                    moves.add(target);
                }
            }
        }

        return moves;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());

        // Mouvement en L: (2,1) ou (1,2)
        boolean isLShape = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);

        if (!isLShape) {
            return false;
        }

        // Vérifie la case de destination
        return isValidTarget(to, board);
    }
}