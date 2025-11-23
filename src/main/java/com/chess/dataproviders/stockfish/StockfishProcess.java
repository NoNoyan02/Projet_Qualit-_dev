package com.chess.dataproviders.stockfish;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère la communication avec le processus Stockfish via UCI.
 * Note: Cette classe utilise la bibliothèque chess-stockfish qui gère automatiquement
 * le téléchargement et l'exécution de Stockfish.
 */
public class StockfishProcess {
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * Initialise le moteur Stockfish.
     * La bibliothèque io.github.guillaumcn:chess-stockfish gère automatiquement
     * le téléchargement et l'initialisation.
     */
    public void initialize() {
        try {
            // Utilisation de la bibliothèque chess-stockfish
            // Le code réel utiliserait: xyz.niflheim.stockfish.engine.Stockfish
            // Pour ce template, on simule l'interface
            sendCommand("uci");
            waitForResponse("uciok");
            sendCommand("isready");
            waitForResponse("readyok");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'initialisation de Stockfish", e);
        }
    }

    /**
     * Obtient le meilleur coup pour une position FEN.
     */
    public String getBestMove(String fen, int thinkingTimeMs) {
        try {
            sendCommand("position fen " + fen);
            sendCommand("go movetime " + thinkingTimeMs);

            String line;
            String bestMove = null;
            while ((line = readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    String[] parts = line.split(" ");
                    if (parts.length > 1) {
                        bestMove = parts[1];
                    }
                    break;
                }
            }
            return bestMove;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du calcul du meilleur coup", e);
        }
    }

    /**
     * Évalue une position (en centipawns).
     */
    public double evaluatePosition(String fen) {
        try {
            sendCommand("position fen " + fen);
            sendCommand("go depth 15");

            String line;
            double score = 0;
            while ((line = readLine()) != null) {
                if (line.contains("score cp")) {
                    String[] parts = line.split(" ");
                    for (int i = 0; i < parts.length - 1; i++) {
                        if (parts[i].equals("cp")) {
                            score = Double.parseDouble(parts[i + 1]);
                            break;
                        }
                    }
                } else if (line.startsWith("bestmove")) {
                    break;
                }
            }
            return score;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'évaluation", e);
        }
    }

    /**
     * Obtient tous les coups légaux pour une position.
     */
    public List<String> getLegalMoves(String fen) {
        // Cette fonctionnalité nécessiterait une analyse plus poussée
        // ou l'utilisation d'une bibliothèque supplémentaire
        // Pour l'instant, retourne une liste vide
        return new ArrayList<>();
    }

    /**
     * Configure le niveau de compétence (1-20).
     */
    public void setSkillLevel(int level) {
        try {
            sendCommand("setoption name Skill Level value " + level);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la configuration du niveau", e);
        }
    }

    /**
     * Arrête le moteur Stockfish.
     */
    public void shutdown() {
        try {
            sendCommand("quit");
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (process != null) process.destroy();
        } catch (IOException e) {
            // Ignore les erreurs de fermeture
        }
    }

    private void sendCommand(String command) throws IOException {
        writer.write(command + "\n");
        writer.flush();
    }

    private String readLine() throws IOException {
        return reader.readLine();
    }

    private void waitForResponse(String expected) throws IOException {
        String line;
        while ((line = readLine()) != null) {
            if (line.equals(expected)) {
                break;
            }
        }
    }

    /**
     * Note: L'implémentation réelle utiliserait:
     *
     * import xyz.niflheim.stockfish.engine.Stockfish;
     *
     * public class StockfishProcess {
     *     private Stockfish stockfish;
     *
     *     public void initialize() {
     *         stockfish = new Stockfish();
     *     }
     *
     *     public String getBestMove(String fen, int time) {
     *         return stockfish.getBestMove(fen, time);
     *     }
     * }
     */
}