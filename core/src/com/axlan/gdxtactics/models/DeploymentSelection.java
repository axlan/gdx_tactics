package com.axlan.gdxtactics.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeploymentSelection {

  private final ArrayList<Integer> enemySpawnSelections = new ArrayList<>();
  private final HashMap<TilePoint, String> playerUnitPlacements = new HashMap<>();

  public List<Integer> getEnemySpawnSelections() {
    return Collections.unmodifiableList(enemySpawnSelections);
  }

  public Map<TilePoint, String> getPlayerUnitPlacements() {
    return Collections.unmodifiableMap(playerUnitPlacements);
  }

  public void addDeployments(List<Integer> enemySpawnSelections,
      Map<TilePoint, String> playerUnitPlacements) {
    this.enemySpawnSelections.addAll(enemySpawnSelections);
    this.playerUnitPlacements.putAll(playerUnitPlacements);
  }
}
