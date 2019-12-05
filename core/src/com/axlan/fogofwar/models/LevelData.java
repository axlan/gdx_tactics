package com.axlan.fogofwar.models;

import com.axlan.gdxtactics.SpriteLookup;
import com.axlan.gdxtactics.TilePoint;

import java.util.Collections;
import java.util.List;

/**
 * Class for loading and storing descriptions of a level
 *
 * <p>All lists loaded are unmodifiable
 *
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class LevelData {

  /**
   * The tile in the map should the camera should start centered on
   */
  public final TilePoint cameraCenter;
  /** The set of units available to the player for deployment */
  public final List<UnitAllotment> playerUnits;
  // TODO-P3 Add ally formations

  /** The points on the map the player can deploy troops */
  public final List<TilePoint> playerSpawnPoints;
  /** The formations of enemy units that will spawn */
  public final List<Formation> enemyFormations;
  /** The name of the map to load/use */
  public final String mapName;
  /**
   * How the enemy units will behave
   */
  public final UnitBehavior enemyBehavior;
  /**
   * Additional optional win conditions for the player
   */
  public final AlternativeWinConditions playerWinConditions;
  /** Additional optional win conditions for the enemy */
  public final AlternativeWinConditions enemyWinConditions;

  public LevelData(
      TilePoint cameraCenter,
      List<UnitAllotment> playerUnits,
      List<TilePoint> playerSpawnPoints,
      List<Formation> enemyFormations,
      String mapName,
      UnitBehavior enemyBehavior,
      AlternativeWinConditions playerWinConditions,
      AlternativeWinConditions enemyWinConditions) {
    this.cameraCenter = cameraCenter;
    this.playerUnits = Collections.unmodifiableList(playerUnits);
    this.playerSpawnPoints = Collections.unmodifiableList(playerSpawnPoints);
    this.enemyFormations = Collections.unmodifiableList(enemyFormations);
    this.mapName = mapName;
    this.enemyBehavior = enemyBehavior;
    this.enemyWinConditions = enemyWinConditions;
    this.playerWinConditions = playerWinConditions;
  }

  /** What behavior should the enemy formation take */
  public enum UnitBehaviorType {
    MOVE,
    DEFEND,
    ATTACK
  }

  // TODO-P3 replace UnitBehavior with more versatile Lua scripts

  /** Description of behavior enemy unit should take */
  public static class UnitBehavior {

    /** Type of behavior pattern */
    public final UnitBehaviorType behaviorType;
    /** String containing Json for parameters that modify the behaviorType */
    public final String args;

    public UnitBehavior(UnitBehaviorType behaviorType, String args) {
      this.behaviorType = behaviorType;
      this.args = args;
    }
  }

  /**
   * Optional alternative win conditions
   */
  public static class AlternativeWinConditions {

    /**
     * Win if unit reaches this point
     */
    public final TilePoint moveToPoint;
    /**
     * Win if oppenent reaches this point
     */
    public final TilePoint opponentAtPoint;

    public AlternativeWinConditions(TilePoint moveToPoint, TilePoint opponentAtPoint) {
      this.moveToPoint = moveToPoint;
      this.opponentAtPoint = opponentAtPoint;
    }
  }

  /** Description of a unit within a {@link Formation} */
  public static class UnitStart {

    /**
     * Identifier for the unit type. Used to lookup stats and sprites
     *
     * @see UnitStats
     * @see SpriteLookup
     */
    public final String unitType;
    /** Position of unit relative to spawn point of {@link Formation} */
    public final TilePoint relativePosition;

    public UnitStart(String unitType, TilePoint relativePosition) {
      this.unitType = unitType;
      this.relativePosition = relativePosition;
    }
  }

  /** A description of a set of units that will spawn together */
  public static class Formation {

    /** A list of the possible points the set of units will spawn around. */
    public final List<TilePoint> spawnPoints;
    /** The list of units in the formation */
    public final List<UnitStart> units;

    public Formation(List<TilePoint> spawnPoints, List<UnitStart> units) {
      this.spawnPoints = Collections.unmodifiableList(spawnPoints);
      this.units = Collections.unmodifiableList(units);
    }

    /**
     * Get the absolute position of a unit in the formation
     *
     * @param spawnIdx The index of the spawn point that was selected.
     * @param unitIdx The index of the unit in units to get the absolute position for.
     * @return The absolute position of the specified unit.
     */
    public TilePoint getUnitPos(int spawnIdx, int unitIdx) {
      return spawnPoints.get(spawnIdx).add(units.get(unitIdx).relativePosition);
    }

    /**
     * Get the absolute position of a unit in the formation
     *
     * @param spawnIdx The index of the spawn point that was selected.
     * @param unit The unit in units to get the absolute position for.
     * @return The absolute position of the specified unit.
     */
    public TilePoint getUnitPos(int spawnIdx, UnitStart unit) {
      int unitIdx = units.indexOf(unit);
      return getUnitPos(spawnIdx, unitIdx);
    }
  }

  /** Description of unit to give the player for placement */
  public static class UnitAllotment {

    /**
     * Identifier for the unit type. Used to lookup stats and sprites
     *
     * @see UnitStats
     * @see SpriteLookup
     */
    public final String type;
    /** How many of the unit to give the player for placement */
    public final int count;

    public UnitAllotment(String type, int count) {
      this.type = type;
      this.count = count;
    }
  }
}
