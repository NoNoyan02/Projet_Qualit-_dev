package com.chess.dataproviders.stockfish;

import com.chess.core.ports.ChessEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du moteur Stockfish.
 * Utilise la bibliothèque chess-stockfish.
 */
public class StockfishEngine implements ChessEngine {
    private StockfishProcess stockfish;
    private int skillLevel;
    private boolean isReady;

    public StockfishEngine() {
        this.skillLevel = 20;
        this.isReady = false;
    }

    @Override
    public void start() {
        if (stockfish == null) {
            stockfish = new StockfishProcess();
            stockfish.initialize();
            setSkillLevel(skillLevel);
            isReady = true;
        }
    }

    @Override
    public void stop() {
        if (stockfish != null) {
            stockfish.shutdown();
            stockfish = null;
            isReady = false;
        }
    }

    @Override
    public String getBestMove(String fen, int maxDepth, long maxTimeMs) {
        ensureStarted();
        return stockfish.getBestMove(fen, maxDepth, maxTimeMs);
    }

    @Override
    public double evaluatePosition(String fen) {
        ensureStarted();
        return stockfish.evaluatePosition(fen);
    }

    @Override
    public List<String> getLegalMoves(String fen) {
        ensureStarted();
        return stockfish.getLegalMoves(fen);
    }

    @Override
    public void setSkillLevel(int level) {
        this.skillLevel = Math.max(1, Math.min(20, level));
        if (stockfish != null) {
            stockfish.setSkillLevel(this.skillLevel);
        }
    }

    @Override
    public String getEngineInfo() {
        return "Stockfish 17.1";
    }

    @Override
    public boolean isReady() {
        return isReady && stockfish != null;
    }

    private void ensureStarted() {
        if (!isReady()) {
            start();
        }
    }
}