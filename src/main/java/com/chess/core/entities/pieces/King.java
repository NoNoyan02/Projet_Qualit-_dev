package com.chess.core.entities.pieces;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un roi.
 */
public class King extends Piece {

    public King(Color color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Position> getLegalMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();

        // Tous les mouvements possibles du roi (1 case dans toutes les directions)
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] dir : directions) {
            int newRow = from.getRow() + dir[0];
            int newCol = from.getCol() + dir[1];

            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                Position target = new Position(newRow, newCol);
                if (isValidTarget(target, board)) {
                    moves.add(target);
                }
            }
        }

        // Roque (à vérifier dans le use case)
        if (!hasMoved && !board.isKingInCheck(color)) {
            // Petit roque (côté roi)
            if (canCastleKingSide(from, board)) {
                moves.add(new Position(from.getRow(), from.getCol() + 2));
            }
            // Grand roque (côté dame)
            if (canCastleQueenSide(from, board)) {
                moves.add(new Position(from.getRow(), from.getCol() - 2));
            }
        }

        return moves;
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());

        // Mouvement normal (1 case)
        if (rowDiff <= 1 && colDiff <= 1) {
            return isValidTarget(to, board);
        }

        // Roque
        if (rowDiff == 0 && colDiff == 2 && !hasMoved) {
            if (colDiff == 2 && to.getCol() > from.getCol()) {
                return canCastleKingSide(from, board);
            } else if (colDiff == 2 && to.getCol() < from.getCol()) {
                return canCastleQueenSide(from, board);
            }
        }

        return false;
    }

    /**
     * Vérifie si le petit roque est possible.
     */
    private boolean canCastleKingSide(Position kingPos, Board board) {
        int row = kingPos.getRow();
        Position rookPos = new Position(row, 7);
        Piece rook = board.getPieceAt(rookPos);

        if (rook == null || rook.getType() != PieceType.ROOK || rook.hasMoved()) {
            return false;
        }

        // Vérifie que les cases entre le roi et la tour sont vides
        for (int col = kingPos.getCol() + 1; col < 7; col++) {
            if (board.getPieceAt(new Position(row, col)) != null) {
                return false;
            }
        }

        // Vérifie que le roi ne traverse pas une case attaquée
        for (int col = kingPos.getCol(); col <= kingPos.getCol() + 2; col++) {
            if (board.isSquareUnderAttack(new Position(row, col), color.opposite())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Vérifie si le grand roque est possible.
     */
    private boolean canCastleQueenSide(Position kingPos, Board board) {
        int row = kingPos.getRow();
        Position rookPos = new Position(row, 0);
        Piece rook = board.getPieceAt(rookPos);

        if (rook == null || rook.getType() != PieceType.ROOK || rook.hasMoved()) {
            return false;
        }

        // Vérifie que les cases entre le roi et la tour sont vides
        for (int col = 1; col < kingPos.getCol(); col++) {
            if (board.getPieceAt(new Position(row, col)) != null) {
                return false;
            }
        }

        // Vérifie que le roi ne traverse pas une case attaquée
        for (int col = kingPos.getCol() - 2; col <= kingPos.getCol(); col++) {
            if (board.isSquareUnderAttack(new Position(row, col), color.opposite())) {
                return false;
            }
        }

        return true;
    }
}