package com.chess.core.usecases;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.entities.pieces.Piece;
import com.chess.core.ports.MoveLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le use case MovePiece.
 * Démontre la testabilité grâce à la Clean Architecture.
 */
@ExtendWith(MockitoExtension.class)
class MovePieceInteractorTest {

    @Mock
    private MoveLogger moveLogger;

    private MovePieceInteractor movePieceInteractor;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        movePieceInteractor = new MovePieceInteractor(moveLogger);
        gameState = new GameState();
        gameState.initializeGame();
    }

    @Test
    @DisplayName("Déplacer un pion de e2 à e4 est valide")
    void execute_validPawnMove_shouldSucceed() {
        // Given
        Position from = Position.fromAlgebraic("e2");
        Position to = Position.fromAlgebraic("e4");

        // When
        Move move = movePieceInteractor.execute(gameState, from, to);

        // Then
        assertNotNull(move);
        assertEquals(from, move.getFrom());
        assertEquals(to, move.getTo());
        assertNull(gameState.getBoard().getPieceAt(from));
        assertNotNull(gameState.getBoard().getPieceAt(to));
        assertEquals(Color.BLACK, gameState.getActivePlayer());

        // Vérification du logging
        verify(moveLogger, times(1)).logMove(eq("current"), any(Move.class), eq(1));
    }

    @Test
    @DisplayName("Essayer de déplacer une pièce inexistante lance une exception")
    void execute_noPieceAtSource_shouldThrowException() {
        // Given
        Position from = Position.fromAlgebraic("e5"); // Case vide
        Position to = Position.fromAlgebraic("e6");

        // When & Then
        assertThatThrownBy(() -> movePieceInteractor.execute(gameState, from, to))
                .isInstanceOf(MovePieceUseCase.IllegalMoveException.class)
                .hasMessageContaining("Aucune pièce");

        verify(moveLogger, never()).logMove(any(), any(), anyInt());
    }

    @Test
    @DisplayName("Essayer de déplacer la pièce de l'adversaire lance une exception")
    void execute_wrongPlayerPiece_shouldThrowException() {
        // Given
        Position from = Position.fromAlgebraic("e7"); // Pion noir
        Position to = Position.fromAlgebraic("e5");

        // When & Then
        assertThatThrownBy(() -> movePieceInteractor.execute(gameState, from, to))
                .isInstanceOf(MovePieceUseCase.IllegalMoveException.class)
                .hasMessageContaining("Ce n'est pas le tour");
    }

    @Test
    @DisplayName("Un mouvement invalide lance une exception")
    void execute_invalidMove_shouldThrowException() {
        // Given
        Position from = Position.fromAlgebraic("e2");
        Position to = Position.fromAlgebraic("e5"); // Trop loin pour un pion

        // When & Then
        assertThatThrownBy(() -> movePieceInteractor.execute(gameState, from, to))
                .isInstanceOf(MovePieceUseCase.IllegalMoveException.class)
                .hasMessageContaining("Mouvement invalide");
    }

    @Test
    @DisplayName("isLegalMove retourne true pour un mouvement valide")
    void isLegalMove_validMove_shouldReturnTrue() {
        // Given
        Position from = Position.fromAlgebraic("e2");
        Position to = Position.fromAlgebraic("e4");

        // When
        boolean isLegal = movePieceInteractor.isLegalMove(gameState, from, to);

        // Then
        assertTrue(isLegal);
    }

    @Test
    @DisplayName("isLegalMove retourne false pour un mouvement invalide")
    void isLegalMove_invalidMove_shouldReturnFalse() {
        // Given
        Position from = Position.fromAlgebraic("e2");
        Position to = Position.fromAlgebraic("e5");

        // When
        boolean isLegal = movePieceInteractor.isLegalMove(gameState, from, to);

        // Then
        assertFalse(isLegal);
    }

    @Test
    @DisplayName("Une capture met à jour correctement le plateau")
    void execute_capture_shouldRemoveCapturedPiece() {
        // Given - Configuration d'une situation de capture
        gameState.initializeFromFen("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2");
        Position from = Position.fromAlgebraic("d2");
        Position to = Position.fromAlgebraic("d4");

        movePieceInteractor.execute(gameState, from, to);

        // Maintenant les noirs jouent
        from = Position.fromAlgebraic("e5");
        to = Position.fromAlgebraic("d4"); // Capture

        // When
        Move move = movePieceInteractor.execute(gameState, from, to);

        // Then
        assertTrue(move.isCapture());
        assertNotNull(move.getCapturedPiece());
    }

    @Test
    @DisplayName("Le compteur de coups est mis à jour correctement")
    void execute_multipleMoves_shouldUpdateMoveCounter() {
        // Given & When
        movePieceInteractor.execute(gameState,
                Position.fromAlgebraic("e2"),
                Position.fromAlgebraic("e4"));

        movePieceInteractor.execute(gameState,
                Position.fromAlgebraic("e7"),
                Position.fromAlgebraic("e5"));

        // Then
        assertEquals(2, gameState.getMoveCount());
        assertEquals(Color.WHITE, gameState.getActivePlayer());
    }

    @Test
    @DisplayName("Le use case peut être testé avec un mock")
    void execute_withMockedLogger_shouldWork() {
        // Given
        MoveLogger mockLogger = mock(MoveLogger.class);
        MovePieceInteractor interactor = new MovePieceInteractor(mockLogger);

        Position from = Position.fromAlgebraic("e2");
        Position to = Position.fromAlgebraic("e4");

        // When
        interactor.execute(gameState, from, to);

        // Then
        verify(mockLogger).logMove(eq("current"), any(Move.class), eq(1));
        verifyNoMoreInteractions(mockLogger);
    }
}