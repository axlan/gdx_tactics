package com.axlan.fogofwar.models;

import com.axlan.gdxtactics.JsonLoader;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;

/**
 * Class for loading and storing stats for units from JSON. The structure of the JSON should mimic
 * the structure of the class.
 *
 * <p>All lists loaded are unmodifiable
 *
 * <p>A static instance is managed by {@link LoadedResources}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class UnitStats {

    /**
     * Identifier for unit
     */
  public final String type;
    /**
     * How many tiles of vision range the unit has
     */
  public final int visionRange;
  /** Total health for unit */
  public final int maxHealth;
  /** Base attack points */
  public final int attack;
  /** Base tiles of movement range */
  public final int movement;
    /**
     * Base tiles of movement range
     */
  public final int minAttackRange;
    /** Base tiles of movement range */
  public final int maxAttackRange;

    public UnitStats(
            String type,
            int visionRange,
            int maxHealth,
            int attack,
            int movement,
            int minAttackRange,
            int maxAttackRange) {
    this.type = type;
    this.visionRange = visionRange;
    this.maxHealth = maxHealth;
    this.attack = attack;
    this.movement = movement;
    this.minAttackRange = minAttackRange;
    this.maxAttackRange = maxAttackRange;
  }

  /**
   * This method deserializes the JSON read from the specified path into a LevelData object
   *
   * @param projectPath path in the assets directory to JSON file to parse
   * @return a new list of Unit objects populated from the JSON file
   * @throws JsonIOException if there was a problem reading from the Reader
   * @throws JsonSyntaxException if json is not a valid representation for an object of type
   */
  static HashMap<String, UnitStats> loadFromJson(String projectPath) {
    UnitStats[] statsList = JsonLoader.loadFromJsonFileInternal(projectPath, UnitStats[].class);
    HashMap<String, UnitStats> statMap = new HashMap<>();
    for (UnitStats stat : statsList) {
      statMap.put(stat.type, stat);
    }
    return statMap;
  }
}
