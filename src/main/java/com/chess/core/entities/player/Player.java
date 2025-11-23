package com.chess.core.entities.player;

import com.chess.core.entities.Color;
import com.chess.core.entities.Position;
import com.chess.core.entities.game.Move;

import java.util.Objects;

/**
 * Représente un joueur humain.
 */
public class Player {
    private final String name;
    private final Color color;
    private final PlayerType type;

    public Player(String name, Color color) {
        this(name, color, PlayerType.HUMAN);
    }

    public Player(String name, Color color, PlayerType type) {
        this.name = Objects.requireNonNull(name, "Le nom ne peut pas être null");
        this.color = Objects.requireNonNull(color, "La couleur ne peut pas être null");
        this.type = Objects.requireNonNull(type, "Le type ne peut pas être null");
    }

    /**
     * Crée un mouvement à partir des positions de départ et d'arrivée.
     * Cette méthode est généralement utilisée pour valider l'input du joueur.
     */
    public Move createMove(Position from, Position to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Les positions ne peuvent pas être null");
        }
        return null; // Le move sera créé par le use case
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public PlayerType getType() {
        return type;
    }

    public boolean isHuman() {
        return type == PlayerType.HUMAN;
    }

    public boolean isAI() {
        return type == PlayerType.AI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name) && color == player.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }

    @Override
    public String toString() {
        return name + " (" + color + ")";
    }

    /**
     * Enum représentant le type de joueur.
     */
    public enum PlayerType {
        HUMAN,
        AI
    }
}