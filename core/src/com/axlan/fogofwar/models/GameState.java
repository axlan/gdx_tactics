package com.axlan.fogofwar.models;

import com.axlan.fogofwar.campaigns.CampaignBase;
import com.axlan.fogofwar.screens.SceneLabel;

import java.util.HashMap;
import java.util.Map;

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
  /**
   * Selected campaign with campaign state
   */
  public final CampaignBase campaign;
  /**
   * Current battle ground city
   */
  public String contestedCity = null;

  /**
   * Player that controls each city
   */
  public final Map<String, City.Controller> controlledCities = new HashMap<>();

  /**
   * Identifier for current scene
   */
  public SceneLabel scene;

  public GameState(CampaignBase campaign) {
    playerResources = new PlayerResources();
    this.campaign = campaign;
  }

  GameState(GameState other) {
    playerResources = new PlayerResources(other.playerResources);
    battleState = new BattleState(other.battleState);
    campaign = other.campaign.makeCopy();
    scene = other.scene;
    contestedCity = other.contestedCity;
  }
}
