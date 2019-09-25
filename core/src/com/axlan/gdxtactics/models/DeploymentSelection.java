package com.axlan.gdxtactics.models;

import com.badlogic.gdx.math.GridPoint2;
import java.util.HashMap;

public class DeploymentSelection {

  public final int[] enemySpawnSelections;
  public final HashMap<GridPoint2, String> playerUnitPlacements;

  public DeploymentSelection(int[] enemySpawnSelections,
      HashMap<GridPoint2, String> playerUnitPlacements) {
    this.enemySpawnSelections = enemySpawnSelections;
    this.playerUnitPlacements = playerUnitPlacements;
  }
}
