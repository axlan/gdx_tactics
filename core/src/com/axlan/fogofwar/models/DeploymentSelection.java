package com.axlan.fogofwar.models;

import com.axlan.gdxtactics.TilePoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for storing the unit deployment selections
 * <p>
 * Selection is made in the {@link com.axlan.fogofwar.screens.DeployView DeployView} screen and
 * used in the subsequent battle.
 *
 * <p>Objects returned in the getters are unmodifiable
 *
 * @see GameStateManager
 */
@SuppressWarnings({"unused"})
public class DeploymentSelection {

  private final ArrayList<Integer> enemySpawnSelections = new ArrayList<>();
  private final HashMap<TilePoint, String> playerUnitPlacements = new HashMap<>();

  /**
   * a List of which spawnPoints in {@link LevelData.Formation} were randomly selected
   * This value is returned as an UnmodifiableList.
   *
   * @return the saved spawn selection
   */
  public List<Integer> getEnemySpawnSelections() {
    return Collections.unmodifiableList(enemySpawnSelections);
  }

  /**
   * Gets the stored mapping of TilePoints on the map and the type of unit the player deployed there.
   * This value is returned as an UnmodifiableMap.
   *
   * @return the saved placements
   */
  public Map<TilePoint, String> getPlayerUnitPlacements() {
    return Collections.unmodifiableMap(playerUnitPlacements);
  }

  /**
   * Store deployment selection while clearing and previous deployment.
   *
   * @param enemySpawnSelections a List of which spawnPoints in {@link LevelData.Formation} were
   *                             randomly selected
   * @param playerUnitPlacements a mapping of TilePoints on the map and the type of unit the player
   *                             deployed there.
   */
  public void setDeployments(
      List<Integer> enemySpawnSelections, Map<TilePoint, String> playerUnitPlacements) {
    this.enemySpawnSelections.clear();
    this.playerUnitPlacements.clear();
    this.enemySpawnSelections.addAll(enemySpawnSelections);
    this.playerUnitPlacements.putAll(playerUnitPlacements);
  }
}
