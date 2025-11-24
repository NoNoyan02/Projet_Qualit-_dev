package com.chess.core.usecases;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;
import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.entities.pieces.*;
import com.chess.core.ports.MoveLogger;
import com.chess.core.ports.ChessEngine;
import com.chess.core.entities.player.Player;
import com.chess.core.entities.player.AIPlayer;

/**
 * Implémentation du cas d'usage pour déplacer une pièce.
 */
public class MovePieceInteractor implements MovePieceUseCase {
    private final MoveLogger moveLogger;
    private final ChessEngine chessEngine;

    public MovePieceInteractor(MoveLogger moveLogger, ChessEngine chessEngine) {
        this.moveLogger = moveLogger;
        this.chessEngine = chessEngine;
    }

    @Override
    public Move execute(GameState gameState, Position from, Position to) {
        return execute(gameState, from, to, null);
    }

    @Override
    public Move execute(GameState gameState, Position from, Position to, PieceType promotionPiece) {
        Board board = gameState.getBoard();
        Color activeColor = gameState.getActivePlayer();

        // Validation de base
        validateMove(gameState, from, to);

        Piece piece = board.getPieceAt(from);
        Piece capturedPiece = board.getPieceAt(to);

        // Création du mouvement
        Move.Builder moveBuilder = new Move.Builder(from, to, piece);

        if (capturedPiece != null) {
            moveBuilder.withCapturedPiece(capturedPiece);
        }

        // Détection des mouvements spéciaux
        Move.MoveType moveType = determineMoveType(piece, from, to, board);
        moveBuilder.withMoveType(moveType);

        // Gestion de la promotion
        if (piece.getType() == PieceType.PAWN && ((Pawn) piece).canPromote(to)) {
            if (promotionPiece == null) {
                promotionPiece = PieceType.QUEEN; // Par défaut
            }
            moveBuilder.withPromotion(promotionPiece);
        }

        Move move = moveBuilder.build();

        // Exécution du mouvement
        executeMove(move, board);

        // Mise à jour de l'état de la partie
        gameState.recordMove(move);
        updateGameState(gameState, move);
        gameState.switchPlayer();

        // Logging
        if (moveLogger != null) {
            moveLogger.logMove("current", move, gameState.getMoveCount());
        }

        return move;
    }

    @Override
    public boolean isLegalMove(GameState gameState, Position from, Position to) {
        try {
            validateMove(gameState, from, to);
            return true;
        } catch (IllegalMoveException e) {
            return false;
        }
    }

    /**
     * Valide un mouvement avant de l'exécuter.
     */
    private void validateMove(GameState gameState, Position from, Position to) {
        Board board = gameState.getBoard();
        Piece piece = board.getPieceAt(from);

        // Vérification de la pièce
        if (piece == null) {
            throw new IllegalMoveException("Aucune pièce à la position " + from);
        }

        // Vérification du tour
        if (piece.getColor() != gameState.getActivePlayer()) {
            throw new IllegalMoveException("Ce n'est pas le tour de " + piece.getColor());
        }

        // Vérification de la validité du mouvement
        if (!piece.isValidMove(from, to, board)) {
            throw new IllegalMoveException("Mouvement invalide de " + from + " à " + to);
        }

        // Vérification que le mouvement ne met pas le roi en échec
        if (wouldLeaveKingInCheck(board, from, to, piece.getColor())) {
            throw new IllegalMoveException("Ce mouvement mettrait le roi en échec");
        }
    }

    /**
     * Détermine le type de mouvement.
     */
    private Move.MoveType determineMoveType(Piece piece, Position from, Position to, Board board) {
        // Roque
        if (piece.getType() == PieceType.KING && Math.abs(to.getCol() - from.getCol()) == 2) {
            return to.getCol() > from.getCol() ?
                    Move.MoveType.CASTLING_KING_SIDE : Move.MoveType.CASTLING_QUEEN_SIDE;
        }

        // Prise en passant
        if (piece.getType() == PieceType.PAWN) {
            if (board.getEnPassantTarget() != null && to.equals(board.getEnPassantTarget())) {
                return Move.MoveType.EN_PASSANT;
            }
        }

        // Capture normale
        if (board.getPieceAt(to) != null) {
            return Move.MoveType.CAPTURE;
        }

        return Move.MoveType.NORMAL;
    }

    /**
     * Exécute physiquement le mouvement sur le plateau.
     */
    private void executeMove(Move move, Board board) {
        Position from = move.getFrom();
        Position to = move.getTo();
        Piece piece = board.getPieceAt(from);

        // Mouvement standard
        board.removePiece(from);

        // Gestion de la promotion
        if (move.isPromotion()) {
            Piece promotedPiece = createPiece(move.getPromotionPiece(), piece.getColor());
            board.placePiece(promotedPiece, to);
        } else {
            board.placePiece(piece, to);
        }

        piece.setHasMoved(true);

        // Gestion du roque
        if (move.isCastling()) {
            executeCastling(move, board);
        }

        // Gestion de la prise en passant
        if (move.getMoveType() == Move.MoveType.EN_PASSANT) {
            int capturedPawnRow = piece.getColor() == Color.WHITE ? to.getRow() - 1 : to.getRow() + 1;
            board.removePiece(new Position(capturedPawnRow, to.getCol()));
        }

        // Mise à jour de la case de prise en passant
        updateEnPassantTarget(move, board, piece);

        // Mise à jour des droits de roque
        updateCastlingRights(move, board);
    }

