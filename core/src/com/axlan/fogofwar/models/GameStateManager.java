package com.axlan.fogofwar.models;

/**
 * Class for storing the state of the current game session. State should be complete for saving and
 * reloading.
 *
 * <p>State is static and shared throughout application
 */
public class GameStateManager {
  //TODO-P1 add methods for saving and loading state, along with any missing data
  //TODO-P1 Add save button as child class of actor stage
  /**
   * Keeps track of state associated with the player
   */
  public static final PlayerResources playerResources = new PlayerResources();
  /**
   * Records decisions made in the {@link com.axlan.fogofwar.screens.DeployView DeployView} screen
   */
  public static final DeploymentSelection deploymentSelection = new DeploymentSelection();
}
