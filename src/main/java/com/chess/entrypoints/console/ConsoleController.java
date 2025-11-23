package com.chess.entrypoints.console;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Board;
import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.entities.pieces.Piece;
import com.chess.core.entities.pieces.PieceType;
import com.chess.core.entities.player.Player;
import com.chess.core.usecases.GetBestMoveUseCase;
import com.chess.core.usecases.MovePieceUseCase;

import java.util.Scanner;

/**
 * Contrôleur pour l'interface console.
 */
public class ConsoleController {
    private final MovePieceUseCase movePieceUseCase;
    private final GetBestMoveUseCase getBestMoveUseCase;
    private final GameState gameState;
    private final Scanner scanner;
    private Player whitePlayer;
    private Player blackPlayer;

    public ConsoleController(MovePieceUseCase movePieceUseCase,
                             GetBestMoveUseCase getBestMoveUseCase) {
        this.movePieceUseCase = movePieceUseCase;
        this.getBestMoveUseCase = getBestMoveUseCase;
        this.gameState = new GameState();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Lance une nouvelle partie.
     */
    public void startNewGame() {
        displayWelcome();
        setupPlayers();
        gameState.initializeGame();
        gameLoop();
    }

    /**
     * Boucle principale du jeu.
     */
    private void gameLoop() {
        while (!gameState.isGameOver()) {
            displayBoard();
            displayGameInfo();

            Player currentPlayer = getCurrentPlayer();

            try {
                Move move;
                if (currentPlayer.isAI()) {
                    System.out.println("L'IA réfléchit...");
                    move = getBestMoveUseCase.execute(gameState);
                    System.out.println("L'IA joue: " + move.toAlgebraic());
                } else {
                    move = promptPlayerMove();
                }

                movePieceUseCase.execute(gameState, move.getFrom(), move.getTo());

            } catch (Exception e) {
                System.out.println("❌ Erreur: " + e.getMessage());
            }
        }

        displayGameOver();
    }

    /**
     * Demande au joueur de saisir son coup.
     */
    private Move promptPlayerMove() {
        System.out.print("\nEntrez votre coup (ex: e2e4) ou 'quit' pour quitter: ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("quit")) {
            System.out.println("Partie abandonnée.");
            System.exit(0);
        }

        if (input.equals("help")) {
            displayHelp();
            return promptPlayerMove();
        }

        if (input.length() < 4) {
            throw new IllegalArgumentException("Format invalide. Utilisez le format: e2e4");
        }

        Position from = Position.fromAlgebraic(input.substring(0, 2));
        Position to = Position.fromAlgebraic(input.substring(2, 4));

        Piece piece = gameState.getBoard().getPieceAt(from);
        Move.Builder builder = new Move.Builder(from, to, piece);

        // Gestion de la promotion
        if (input.length() == 5) {
            char promoChar = input.charAt(4);
            PieceType promoType = PieceType.fromFen(Character.toUpperCase(promoChar));
            builder.withPromotion(promoType);
        }

        return builder.build();
    }

    /**
     * Affiche le plateau de jeu.
     */
    private void displayBoard() {
        System.out.println("\n   a b c d e f g h");
        System.out.println("  ┌───────────────┐");

        Board board = gameState.getBoard();
        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " │");
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(new Position(row, col));
                System.out.print(getPieceSymbol(piece) + " ");
            }
            System.out.println("│ " + (row + 1));
        }

        System.out.println("  └───────────────┘");
        System.out.println("   a b c d e f g h");
    }

    /**
     * Retourne le symbole Unicode d'une pièce.
     */
    private String getPieceSymbol(Piece piece) {
        if (piece == null) {
            return " ";
        }

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

    /**
     * Affiche les informations de la partie.
     */
    private void displayGameInfo() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Tour: " + (gameState.getBoard().getFullMoveNumber()));
        System.out.println("Au tour de: " + getCurrentPlayer().getName() +
                " (" + gameState.getActivePlayer() + ")");
        System.out.println("Statut: " + gameState.getStatus());
        System.out.println("=".repeat(40));
    }

    /**
     * Configure les joueurs.
     */
    private void setupPlayers() {
        System.out.print("Nom du joueur blanc (ou 'AI' pour l'ordinateur): ");
        String whiteName = scanner.nextLine().trim();

        System.out.print("Nom du joueur noir (ou 'AI' pour l'ordinateur): ");
        String blackName = scanner.nextLine().trim();

        if (whiteName.equalsIgnoreCase("AI")) {
            whitePlayer = new Player("IA Blanche", Color.WHITE, Player.PlayerType.AI);
        } else {
            whitePlayer = new Player(whiteName, Color.WHITE);
        }

        if (blackName.equalsIgnoreCase("AI")) {
            blackPlayer = new Player("IA Noire", Color.BLACK, Player.PlayerType.AI);
        } else {
            blackPlayer = new Player(blackName, Color.BLACK);
        }
    }

    private Player getCurrentPlayer() {
        return gameState.getActivePlayer() == Color.WHITE ? whitePlayer : blackPlayer;
    }

    private void displayWelcome() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("♔♕♖♗♘♙  CHESS GAME - Groupe 207  ♙♘♗♖♕♔");
        System.out.println("=".repeat(50));
    }

    private void displayHelp() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("AIDE");
        System.out.println("=".repeat(50));
        System.out.println("Format des coups: e2e4 (de la case e2 vers la case e4)");
        System.out.println("Promotion: e7e8q (promouvoir en dame)");
        System.out.println("  q = Dame, r = Tour, b = Fou, n = Cavalier");
        System.out.println("Commandes: 'quit' pour quitter, 'help' pour l'aide");
        System.out.println("=".repeat(50));
    }

    private void displayGameOver() {
        displayBoard();
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PARTIE TERMINÉE");
        System.out.println("=".repeat(50));
        System.out.println("Statut final: " + gameState.getStatus());

        if (gameState.getStatus() == GameState.GameStatus.CHECKMATE) {
            Color winner = gameState.getActivePlayer().opposite();
            System.out.println("🏆 Victoire des " + winner + " !");
        } else if (gameState.getStatus() == GameState.GameStatus.STALEMATE) {
            System.out.println("Match nul (pat)");
        }

        System.out.println("Nombre de coups: " + gameState.getMoveCount());
        System.out.println("=".repeat(50));
    }

    /**
     * Ferme les ressources.
     */
    public void close() {
        scanner.close();
    }
}