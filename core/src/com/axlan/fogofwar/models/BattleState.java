package com.axlan.fogofwar.models;

import com.axlan.fogofwar.models.LevelData.Formation;
import com.axlan.fogofwar.models.LevelData.UnitStart;
import com.axlan.gdxtactics.TilePoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * State that describes current battle
 */
public class BattleState {

  /**
   * Mapping of points on the map, to the players units on that tile.
   */
  public final HashMap<TilePoint, FieldedUnit> playerUnits = new HashMap<>();
  /**
   * Mapping of points on the map, to the enemy units on that tile.
   */
  public final HashMap<TilePoint, FieldedUnit> enemyUnits = new HashMap<>();

  public BattleState(Map<String, UnitStats> unitStats, DeploymentSelection deploymentSelection,
      List<Formation> enemyFormations) {
    for (TilePoint point : deploymentSelection.getPlayerUnitPlacements().keySet()) {
      String unitType = deploymentSelection.getPlayerUnitPlacements().get(point);
      playerUnits.put(point, new FieldedUnit(unitStats.get(unitType)));
    }
    for (int formationIdx = 0; formationIdx < enemyFormations.size(); formationIdx++) {
      Formation formation = enemyFormations.get(formationIdx);
      int spawnIdx = deploymentSelection.getEnemySpawnSelections().get(formationIdx);
      for (UnitStart unit : formation.units) {
        TilePoint startPos = formation.getUnitPos(spawnIdx, unit);
        enemyUnits.put(startPos, new FieldedUnit(unitStats.get(unit.unitType)));
      }
    }
  }

}
