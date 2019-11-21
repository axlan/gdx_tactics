package com.axlan.fogofwar.models;

import com.axlan.gdxtactics.GameStateManagerBase;

/**
 * Class for storing the state of the current game session. State should be complete for saving and
 * reloading.
 */
public class GameStateManager extends GameStateManagerBase<GameState> {

  @Override
  protected GameState newGameState() {
    return new GameState();
  }

  @Override
  protected GameState[] newGameStateArray(int length) {
    return new GameState[length];
  }

  @Override
  protected GameState newGameState(GameState orig) {
    return new GameState(orig);
  }

  @Override
  protected void fetchSavesFromPrefs() {
    fetchSavesFromPrefs(GameState.class);
  }
}
