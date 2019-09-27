package com.axlan.gdxtactics.models;

import java.util.HashMap;

public class DeploymentSelection {

  public final int[] enemySpawnSelections;
  public final HashMap<TilePoint, String> playerUnitPlacements;

  public DeploymentSelection(int[] enemySpawnSelections,
      HashMap<TilePoint, String> playerUnitPlacements) {
    this.enemySpawnSelections = enemySpawnSelections;
    this.playerUnitPlacements = playerUnitPlacements;
  }
}
