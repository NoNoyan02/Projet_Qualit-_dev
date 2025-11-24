package com.chess.core.usecases;

import com.chess.core.entities.game.Board;
import com.chess.core.entities.game.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests complets pour toutes les conditions de nulle.
 */
class DrawDetectorUseCaseTest {

    private DrawDetectorUseCase detector;
    private GameState gameState;
    private Board board;

    @BeforeEach
    void setUp() {
        detector = new DrawDetectorUseCase();
        gameState = new GameState();
        board = gameState.getBoard();
    }

    @Test
    @DisplayName("1. Pat - Roi noir coincé sans échec")
    void testStalemate() {
        // Correct FEN for a stalemate: Black king at a3, White pawns at b2 and c2, White king at a1
        board.setupFromFen("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1");
        gameState.initializeFromFen("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1");

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertTrue(result.isDraw());
        assertEquals(DrawDetectorUseCase.DrawType.STALEMATE, result.getDrawType());
        assertTrue(result.isAutomatic());
    }

    @Test
    @DisplayName("2. Matériel insuffisant - Roi contre Roi")
    void testInsufficientMaterial_KingVsKing() {
        board.setupFromFen("8/8/8/3k4/8/8/8/3K4 w - - 0 1");
        gameState.initializeFromFen("8/8/8/3k4/8/8/8/3K4 w - - 0 1");

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertTrue(result.isDraw());
        assertEquals(DrawDetectorUseCase.DrawType.INSUFFICIENT_MATERIAL, result.getDrawType());
        assertTrue(result.isAutomatic());
    }

    @Test
    @DisplayName("2. Matériel insuffisant - Roi + Cavalier vs Roi")
    void testInsufficientMaterial_KingKnightVsKing() {
        board.setupFromFen("8/8/8/3k4/8/8/8/3KN3 w - - 0 1");
        gameState.initializeFromFen("8/8/8/3k4/8/8/8/3KN3 w - - 0 1");

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertTrue(result.isDraw());
        assertEquals(DrawDetectorUseCase.DrawType.INSUFFICIENT_MATERIAL, result.getDrawType());
    }

    @Test
    @DisplayName("2. Matériel insuffisant - Roi + Fou vs Roi")
    void testInsufficientMaterial_KingBishopVsKing() {
        board.setupFromFen("8/8/8/3k4/8/8/8/3KB3 w - - 0 1");
        gameState.initializeFromFen("8/8/8/3k4/8/8/8/3KB3 w - - 0 1");

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertTrue(result.isDraw());
        assertEquals(DrawDetectorUseCase.DrawType.INSUFFICIENT_MATERIAL, result.getDrawType());
    }

    @Test
    @DisplayName("2. Matériel insuffisant - Fous de même couleur")
    void testInsufficientMaterial_BishopsSameColor() {
        // Les deux fous sur cases claires
        board.setupFromFen("8/8/8/3kb3/8/8/8/3KB3 w - - 0 1");
        gameState.initializeFromFen("8/8/8/3kb3/8/8/8/3KB3 w - - 0 1");

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertTrue(result.isDraw());
        assertEquals(DrawDetectorUseCase.DrawType.INSUFFICIENT_MATERIAL, result.getDrawType());
    }

    @Test
    @DisplayName("2. PAS de nulle - Roi + Tour vs Roi (mat possible)")
    void testNoDrawWith_KingRookVsKing() {
        board.setupFromFen("8/8/8/3k4/8/8/8/3KR3 w - - 0 1");
        gameState.initializeFromFen("8/8/8/3k4/8/8/8/3KR3 w - - 0 1");

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertFalse(result.isDraw());
    }

    @Test
    @DisplayName("4. Règle des 50 coups - Exactement 100 demi-coups")
    void testFiftyMoveRule_Exactly100HalfMoves() {
        board.setupFromFen("8/8/8/3k4/8/8/8/3K4 w - - 100 1");
        gameState.initializeFromFen("8/8/8/3k4/8/8/8/3K4 w - - 100 1");

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertTrue(result.isDraw());
        assertEquals(DrawDetectorUseCase.DrawType.FIFTY_MOVE_RULE, result.getDrawType());
        assertFalse(result.isAutomatic()); // Sur réclamation
    }

//    @Test
//    @DisplayName("4. PAS de règle des 50 coups - Seulement 99 demi-coups")
//    void testNoFiftyMoveRule_Only99HalfMoves() {
//        board.setupFromFen("8/8/8/3k4/8/8/8/3K4 w - - 99 1");
//        gameState.initializeFromFen("8/8/8/3k4/8/8/8/3K4 w - - 99 1");
//
//        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);
//
//        assertFalse(result.isDraw());
//    }

//    @Test
//    @DisplayName("5. Triple répétition - Même position 3 fois")
//    void testThreefoldRepetition() {
//        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//
//        detector.recordPosition(fen); // 1ère
//        detector.recordPosition(fen); // 2ème
//
//        board.setupFromFen(fen);
//        gameState.initializeFromFen(fen);
//
//        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);
//
//        assertTrue(result.isDraw());
//        assertEquals(DrawDetectorUseCase.DrawType.THREEFOLD_REPETITION, result.getDrawType());
//        assertFalse(result.isAutomatic());
//    }

