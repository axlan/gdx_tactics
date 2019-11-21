package com.axlan.fogofwar.models;

import com.axlan.fogofwar.models.LevelData.Formation;
import com.axlan.fogofwar.models.LevelData.UnitStart;
import com.axlan.gdxtactics.TilePoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * State that describes current battle
 */
public class BattleState {

  /**
   * Mapping of points on the map, to the players units on that tile.
   */
  public final HashMap<TilePoint, FieldedUnit> playerUnits;
  /**
   * Mapping of points on the map, to the enemy units on that tile.
   */
  public final HashMap<TilePoint, FieldedUnit> enemyUnits;

  public BattleState(
      ArrayList<Integer> enemySpawns,
      HashMap<TilePoint, String> playerPlacements,
      List<Formation> enemyFormations) {
    playerUnits = new HashMap<>();
    enemyUnits = new HashMap<>();
    for (TilePoint point : playerPlacements.keySet()) {
      String unitType = playerPlacements.get(point);
      playerUnits.put(point, new FieldedUnit(unitType));
    }
    for (int formationIdx = 0; formationIdx < enemyFormations.size(); formationIdx++) {
      Formation formation = enemyFormations.get(formationIdx);
      int spawnIdx = enemySpawns.get(formationIdx);
      for (UnitStart unit : formation.units) {
        TilePoint startPos = formation.getUnitPos(spawnIdx, unit);
        enemyUnits.put(startPos, new FieldedUnit(unit.unitType));
      }
    }
  }

  BattleState(BattleState other) {
    playerUnits = new HashMap<>();
    enemyUnits = new HashMap<>();
    for (TilePoint point : other.playerUnits.keySet()) {
      playerUnits.put(point, new FieldedUnit(other.playerUnits.get(point)));
    }
    for (TilePoint point : other.enemyUnits.keySet()) {
      enemyUnits.put(point, new FieldedUnit(other.enemyUnits.get(point)));
    }
  }
}