    /**
     * Exécute un roque.
     */
    private void executeCastling(Move move, Board board) {
        int row = move.getFrom().getRow();

        if (move.getMoveType() == Move.MoveType.CASTLING_KING_SIDE) {
            // Petit roque
            Piece rook = board.removePiece(new Position(row, 7));
            board.placePiece(rook, new Position(row, 5));
            rook.setHasMoved(true);
        } else {
            // Grand roque
            Piece rook = board.removePiece(new Position(row, 0));
            board.placePiece(rook, new Position(row, 3));
            rook.setHasMoved(true);
        }
    }

    /**
     * Met à jour la case de prise en passant.
     */
    private void updateEnPassantTarget(Move move, Board board, Piece piece) {
        if (piece.getType() == PieceType.PAWN) {
            int rowDiff = Math.abs(move.getTo().getRow() - move.getFrom().getRow());
            if (rowDiff == 2) {
                int targetRow = (move.getFrom().getRow() + move.getTo().getRow()) / 2;
                board.setEnPassantTarget(new Position(targetRow, move.getFrom().getCol()));
                return;
            }
        }
        board.setEnPassantTarget(null);
    }

    /**
     * Met à jour les droits de roque.
     */
    private void updateCastlingRights(Move move, Board board) {
        Piece piece = board.getPieceAt(move.getTo());

        if (piece.getType() == PieceType.KING) {
            board.getCastlingRights(piece.getColor()).disableAll();
        } else if (piece.getType() == PieceType.ROOK) {
            Board.CastlingRights rights = board.getCastlingRights(piece.getColor());
            if (move.getFrom().getCol() == 0) {
                rights.setQueenSide(false);
            } else if (move.getFrom().getCol() == 7) {
                rights.setKingSide(false);
            }
        }
    }

    /**
     * Vérifie si un mouvement mettrait le roi en échec.
     */
    private boolean wouldLeaveKingInCheck(Board board, Position from, Position to, Color color) {
        // Simulation du mouvement
        Board tempBoard = board.copy();
        Piece piece = tempBoard.removePiece(from);
        tempBoard.placePiece(piece, to);

        return tempBoard.isKingInCheck(color);
    }

    /**
     * Met à jour l'état de la partie après un mouvement.
     */
    private void updateGameState(GameState gameState, Move move) {
        Board board = gameState.getBoard();

        // Mise à jour du compteur de demi-coups
        if (move.isCapture() || move.getMovedPiece().getType() == PieceType.PAWN) {
            board.resetHalfMoveClock();
        } else {
            board.incrementHalfMoveClock();
        }

        // Mise à jour du numéro de coup
        if (gameState.getActivePlayer() == Color.BLACK) {
            board.incrementFullMoveNumber();
        }

        // Vérification de l'échec et du mat
        Color nextPlayer = gameState.getActivePlayer().opposite();
        if (board.isKingInCheck(nextPlayer)) {
            if (isCheckmate(board, nextPlayer)) {
                gameState.updateStatus(GameState.GameStatus.CHECKMATE);
            } else {
                gameState.updateStatus(GameState.GameStatus.CHECK);
            }
        } else if (isStalemate(board, nextPlayer)) {
            gameState.updateStatus(GameState.GameStatus.STALEMATE);
        }
    }

    /**
     * Vérifie si c'est un échec et mat.
     */
    private boolean isCheckmate(Board board, Color color) {
        return board.isKingInCheck(color) && !hasLegalMoves(board, color);
    }

    /**
     * Vérifie si c'est un pat.
     */
    private boolean isStalemate(Board board, Color color) {
        return !board.isKingInCheck(color) && !hasLegalMoves(board, color);
    }

    /**
     * Vérifie si un joueur a des coups légaux.
     */
    private boolean hasLegalMoves(Board board, Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position from = new Position(row, col);
                Piece piece = board.getPieceAt(from);

                if (piece != null && piece.getColor() == color) {
                    for (Position to : piece.getLegalMoves(from, board)) {
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
     * Crée une pièce selon son type et sa couleur.
     */
    private Piece createPiece(PieceType type, Color color) {
        return switch (type) {
            case KING -> new King(color);
            case QUEEN -> new Queen(color);
            case ROOK -> new Rook(color);
            case BISHOP -> new Bishop(color);
            case KNIGHT -> new Knight(color);
            case PAWN -> new Pawn(color);
        };
    }
}