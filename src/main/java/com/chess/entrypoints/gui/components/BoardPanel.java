package com.chess.entrypoints.gui.components;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;
import com.chess.core.entities.game.GameSettings;
import com.chess.core.entities.game.Move;
import com.chess.core.entities.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panneau personnalisé pour afficher l'échiquier.
 */
public class BoardPanel extends JPanel {
    private static final int BOARD_SIZE = 640;
    private static final int SQUARE_SIZE = BOARD_SIZE / 8;

    private Board board;
    private GameSettings settings;
    private Position selectedSquare;
    private Position hoveredSquare;
    private List<Position> legalMoves;
    private Position draggedPiecePosition;
    private Point draggedPieceLocation;
    private Move lastMove;

    // Callbacks
    private MoveCallback onMoveAttempt;

    // Couleurs des thèmes
    private java.awt.Color lightSquareColor;
    private java.awt.Color darkSquareColor;
    private java.awt.Color highlightColor;
    private java.awt.Color legalMoveColor;

    public BoardPanel(Board board, GameSettings settings) {
        this.board = board;
        this.settings = settings;
        this.legalMoves = new ArrayList<>();

        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        setBackground(java.awt.Color.WHITE);

        applyTheme(settings.getBoardTheme());
        setupListeners();
    }

    private void applyTheme(GameSettings.BoardTheme theme) {
        switch (theme) {
            case GREEN:
                lightSquareColor = new java.awt.Color(238, 238, 210);
                darkSquareColor = new java.awt.Color(118, 150, 86);
                break;
            case BROWN:
                lightSquareColor = new java.awt.Color(240, 217, 181);
                darkSquareColor = new java.awt.Color(181, 136, 99);
                break;
            case BLUE:
                lightSquareColor = new java.awt.Color(222, 227, 230);
                darkSquareColor = new java.awt.Color(140, 162, 173);
                break;
            case GRAY:
                lightSquareColor = new java.awt.Color(200, 200, 200);
                darkSquareColor = new java.awt.Color(100, 100, 100);
                break;
            default:
                lightSquareColor = new java.awt.Color(238, 238, 210);
                darkSquareColor = new java.awt.Color(118, 150, 86);
        }

        highlightColor = new java.awt.Color(186, 202, 68);
        legalMoveColor = new java.awt.Color(0, 0, 0, 40);
    }

    private void setupListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void handleMousePressed(MouseEvent e) {
        Position clicked = getSquareAt(e.getPoint());
        if (clicked == null) return;

        Piece piece = board.getPieceAt(clicked);

        if (settings.getMoveMode() == GameSettings.MoveMode.DRAG_AND_DROP && piece != null) {
            draggedPiecePosition = clicked;
            draggedPieceLocation = e.getPoint();
            legalMoves = piece.getLegalMoves(clicked, board);
        } else if (settings.getMoveMode() == GameSettings.MoveMode.CLICK) {
            handleClickMode(clicked);
        } else {
            // Mode mixte
            if (piece != null) {
                draggedPiecePosition = clicked;
                draggedPieceLocation = e.getPoint();
                legalMoves = piece.getLegalMoves(clicked, board);
            } else {
                handleClickMode(clicked);
            }
        }

        repaint();
    }

