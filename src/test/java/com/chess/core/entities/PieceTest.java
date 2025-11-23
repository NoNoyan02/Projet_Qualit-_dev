package com.chess.core.entities.pieces;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les pièces d'échecs.
 */
class PieceTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    @DisplayName("Un pion blanc peut avancer d'une case")
    void pawn_canMoveOneSquareForward() {
        // Given
        Pawn whitePawn = new Pawn(Color.WHITE);
        Position start = new Position(1, 4); // e2
        board.placePiece(whitePawn, start);

        // When
        List<Position> moves = whitePawn.getLegalMoves(start, board);

        // Then
        assertTrue(moves.contains(new Position(2, 4))); // e3
    }

    @Test
    @DisplayName("Un pion peut avancer de deux cases au premier coup")
    void pawn_canMoveTwoSquaresOnFirstMove() {
        // Given
        Pawn whitePawn = new Pawn(Color.WHITE);
        Position start = new Position(1, 4); // e2
        board.placePiece(whitePawn, start);

        // When
        List<Position> moves = whitePawn.getLegalMoves(start, board);

        // Then
        assertTrue(moves.contains(new Position(3, 4))); // e4
    }

    @Test
    @DisplayName("Un cavalier se déplace en L")
    void knight_movesInLShape() {
        // Given
        Knight knight = new Knight(Color.WHITE);
        Position start = new Position(3, 3); // d4
        board.placePiece(knight, start);

        // When
        List<Position> moves = knight.getLegalMoves(start, board);

        // Then
        assertEquals(8, moves.size());
        assertTrue(moves.contains(new Position(5, 4))); // e6
        assertTrue(moves.contains(new Position(5, 2))); // c6
        assertTrue(moves.contains(new Position(4, 5))); // f5
    }

    @Test
    @DisplayName("Une tour se déplace horizontalement et verticalement")
    void rook_movesHorizontallyAndVertically() {
        // Given
        Rook rook = new Rook(Color.WHITE);
        Position start = new Position(3, 3); // d4
        board.placePiece(rook, start);

        // When
        List<Position> moves = rook.getLegalMoves(start, board);

        // Then
        assertEquals(14, moves.size()); // 7 + 7 (sans obstacles)
    }

    @Test
    @DisplayName("Un fou se déplace en diagonale")
    void bishop_movesDiagonally() {
        // Given
        Bishop bishop = new Bishop(Color.WHITE);
        Position start = new Position(3, 3); // d4
        board.placePiece(bishop, start);

        // When
        List<Position> moves = bishop.getLegalMoves(start, board);

        // Then
        assertEquals(13, moves.size());
        assertTrue(moves.contains(new Position(6, 6))); // g7
        assertTrue(moves.contains(new Position(0, 0))); // a1
    }

    @Test
    @DisplayName("La reine combine les mouvements de la tour et du fou")
    void queen_combinesRookAndBishopMoves() {
        // Given
        Queen queen = new Queen(Color.WHITE);
        Position start = new Position(3, 3); // d4
        board.placePiece(queen, start);

        // When
        List<Position> moves = queen.getLegalMoves(start, board);

        // Then
        assertEquals(27, moves.size()); // 14 (tour) + 13 (fou)
    }

    @Test
    @DisplayName("Le roi ne peut se déplacer que d'une case")
    void king_movesOneSquare() {
        // Given
        King king = new King(Color.WHITE);
        Position start = new Position(3, 3); // d4
        board.placePiece(king, start);

        // When
        List<Position> moves = king.getLegalMoves(start, board);

        // Then
        assertEquals(8, moves.size());
    }

    @Test
    @DisplayName("Les pièces ne peuvent pas capturer leurs propres pièces")
    void pieces_cannotCaptureFriendlyPieces() {
        // Given
        Rook rook = new Rook(Color.WHITE);
        Pawn friendlyPawn = new Pawn(Color.WHITE);
        Position rookPos = new Position(3, 3);
        Position pawnPos = new Position(3, 5);

        board.placePiece(rook, rookPos);
        board.placePiece(friendlyPawn, pawnPos);

        // When
        List<Position> moves = rook.getLegalMoves(rookPos, board);

        // Then
        assertFalse(moves.contains(pawnPos));
    }

    @Test
    @DisplayName("Les pièces peuvent capturer les pièces ennemies")
    void pieces_canCaptureEnemyPieces() {
        // Given
        Rook whiteRook = new Rook(Color.WHITE);
        Pawn blackPawn = new Pawn(Color.BLACK);
        Position rookPos = new Position(3, 3);
        Position pawnPos = new Position(3, 5);

        board.placePiece(whiteRook, rookPos);
        board.placePiece(blackPawn, pawnPos);

        // When
        List<Position> moves = whiteRook.getLegalMoves(rookPos, board);

        // Then
        assertTrue(moves.contains(pawnPos));
    }

    @Test
    @DisplayName("Les pièces ne peuvent pas traverser d'autres pièces (sauf le cavalier)")
    void pieces_cannotJumpOverOthers() {
        // Given
        Rook rook = new Rook(Color.WHITE);
        Pawn blockingPawn = new Pawn(Color.WHITE);
        Position rookPos = new Position(3, 3);
        Position pawnPos = new Position(3, 4);
        Position beyondPawn = new Position(3, 5);

        board.placePiece(rook, rookPos);
        board.placePiece(blockingPawn, pawnPos);

        // When
        List<Position> moves = rook.getLegalMoves(rookPos, board);

        // Then
        assertFalse(moves.contains(beyondPawn));
    }

    @Test
    @DisplayName("Test de la notation FEN pour les pièces")
    void pieces_haveFenNotation() {
        assertEquals('K', new King(Color.WHITE).toFen());
        assertEquals('k', new King(Color.BLACK).toFen());
        assertEquals('Q', new Queen(Color.WHITE).toFen());
        assertEquals('q', new Queen(Color.BLACK).toFen());
        assertEquals('R', new Rook(Color.WHITE).toFen());
        assertEquals('r', new Rook(Color.BLACK).toFen());
    }
}