package com.axlan.fogofwar.models;

import com.axlan.fogofwar.campaigns.CampaignBase;
import com.axlan.gdxtactics.GameStateManagerBase;

/**
 * Class for storing the state of the current game session. State should be complete for saving and
 * reloading.
 */
public class GameStateManager extends GameStateManagerBase<GameState> {

  public GameStateManager() {
    super();
    gsonBuilder.registerTypeAdapter(CampaignBase.class, new CampaignBaseDeserializer());
    gsonBuilder.registerTypeAdapter(CampaignBase.class, new CampaignBaseSerializer());
    gsonBuilder.enableComplexMapKeySerialization();
    fetchSavesFromPrefs();
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
