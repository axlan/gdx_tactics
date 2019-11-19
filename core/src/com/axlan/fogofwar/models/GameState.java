package com.axlan.fogofwar.models;

/**
 * Class for storing the state of the current game session. State should be complete for saving and
 * reloading.
 *
 * <p>State is static and shared throughout application
 */
public class GameState {
    /**
     * Keeps track of state associated with the player
     */
    public final PlayerResources playerResources;
    /**
     * Records decisions made in the {@link com.axlan.fogofwar.screens.DeployView DeployView} screen
     */
    public BattleState battleState;

    GameState() {
        playerResources = new PlayerResources();
    }

    GameState(GameState other) {
        playerResources = new PlayerResources(other.playerResources);
        battleState = new BattleState(other.battleState);
    }
}
