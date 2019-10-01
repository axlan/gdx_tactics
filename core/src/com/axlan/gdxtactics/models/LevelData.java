package com.axlan.gdxtactics.models;

import com.badlogic.gdx.Gdx;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class for loading and storing descriptions of a level from JSON. The structure of the JSON should
 * mimic the structure of the class.
 *
 * <p> All lists loaded are unmodifiable
 * <p> A static instance is managed by {@link LoadedResources}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class LevelData {

  /**
   * The tile in the map should the camera should start centered on
   */
  public final TilePoint cameraCenter;
  /** The set of units available to the player for deployment */
  public final List<UnitAllotment> playerUnits;

  //TODO-P3 Add ally formations
  /** The string to display describing where the briefing is occurring */
  public final String briefSetting;
  /** The pages of the briefing to step through */
  public final List<BriefPage> briefPages;
  /** The items available in the shop */
  public final List<ShopItem> shopItems;
  /** The points on the map the player can deploy troops */
  public final List<TilePoint> playerSpawnPoints;
  /** The formations of enemy units that will spawn */
  public final List<Formation> enemyFormations;
  /** The name of the map to load/use */
  public final String mapName;

  private LevelData(TilePoint cameraCenter,
      List<UnitAllotment> playerUnits, String briefSetting,
      List<BriefPage> briefPages, List<ShopItem> shopItems,
      List<TilePoint> playerSpawnPoints,
      List<Formation> enemyFormations, String mapName) {
    this.cameraCenter = cameraCenter;
    this.playerUnits = Collections.unmodifiableList(playerUnits);
    this.briefSetting = briefSetting;
    this.briefPages = Collections.unmodifiableList(briefPages);
    this.shopItems = Collections.unmodifiableList(shopItems);
    this.playerSpawnPoints = Collections.unmodifiableList(playerSpawnPoints);
    this.enemyFormations = Collections.unmodifiableList(enemyFormations);
    this.mapName = mapName;
  }

  /**
   * This method deserializes the JSON read from the specified path into a LevelData object
   *
   * @param projectPath path in the assets directory to JSON file to parse
   * @return a new instance of LevelData populated from the JSON file
   * @throws JsonIOException     if there was a problem reading from the Reader
   * @throws JsonSyntaxException if json is not a valid representation for an object of type
   */
  static LevelData loadFromJson(String projectPath) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapterFactory(new ImmutableListTypeAdapterFactory());
    Reader reader = Gdx.files.internal(projectPath).reader();
    return gsonBuilder.create().fromJson(reader, LevelData.class);
  }

  /**
   * What behavior should the enemy formation take
   */
  public enum UnitBehaviorType {
    MOVE,
    DEFEND,
    ATTACK
  }

  /**
   * Should the units in a formation be spotted in order, or at random
   */
  public enum SpotType {
    RANDOM,
    ORDERED
  }

  /** Description of a page of briefing dialogue */
  public static class BriefPage {

    /** The identification for the speaker */
    public final String speaker;
    /** The page of dialogue */
    public final String dialogue;

    private BriefPage(String speaker, String dialogue) {
      this.speaker = speaker;
      this.dialogue = dialogue;
    }
  }

  //TODO-P3 replace UnitBehavior with more versatile Lua scripts

  /** Description of behavior enemy unit should take*/
  public static class UnitBehavior {

    /** Type of behavior pattern */
    public final UnitBehaviorType behaviorType;
    /** A map of parameters that modify the behaviorType */
    public final Map<String, String> args;

    private UnitBehavior(UnitBehaviorType behaviorType,
        Map<String, String> args) {
      this.behaviorType = behaviorType;
      this.args = Collections.unmodifiableMap(args);
    }
  }

  /** Description of a unit within a {@link Formation} */
  public static class UnitStart {

    /**
     * Identifier for the unit type. Used to lookup stats and sprites
     *
     * @see UnitStats
     * @see com.axlan.gdxtactics.screens.SpriteLookup
     */
    public final String unitType;
    /** Position of unit relative to spawn point of {@link Formation} */
    public final TilePoint relativePosition;
    /** How the unit will behave */
    public final UnitBehavior behavior;

    private UnitStart(String unitType, TilePoint relativePosition,
        UnitBehavior behavior) {
      this.unitType = unitType;
      this.relativePosition = relativePosition;
      this.behavior = behavior;
    }
  }

  /** A description of a set of units that will spawn together */
  public static class Formation {

    /** A list of the possible points the set of units will spawn around. */
    public final List<TilePoint> spawnPoints;
    /** The list of units in the formation */
    public final List<UnitStart> units;

    private Formation(List<TilePoint> spawnPoints, List<UnitStart> units) {
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
  }

  /**
   * Class describing how an item will reveal information about the enemy.
   *
   * <p>This sets what the player will see during the {@link com.axlan.gdxtactics.screens.DeployView} scene
   */
  public static class Intel {

    //TODO-P2 Add reported and actual accuracy values, mis-identification, false positive, etc.
    //TODO-P2 Allow items to persist between levels.
    /** The index of the {@link Formation formation} to reveal info on */
    public final int formationSpottedIdx;
    /** How many units in the formation should be reveled */
    public final int numberOfUnits;
    /** How should the reveal be ordered */
    public final SpotType spotType;

    private Intel(int formationSpottedIdx, int numberOfUnits,
        SpotType spotType) {
      this.formationSpottedIdx = formationSpottedIdx;
      this.numberOfUnits = numberOfUnits;
      this.spotType = spotType;
    }
  }

  /** Description of an item appearing in the shop */
  public static class ShopItem {

    /** Name of item to be displayed */
    public final String name;
    /** Cost of item in store */
    public final int cost;
    /** Description of item to be displayed */
    public final String description;
    /** Pieces of intel that buying the item will reveal */
    public final List<Intel> effects;

    private ShopItem(String name, int cost, String description,
        List<Intel> effects) {
      this.name = name;
      this.cost = cost;
      this.description = description;
      this.effects = Collections.unmodifiableList(effects);
    }
  }

  /** Description of unit to give the player for placement */
  public static class UnitAllotment {

    /**
     * Identifier for the unit type. Used to lookup stats and sprites
     *
     * @see UnitStats
     * @see com.axlan.gdxtactics.screens.SpriteLookup
     */
    public final String type;
    /** How many of the unit to give the player for placement */
    public final int count;

    private UnitAllotment(String type, int count) {
      this.type = type;
      this.count = count;
    }
  }
}
