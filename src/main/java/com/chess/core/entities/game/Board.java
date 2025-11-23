package com.chess.core.entities.game;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.pieces.*;

import java.util.*;

/**
 * Représente le plateau d'échecs 8x8 avec support de la notation FEN.
 */
public class Board {
    private final Tile[][] tiles;
    private Position enPassantTarget;
    private final Map<Color, CastlingRights> castlingRights;
    private int halfMoveClock; // Pour la règle des 50 coups
    private int fullMoveNumber;

    public Board() {
        this.tiles = new Tile[8][8];
        this.castlingRights = new EnumMap<>(Color.class);
        initializeEmptyBoard();
    }

    /**
     * Initialise un plateau vide.
     */
    private void initializeEmptyBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                tiles[row][col] = new Tile(new Position(row, col));
            }
        }
        castlingRights.put(Color.WHITE, new CastlingRights());
        castlingRights.put(Color.BLACK, new CastlingRights());
        halfMoveClock = 0;
        fullMoveNumber = 1;
    }

    /**
     * Configure le plateau avec la position initiale standard.
     */
    public void setupInitialPosition() {
        setupFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    /**
     * Configure le plateau à partir d'une chaîne FEN.
     */
    public void setupFromFen(String fen) {
        String[] parts = fen.split(" ");
        if (parts.length < 4) {
            throw new IllegalArgumentException("FEN invalide: " + fen);
        }

        // 1. Position des pièces
        setupPiecesFromFen(parts[0]);

        // 2. Tour actif (w/b) - géré dans GameState

        // 3. Droits de roque
        setupCastlingRights(parts[2]);

        // 4. Case de prise en passant
        if (!parts[3].equals("-")) {
            enPassantTarget = Position.fromAlgebraic(parts[3]);
        } else {
            enPassantTarget = null;
        }

        // 5. Compteur de demi-coups
        if (parts.length > 4) {
            halfMoveClock = Integer.parseInt(parts[4]);
        }

        // 6. Numéro de coup complet
        if (parts.length > 5) {
            fullMoveNumber = Integer.parseInt(parts[5]);
        }
    }

    /**
     * Configure les pièces à partir de la partie FEN de la position.
     */
    private void setupPiecesFromFen(String piecePlacement) {
        String[] ranks = piecePlacement.split("/");
        for (int row = 7; row >= 0; row--) {
            int col = 0;
            String rank = ranks[7 - row];

            for (char c : rank.toCharArray()) {
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    Color color = Character.isUpperCase(c) ? Color.WHITE : Color.BLACK;
                    PieceType type = PieceType.fromFen(c);
                    Piece piece = createPiece(type, color);
                    placePiece(piece, new Position(row, col));
                    col++;
                }
            }
        }
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

    /**
     * Configure les droits de roque à partir de la notation FEN.
     */
    private void setupCastlingRights(String castlingStr) {
        CastlingRights white = new CastlingRights();
        CastlingRights black = new CastlingRights();

        if (!castlingStr.equals("-")) {
            white.setKingSide(castlingStr.contains("K"));
            white.setQueenSide(castlingStr.contains("Q"));
            black.setKingSide(castlingStr.contains("k"));
            black.setQueenSide(castlingStr.contains("q"));
        }

        castlingRights.put(Color.WHITE, white);
        castlingRights.put(Color.BLACK, black);
    }

    /**
     * Convertit le plateau en notation FEN.
     */
    public String toFen(Color activeColor) {
        StringBuilder fen = new StringBuilder();

        // 1. Position des pièces
        for (int row = 7; row >= 0; row--) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = getPieceAt(new Position(row, col));
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece.toFen());
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (row > 0) {
                fen.append('/');
            }
        }

        // 2. Tour actif
        fen.append(' ').append(activeColor.toFen());

        // 3. Droits de roque
        fen.append(' ').append(getCastlingRightsString());

        // 4. Case de prise en passant
        fen.append(' ').append(enPassantTarget != null ? enPassantTarget.toAlgebraic() : "-");

        // 5. Compteur de demi-coups
        fen.append(' ').append(halfMoveClock);

        // 6. Numéro de coup complet
        fen.append(' ').append(fullMoveNumber);

        return fen.toString();
    }

    /**
     * Génère la chaîne des droits de roque pour la notation FEN.
     */
    private String getCastlingRightsString() {
        StringBuilder castling = new StringBuilder();
        CastlingRights white = castlingRights.get(Color.WHITE);
        CastlingRights black = castlingRights.get(Color.BLACK);

        if (white.canCastleKingSide()) castling.append('K');
        if (white.canCastleQueenSide()) castling.append('Q');
        if (black.canCastleKingSide()) castling.append('k');
        if (black.canCastleQueenSide()) castling.append('q');

        return castling.length() > 0 ? castling.toString() : "-";
    }

    public Piece getPieceAt(Position position) {
        if (!position.isValid()) {
            return null;
        }
        return tiles[position.getRow()][position.getCol()].getPiece();
    }

    public void placePiece(Piece piece, Position position) {
        tiles[position.getRow()][position.getCol()].setPiece(piece);
    }

    public Piece removePiece(Position position) {
        return tiles[position.getRow()][position.getCol()].removePiece();
    }

    public Tile getTile(Position position) {
        return tiles[position.getRow()][position.getCol()];
    }

    /**
     * Trouve la position du roi d'une couleur donnée.
     */
    public Position findKing(Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = tiles[row][col].getPiece();
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == color) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    /**
     * Vérifie si une case est attaquée par une couleur donnée.
     */
    public boolean isSquareUnderAttack(Position position, Color attackingColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = tiles[row][col].getPiece();
                if (piece != null && piece.getColor() == attackingColor) {
                    Position from = new Position(row, col);
                    if (piece.isValidMove(from, position, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si le roi d'une couleur est en échec.
     */
    public boolean isKingInCheck(Color kingColor) {
        Position kingPosition = findKing(kingColor);
        return kingPosition != null && isSquareUnderAttack(kingPosition, kingColor.opposite());
    }

    /**
     * Retourne une copie du plateau.
     */
    public Board copy() {
        Board copy = new Board();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = this.tiles[row][col].getPiece();
                if (piece != null) {
                    copy.tiles[row][col].setPiece(createPiece(piece.getType(), piece.getColor()));
                }
            }
        }
        copy.enPassantTarget = this.enPassantTarget;
        copy.castlingRights.putAll(this.castlingRights);
        copy.halfMoveClock = this.halfMoveClock;
        copy.fullMoveNumber = this.fullMoveNumber;
        return copy;
    }

    // Getters et setters
    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Position enPassantTarget) {
        this.enPassantTarget = enPassantTarget;
    }

    public CastlingRights getCastlingRights(Color color) {
        return castlingRights.get(color);
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public void setHalfMoveClock(int halfMoveClock) {
        this.halfMoveClock = halfMoveClock;
    }

    public void incrementHalfMoveClock() {
        this.halfMoveClock++;
    }

    public void resetHalfMoveClock() {
        this.halfMoveClock = 0;
    }

    public int getFullMoveNumber() {
        return fullMoveNumber;
    }

    public void incrementFullMoveNumber() {
        this.fullMoveNumber++;
    }

    /**
     * Classe représentant les droits de roque.
     */
    public static class CastlingRights {
        private boolean kingSide;
        private boolean queenSide;

        public CastlingRights() {
            this.kingSide = true;
            this.queenSide = true;
        }

        public boolean canCastleKingSide() {
            return kingSide;
        }

        public void setKingSide(boolean kingSide) {
            this.kingSide = kingSide;
        }

        public boolean canCastleQueenSide() {
            return queenSide;
        }

        public void setQueenSide(boolean queenSide) {
            this.queenSide = queenSide;
        }

        public void disableAll() {
            this.kingSide = false;
            this.queenSide = false;
        }
    }
}