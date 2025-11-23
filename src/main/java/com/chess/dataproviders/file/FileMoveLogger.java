package com.chess.dataproviders.file;

import com.chess.core.entities.game.Move;
import com.chess.core.ports.MoveLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation de la journalisation des coups en fichiers.
 */
public class FileMoveLogger implements MoveLogger {
    private static final Logger logger = LoggerFactory.getLogger(FileMoveLogger.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Path logDirectory;

    public FileMoveLogger(String logDirectoryPath) {
        this.logDirectory = Paths.get(logDirectoryPath);
        initializeLogDirectory();
    }

    private void initializeLogDirectory() {
        try {
            if (!Files.exists(logDirectory)) {
                Files.createDirectories(logDirectory);
                logger.info("Répertoire de logs créé: {}", logDirectory);
            }
        } catch (IOException e) {
            logger.error("Erreur lors de la création du répertoire de logs", e);
        }
    }

    @Override
    public void logMove(String gameId, Move move, int moveNumber) {
        try {
            Path logFile = getLogFile(gameId);
            String logEntry = formatLogEntry(move, moveNumber);

            Files.writeString(logFile, logEntry,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            logger.debug("Coup {} enregistré pour la partie {}", moveNumber, gameId);
        } catch (IOException e) {
            logger.error("Erreur lors de l'enregistrement du coup", e);
        }
    }

    @Override
    public List<String> getMoveHistory(String gameId) {
        try {
            Path logFile = getLogFile(gameId);
            if (!Files.exists(logFile)) {
                return new ArrayList<>();
            }

            return Files.readAllLines(logFile).stream()
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::extractMoveFromLog)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture de l'historique", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void clearHistory(String gameId) {
        try {
            Path logFile = getLogFile(gameId);
            if (Files.exists(logFile)) {
                Files.delete(logFile);
                logger.info("Historique de la partie {} supprimé", gameId);
            }
        } catch (IOException e) {
            logger.error("Erreur lors de la suppression de l'historique", e);
        }
    }

    @Override
    public String exportToPGN(String gameId, String whitePlayer, String blackPlayer) {
        List<String> moves = getMoveHistory(gameId);

        StringBuilder pgn = new StringBuilder();
        pgn.append("[Event \"Chess Game\"]\n");
        pgn.append("[Date \"").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\"]\n");
        pgn.append("[White \"").append(whitePlayer).append("\"]\n");
        pgn.append("[Black \"").append(blackPlayer).append("\"]\n");
        pgn.append("[Result \"*\"]\n\n");

        for (int i = 0; i < moves.size(); i++) {
            if (i % 2 == 0) {
                pgn.append((i / 2 + 1)).append(". ");
            }
            pgn.append(moves.get(i)).append(" ");
        }

        return pgn.toString();
    }

    @Override
    public void logEvent(String gameId, String event) {
        try {
            Path logFile = getLogFile(gameId);
            String eventEntry = String.format("[%s] EVENT: %s%n",
                    LocalDateTime.now().format(DATE_FORMATTER), event);

            Files.writeString(logFile, eventEntry,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            logger.info("Événement enregistré pour la partie {}: {}", gameId, event);
        } catch (IOException e) {
            logger.error("Erreur lors de l'enregistrement de l'événement", e);
        }
    }

    private Path getLogFile(String gameId) {
        return logDirectory.resolve(gameId + "_moves.log");
    }

    private String formatLogEntry(Move move, int moveNumber) {
        return String.format("[%s] Move %d: %s%n",
                LocalDateTime.now().format(DATE_FORMATTER),
                moveNumber,
                move.toAlgebraic());
    }

    private String extractMoveFromLog(String logLine) {
        // Format: [timestamp] Move N: move
        int lastColon = logLine.lastIndexOf(':');
        if (lastColon != -1 && lastColon < logLine.length() - 1) {
            return logLine.substring(lastColon + 1).trim();
        }
        return logLine;
    }
}