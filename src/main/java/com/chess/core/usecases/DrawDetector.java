package com.chess.core.usecases;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;
import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.entities.pieces.Piece;
import com.chess.core.entities.pieces.PieceType;

import java.util.*;

/**
 * Détecte toutes les conditions de nulle (draw) aux échecs.
 */
public class DrawDetector {

    /**
     * Types de nulle possibles.
     */
    public enum DrawType {
        STALEMATE("Pat - Aucun coup légal"),
        FIFTY_MOVE_RULE("Règle des 50 coups"),
        THREEFOLD_REPETITION("Triple répétition"),
        INSUFFICIENT_MATERIAL("Matériel insuffisant"),
        AGREEMENT("Nulle acceptée"),
        FIVEFOLD_REPETITION("Quintuple répétition (automatique)"),
        SEVENTY_FIVE_MOVE_RULE("Règle des 75 coups (automatique)"),
        DEAD_POSITION("Position morte");

        private final String description;

        DrawType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Résultat de la détection de nulle.
     */
    public static class DrawResult {
        private final boolean isDraw;
        private final DrawType drawType;
        private final boolean isAutomatic; // Nulle automatique ou sur réclamation

        public DrawResult(boolean isDraw, DrawType drawType, boolean isAutomatic) {
            this.isDraw = isDraw;
            this.drawType = drawType;
            this.isAutomatic = isAutomatic;
        }

        public static DrawResult noDraw() {
            return new DrawResult(false, null, false);
        }

        public static DrawResult draw(DrawType type, boolean automatic) {
            return new DrawResult(true, type, automatic);
        }

        public boolean isDraw() { return isDraw; }
        public DrawType getDrawType() { return drawType; }
        public boolean isAutomatic() { return isAutomatic; }
    }

    private final Map<String, Integer> positionHistory; // FEN -> count

    public DrawDetector() {
        this.positionHistory = new HashMap<>();
    }

    /**
     * Vérifie toutes les conditions de nulle.
     */
    public DrawResult checkForDraw(GameState gameState) {
        Board board = gameState.getBoard();
        Color activeColor = gameState.getActivePlayer();

        // 1. Pat (Stalemate) - Aucun coup légal sans être en échec
        if (isStalemate(board, activeColor)) {
            return DrawResult.draw(DrawType.STALEMATE, true);
        }

        // 2. Matériel insuffisant
        if (hasInsufficientMaterial(board)) {
            return DrawResult.draw(DrawType.INSUFFICIENT_MATERIAL, true);
        }

        // 3. Position morte (Dead Position)
        if (isDeadPosition(board)) {
            return DrawResult.draw(DrawType.DEAD_POSITION, true);
        }

        // 4. Règle des 75 coups (automatique)
        if (board.getHalfMoveClock() >= 150) { // 75 coups complets = 150 demi-coups
            return DrawResult.draw(DrawType.SEVENTY_FIVE_MOVE_RULE, true);
        }

        // 5. Quintuple répétition (automatique)
        String currentFen = board.toFen(activeColor);
        recordPosition(currentFen);
        int repetitions = positionHistory.getOrDefault(currentFen, 0);
        if (repetitions >= 5) {
            return DrawResult.draw(DrawType.FIVEFOLD_REPETITION, true);
        }

        // 6. Règle des 50 coups (sur réclamation)
        if (board.getHalfMoveClock() >= 100) { // 50 coups complets = 100 demi-coups
            return DrawResult.draw(DrawType.FIFTY_MOVE_RULE, false);
        }

        // 7. Triple répétition (sur réclamation)
        if (repetitions >= 3) {
            return DrawResult.draw(DrawType.THREEFOLD_REPETITION, false);
        }

        return DrawResult.noDraw();
    }

    /**
     * 1. PAT (Stalemate) : Aucun coup légal sans être en échec.
     */
    private boolean isStalemate(Board board, Color color) {
        // Le roi ne doit pas être en échec
        if (board.isKingInCheck(color)) {
            return false;
        }

        // Aucun coup légal disponible
        return !hasAnyLegalMove(board, color);
    }

    /**
     * 2. MATÉRIEL INSUFFISANT : Impossible de mater.
     */
    private boolean hasInsufficientMaterial(Board board) {
        List<Piece> whitePieces = getPieces(board, Color.WHITE);
        List<Piece> blackPieces = getPieces(board, Color.BLACK);

        // Roi contre Roi
        if (whitePieces.size() == 1 && blackPieces.size() == 1) {
            return true;
        }

        // Roi contre Roi + Cavalier
        if (isKingVsKingAndKnight(whitePieces, blackPieces) ||
                isKingVsKingAndKnight(blackPieces, whitePieces)) {
            return true;
        }

        // Roi contre Roi + Fou
        if (isKingVsKingAndBishop(whitePieces, blackPieces) ||
                isKingVsKingAndBishop(blackPieces, whitePieces)) {
            return true;
        }

        // Roi + Fou contre Roi + Fou (même couleur de case)
        if (isKingBishopVsKingBishop(board, whitePieces, blackPieces)) {
            return true;
        }

        return false;
    }

