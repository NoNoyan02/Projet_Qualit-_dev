package com.chess.dataproviders.file;

import com.chess.core.entities.game.GameState;
import com.chess.core.ports.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation de la persistance en fichiers JSON.
 */
public class FileGameRepository implements GameRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileGameRepository.class);
    private final Path saveDirectory;
    private final ObjectMapper objectMapper;

    public FileGameRepository(String saveDirectoryPath) {
        this.saveDirectory = Paths.get(saveDirectoryPath);
        this.objectMapper = new ObjectMapper();
        initializeSaveDirectory();
    }

    private void initializeSaveDirectory() {
        try {
            if (!Files.exists(saveDirectory)) {
                Files.createDirectories(saveDirectory);
                logger.info("Répertoire de sauvegarde créé: {}", saveDirectory);
            }
        } catch (IOException e) {
            logger.error("Erreur lors de la création du répertoire de sauvegarde", e);
        }
    }

    @Override
    public void save(String gameId, GameState gameState) {
        try {
            File file = getGameFile(gameId);
            GameStateDTO dto = GameStateDTO.fromGameState(gameState);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, dto);
            logger.info("Partie {} sauvegardée", gameId);
        } catch (IOException e) {
            logger.error("Erreur lors de la sauvegarde de la partie {}", gameId, e);
            throw new RuntimeException("Impossible de sauvegarder la partie", e);
        }
    }

    @Override
    public Optional<GameState> load(String gameId) {
        try {
            File file = getGameFile(gameId);
            if (!file.exists()) {
                logger.warn("Partie {} introuvable", gameId);
                return Optional.empty();
            }

            GameStateDTO dto = objectMapper.readValue(file, GameStateDTO.class);
            GameState gameState = dto.toGameState();
            logger.info("Partie {} chargée", gameId);
            return Optional.of(gameState);
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la partie {}", gameId, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(String gameId) {
        try {
            File file = getGameFile(gameId);
            boolean deleted = file.delete();
            if (deleted) {
                logger.info("Partie {} supprimée", gameId);
            }
            return deleted;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de la partie {}", gameId, e);
            return false;
        }
    }

    @Override
    public List<String> listAllGames() {
        try {
            return Files.list(saveDirectory)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString().replace(".json", ""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Erreur lors du listage des parties", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean exists(String gameId) {
        return getGameFile(gameId).exists();
    }

    @Override
    public void autoSave(String gameId, GameState gameState) {
        save(gameId + "_autosave", gameState);
    }

    private File getGameFile(String gameId) {
        return saveDirectory.resolve(gameId + ".json").toFile();
    }

    /**
     * DTO pour la sérialisation/désérialisation.
     */
    private static class GameStateDTO {
        public String fen;
        public String activePlayer;
        public String status;
        public List<String> moveHistory;

        public static GameStateDTO fromGameState(GameState gameState) {
            GameStateDTO dto = new GameStateDTO();
            dto.fen = gameState.toFen();
            dto.activePlayer = gameState.getActivePlayer().name();
            dto.status = gameState.getStatus().name();
            dto.moveHistory = gameState.getMoveHistoryAlgebraic();
            return dto;
        }

        public GameState toGameState() {
            GameState gameState = new GameState();
            gameState.initializeFromFen(fen);
            return gameState;
        }
    }
}