    @Test
    @DisplayName("5. PAS de triple répétition - Seulement 2 fois")
    void testNoThreefoldRepetition_Only2Times() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        detector.recordPosition(fen); // 1ère
        detector.recordPosition(fen); // 2ème

        board.setupFromFen(fen);
        gameState.initializeFromFen(fen);

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertFalse(result.isDraw());
    }

    @Test
    @DisplayName("6. Règle des 75 coups - 150 demi-coups (automatique)")
    void testSeventyFiveMoveRule() {
        board.setupFromFen("8/8/8/3k4/8/8/8/3K4 w - - 150 1");
        gameState.initializeFromFen("8/8/8/3k4/8/8/8/3K4 w - - 150 1");

        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);

        assertTrue(result.isDraw());
        assertEquals(DrawDetectorUseCase.DrawType.SEVENTY_FIVE_MOVE_RULE, result.getDrawType());
        assertTrue(result.isAutomatic()); // Automatique
    }

//    @Test
//    @DisplayName("7. Quintuple répétition - 5 fois (automatique)")
//    void testFivefoldRepetition() {
//        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//
//        for (int i = 0; i < 4; i++) {
//            detector.recordPosition(fen);
//        }
//
//        board.setupFromFen(fen);
//        gameState.initializeFromFen(fen);
//
//        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);
//
//        assertTrue(result.isDraw());
//        assertEquals(DrawDetectorUseCase.DrawType.FIVEFOLD_REPETITION, result.getDrawType());
//        assertTrue(result.isAutomatic());
//    }

    @Test
    @DisplayName("Vérifier le compteur de répétitions")
    void testRepetitionCount() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        detector.recordPosition(fen);
        assertEquals(1, detector.getRepetitionCount(fen));

        detector.recordPosition(fen);
        assertEquals(2, detector.getRepetitionCount(fen));

        detector.recordPosition(fen);
        assertEquals(3, detector.getRepetitionCount(fen));
    }

    @Test
    @DisplayName("Reset après capture réinitialise l'historique")
    void testResetAfterCapture() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        detector.recordPosition(fen);
        detector.recordPosition(fen);

        assertEquals(2, detector.getRepetitionCount(fen));

        // Reset (simule une capture)
        detector.reset();

        assertEquals(0, detector.getRepetitionCount(fen));
    }

    @Test
    @DisplayName("Peut réclamer la nulle - 50 coups")
    void testCanClaimDraw_FiftyMoves() {
        board.setupFromFen("8/8/8/3k4/8/8/8/3K4 w - - 100 1");
        gameState.initializeFromFen("8/8/8/3k4/8/8/8/3K4 w - - 100 1");

        assertTrue(detector.canClaimDraw(gameState));
    }

//    @Test
//    @DisplayName("Peut réclamer la nulle - Triple répétition")
//    void testCanClaimDraw_ThreefoldRepetition() {
//        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
//
//        detector.recordPosition(fen);
//        detector.recordPosition(fen);
//        detector.recordPosition(fen);
//
//        board.setupFromFen(fen);
//        gameState.initializeFromFen(fen);
//
//        assertTrue(detector.canClaimDraw(gameState));
//    }

    @Test
    @DisplayName("Ne peut PAS réclamer - Nulle automatique (pat)")
    void testCannotClaimDraw_AutomaticDraw() {
        // Pat = automatique, pas de réclamation
        String stalemateFen = "7k/5Q2/6K1/8/8/8/8/8 b - - 0 1";
        board.setupFromFen(stalemateFen);
        gameState.initializeFromFen(stalemateFen);

        // Le pat est détecté mais c'est automatique
        DrawDetectorUseCase.DrawResult result = detector.checkForDraw(gameState);
        assertTrue(result.isDraw());
        assertTrue(result.isAutomatic());

        // Donc canClaimDraw retourne false (car automatique)
        assertFalse(detector.canClaimDraw(gameState));
    }

    @Test
    @DisplayName("Position différente ne compte pas comme répétition")
    void testDifferentPositionNotRepetition() {
        String fen1 = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String fen2 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";

        detector.recordPosition(fen1);
        detector.recordPosition(fen2);
        detector.recordPosition(fen1);

        assertEquals(2, detector.getRepetitionCount(fen1));
        assertEquals(1, detector.getRepetitionCount(fen2));
    }
}