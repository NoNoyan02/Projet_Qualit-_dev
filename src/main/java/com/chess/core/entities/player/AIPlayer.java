package com.chess.core.entities.player;

import com.chess.core.entities.Color;
import com.chess.core.entities.game.GameState;
import com.chess.core.entities.game.Move;
import com.chess.core.ports.ChessEngine;

/**
 * Représente un joueur IA utilisant un moteur d'échecs.
 */
public class AIPlayer extends Player {
    private final int skillLevel;

    public AIPlayer(String name, Color color, int skillLevel) {
        super(name, color, PlayerType.AI);
        this.skillLevel = Math.max(1, Math.min(20, skillLevel));
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    @Override
    public String toString() {
        return getName() + " (" + getColor() + ", AI Level " + skillLevel + ")";
    }
}