    private void handleMouseReleased(MouseEvent e) {
        if (draggedPiecePosition != null) {
            Position target = getSquareAt(e.getPoint());

            if (target != null && !target.equals(draggedPiecePosition)) {
                attemptMove(draggedPiecePosition, target);
            }

            draggedPiecePosition = null;
            draggedPieceLocation = null;
            legalMoves.clear();
            repaint();
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (draggedPiecePosition != null) {
            draggedPieceLocation = e.getPoint();
            repaint();
        }
    }

    private void handleMouseMoved(MouseEvent e) {
        Position hovered = getSquareAt(e.getPoint());
        if (hovered != hoveredSquare) {
            hoveredSquare = hovered;
            repaint();
        }
    }

    private void handleClickMode(Position clicked) {
        if (selectedSquare == null) {
            Piece piece = board.getPieceAt(clicked);
            if (piece != null) {
                selectedSquare = clicked;
                legalMoves = piece.getLegalMoves(clicked, board);
            }
        } else {
            if (clicked.equals(selectedSquare)) {
                selectedSquare = null;
                legalMoves.clear();
            } else {
                attemptMove(selectedSquare, clicked);
                selectedSquare = null;
                legalMoves.clear();
            }
        }
    }

    private void attemptMove(Position from, Position to) {
        if (onMoveAttempt != null) {
            onMoveAttempt.onMove(from, to);
        }
    }

    private Position getSquareAt(Point point) {
        int col = point.x / SQUARE_SIZE;
        int row = 7 - (point.y / SQUARE_SIZE); // Inversé pour l'affichage

        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return new Position(row, col);
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBoard(g2d);
        drawCoordinates(g2d);
        drawHighlights(g2d);
        drawPieces(g2d);
        drawDraggedPiece(g2d);
    }

    private void drawBoard(Graphics2D g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isLight = (row + col) % 2 == 0;
                g.setColor(isLight ? lightSquareColor : darkSquareColor);

                int x = col * SQUARE_SIZE;
                int y = (7 - row) * SQUARE_SIZE;
                g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void drawCoordinates(Graphics2D g) {
        if (settings.getCoordinateDisplay() == GameSettings.CoordinateDisplay.NONE) {
            return;
        }

        g.setFont(new Font("Arial", Font.BOLD, 12));

        for (int i = 0; i < 8; i++) {
            // Colonnes (a-h)
            char col = (char) ('a' + i);
            boolean isLight = i % 2 == 0;
            g.setColor(isLight ? darkSquareColor : lightSquareColor);

            int x = i * SQUARE_SIZE + SQUARE_SIZE - 15;
            int y = 7 * SQUARE_SIZE + SQUARE_SIZE - 5;
            g.drawString(String.valueOf(col), x, y);

            // Rangées (1-8)
            String row = String.valueOf(8 - i);
            isLight = i % 2 == 0;
            g.setColor(isLight ? darkSquareColor : lightSquareColor);

            x = 5;
            y = i * SQUARE_SIZE + 17;
            g.drawString(row, x, y);
        }
    }

    private void drawHighlights(Graphics2D g) {
        // Dernier coup
        if (settings.isHighlightMoves() && lastMove != null) {
            g.setColor(highlightColor);
            drawSquareHighlight(g, lastMove.getFrom());
            drawSquareHighlight(g, lastMove.getTo());
        }

        // Case sélectionnée
        if (selectedSquare != null) {
            g.setColor(new java.awt.Color(186, 202, 68, 180));
            drawSquareHighlight(g, selectedSquare);
        }

        // Coups légaux
        if (settings.isShowLegalMoves() && !legalMoves.isEmpty()) {
            g.setColor(legalMoveColor);
            for (Position pos : legalMoves) {
                Piece target = board.getPieceAt(pos);
                if (target != null) {
                    // Capture - cercle sur le bord
                    drawCaptureIndicator(g, pos);
                } else {
                    // Case vide - petit cercle au centre
                    drawMoveIndicator(g, pos);
                }
            }
        }
    }

    private void drawSquareHighlight(Graphics2D g, Position pos) {
        int x = pos.getCol() * SQUARE_SIZE;
        int y = (7 - pos.getRow()) * SQUARE_SIZE;
        g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawMoveIndicator(Graphics2D g, Position pos) {
        int x = pos.getCol() * SQUARE_SIZE;
        int y = (7 - pos.getRow()) * SQUARE_SIZE;
        int centerX = x + SQUARE_SIZE / 2;
        int centerY = y + SQUARE_SIZE / 2;
        int radius = SQUARE_SIZE / 6;

        g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    private void drawCaptureIndicator(Graphics2D g, Position pos) {
        int x = pos.getCol() * SQUARE_SIZE;
        int y = (7 - pos.getRow()) * SQUARE_SIZE;
        int thickness = SQUARE_SIZE / 12;

        // Dessine un cadre épais
        g.setStroke(new BasicStroke(thickness));
        g.drawRect(x + thickness/2, y + thickness/2,
                SQUARE_SIZE - thickness, SQUARE_SIZE - thickness);
    }

    private void drawPieces(Graphics2D g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);

                // Ne pas dessiner la pièce en cours de déplacement
                if (pos.equals(draggedPiecePosition)) {
                    continue;
                }

                Piece piece = board.getPieceAt(pos);
                if (piece != null) {
                    int x = col * SQUARE_SIZE;
                    int y = (7 - row) * SQUARE_SIZE;
                    drawPiece(g, piece, x, y, SQUARE_SIZE);
                }
            }
        }
    }

    private void drawDraggedPiece(Graphics2D g) {
        if (draggedPiecePosition != null && draggedPieceLocation != null) {
            Piece piece = board.getPieceAt(draggedPiecePosition);
            if (piece != null) {
                int x = draggedPieceLocation.x - SQUARE_SIZE / 2;
                int y = draggedPieceLocation.y - SQUARE_SIZE / 2;
                drawPiece(g, piece, x, y, SQUARE_SIZE);
            }
        }
    }

    private void drawPiece(Graphics2D g, Piece piece, int x, int y, int size) {
        // Symboles Unicode des pièces
        String symbol = getPieceSymbol(piece);

        g.setFont(new Font("Arial Unicode MS", Font.PLAIN, size - 10));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(symbol);
        int textHeight = fm.getHeight();

        int textX = x + (size - textWidth) / 2;
        int textY = y + (size - textHeight) / 2 + fm.getAscent();

        // Ombre
        g.setColor(new java.awt.Color(0, 0, 0, 50));
        g.drawString(symbol, textX + 2, textY + 2);

        // Pièce
        g.setColor(piece.getColor() == Color.WHITE ?
                java.awt.Color.WHITE : java.awt.Color.BLACK);
        g.drawString(symbol, textX, textY);
    }

    private String getPieceSymbol(Piece piece) {
        boolean isWhite = piece.getColor() == Color.WHITE;

        return switch (piece.getType()) {
            case KING -> isWhite ? "♔" : "♚";
            case QUEEN -> isWhite ? "♕" : "♛";
            case ROOK -> isWhite ? "♖" : "♜";
            case BISHOP -> isWhite ? "♗" : "♝";
            case KNIGHT -> isWhite ? "♘" : "♞";
            case PAWN -> isWhite ? "♙" : "♟";
        };
    }

    public void setLastMove(Move move) {
        this.lastMove = move;
        repaint();
    }

    public void updateBoard(Board board) {
        this.board = board;
        repaint();
    }

    public void setOnMoveAttempt(MoveCallback callback) {
        this.onMoveAttempt = callback;
    }

    @FunctionalInterface
    public interface MoveCallback {
        void onMove(Position from, Position to);
    }
}