    private boolean isKingVsKingAndKnight(List<Piece> pieces1, List<Piece> pieces2) {
        return pieces1.size() == 1 &&
                pieces2.size() == 2 &&
                pieces2.stream().anyMatch(p -> p.getType() == PieceType.KNIGHT);
    }

    private boolean isKingVsKingAndBishop(List<Piece> pieces1, List<Piece> pieces2) {
        return pieces1.size() == 1 &&
                pieces2.size() == 2 &&
                pieces2.stream().anyMatch(p -> p.getType() == PieceType.BISHOP);
    }

    private boolean isKingBishopVsKingBishop(Board board, List<Piece> white, List<Piece> black) {
        if (white.size() != 2 || black.size() != 2) {
            return false;
        }

        Piece whiteBishop = white.stream()
                .filter(p -> p.getType() == PieceType.BISHOP)
                .findFirst().orElse(null);

        Piece blackBishop = black.stream()
                .filter(p -> p.getType() == PieceType.BISHOP)
                .findFirst().orElse(null);

        if (whiteBishop == null || blackBishop == null) {
            return false;
        }

        // Trouver les positions des fous
        Position whitePos = findPiecePosition(board, whiteBishop);
        Position blackPos = findPiecePosition(board, blackBishop);

        if (whitePos == null || blackPos == null) {
            return false;
        }

        // Même couleur de case (la somme row+col est paire ou impaire)
        boolean whiteLightSquare = (whitePos.getRow() + whitePos.getCol()) % 2 == 0;
        boolean blackLightSquare = (blackPos.getRow() + blackPos.getCol()) % 2 == 0;

        return whiteLightSquare == blackLightSquare;
    }

    /**
     * 3. POSITION MORTE : Aucune suite de coups légaux ne peut mener au mat.
     */
    private boolean isDeadPosition(Board board) {
        // Pour simplifier, on vérifie quelques cas évidents
        // Une implémentation complète nécessiterait une analyse plus approfondie

        List<Piece> whitePieces = getPieces(board, Color.WHITE);
        List<Piece> blackPieces = getPieces(board, Color.BLACK);

        // Seulement des rois
        if (whitePieces.size() == 1 && blackPieces.size() == 1) {
            return true;
        }

        // Roi + cavaliers contre Roi (position bloquée)
        boolean whiteOnlyKnights = whitePieces.stream()
                .filter(p -> p.getType() != PieceType.KING)
                .allMatch(p -> p.getType() == PieceType.KNIGHT);

        boolean blackOnlyKing = blackPieces.size() == 1;

        if (whiteOnlyKnights && blackOnlyKing && whitePieces.size() <= 3) {
            // Vérifier si les cavaliers sont bloqués (implémentation simplifiée)
            return false; // À améliorer avec analyse de blocage
        }

        return false;
    }

    /**
     * Enregistre une position pour la détection de répétition.
     */
    public void recordPosition(String fen) {
        // On ne garde que la partie position (sans compteurs)
        String positionPart = extractPositionPart(fen);
        positionHistory.put(positionPart, positionHistory.getOrDefault(positionPart, 0) + 1);
    }

    /**
     * Réinitialise l'historique des positions.
     */
    public void reset() {
        positionHistory.clear();
    }

    /**
     * Extrait la partie position du FEN (sans compteurs de coups).
     */
    private String extractPositionPart(String fen) {
        // Format FEN: position active castling enpassant halfmove fullmove
        // On garde: position active castling enpassant
        String[] parts = fen.split(" ");
        if (parts.length >= 4) {
            return String.join(" ", parts[0], parts[1], parts[2], parts[3]);
        }
        return fen;
    }

    /**
     * Vérifie si un joueur a au moins un coup légal.
     */
    private boolean hasAnyLegalMove(Board board, Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position from = new Position(row, col);
                Piece piece = board.getPieceAt(from);

                if (piece != null && piece.getColor() == color) {
                    List<Position> moves = piece.getLegalMoves(from, board);
                    for (Position to : moves) {
                        if (!wouldLeaveKingInCheck(board, from, to, color)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si un mouvement mettrait le roi en échec.
     */
    private boolean wouldLeaveKingInCheck(Board board, Position from, Position to, Color color) {
        Board tempBoard = board.copy();
        Piece piece = tempBoard.removePiece(from);
        tempBoard.placePiece(piece, to);
        return tempBoard.isKingInCheck(color);
    }

    /**
     * Récupère toutes les pièces d'une couleur.
     */
    private List<Piece> getPieces(Board board, Color color) {
        List<Piece> pieces = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                if (piece != null && piece.getColor() == color) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    /**
     * Trouve la position d'une pièce spécifique sur le plateau.
     */
    private Position findPiecePosition(Board board, Piece targetPiece) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);
                if (piece != null &&
                        piece.getType() == targetPiece.getType() &&
                        piece.getColor() == targetPiece.getColor()) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Obtient le nombre de répétitions de la position actuelle.
     */
    public int getRepetitionCount(String fen) {
        String positionPart = extractPositionPart(fen);
        return positionHistory.getOrDefault(positionPart, 0);
    }

    /**
     * Peut réclamer la nulle (50 coups ou triple répétition).
     */
    public boolean canClaimDraw(GameState gameState) {
        DrawResult result = checkForDraw(gameState);
        return result.isDraw() && !result.isAutomatic();
    }
}