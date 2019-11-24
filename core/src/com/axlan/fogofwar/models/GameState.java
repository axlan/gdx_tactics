package com.axlan.fogofwar.models;

import com.axlan.fogofwar.campaigns.CampaignBase;

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

  public GameState(CampaignBase campaign) {
    playerResources = new PlayerResources();
    this.campaign = campaign;
  }

  GameState(GameState other) {
    playerResources = new PlayerResources(other.playerResources);
    battleState = new BattleState(other.battleState);
    campaign = other.campaign.makeCopy();
  }
}
