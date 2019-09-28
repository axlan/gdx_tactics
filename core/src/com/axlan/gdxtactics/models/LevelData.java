package com.axlan.gdxtactics.models;

import com.badlogic.gdx.Gdx;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class LevelData {

  public enum UnitBehaviorType {
    MOVE,
    DEFEND,
    ATTACK
  }

  public enum SpotType {
    RANDOM,
    ORDERED
  }

  //TODO Add ally formations
  public final TilePoint cameraCenter;
  public final List<UnitAllotment> playerUnits;
  public final String briefSetting;
  public final List<BriefPage> briefPages;
  public final List<ShopItem> shopItems;
  public final List<TilePoint> playerSpawnPoints;
  public final List<Formation> enemyFormations;
  public final String mapName;

  public LevelData(TilePoint cameraCenter,
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
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapterFactory(new ImmutableListTypeAdapterFactory());
    Reader reader = Gdx.files.internal(projectPath).reader();
    return gson.create().fromJson(reader, LevelData.class);
  }

  /** Description of a page of briefing dialogue */
  public static class BriefPage {

    /** The identification for the speaker */
    public final String speaker;
    /** The page of dialogue */
    public final String dialogue;

    public BriefPage(String speaker, String dialogue) {
      this.speaker = speaker;
      this.dialogue = dialogue;
    }
  }

  public static class UnitBehavior {

    public final UnitBehaviorType behaviorType;
    public final Map<String, String> args;

    public UnitBehavior(UnitBehaviorType behaviorType,
        Map<String, String> args) {
      this.behaviorType = behaviorType;
      this.args = Collections.unmodifiableMap(args);
    }
  }

  public static class UnitStart {

    public final String unitType;
    public final TilePoint relativePosition;
    public final UnitBehavior behavior;

    public UnitStart(String unitType, TilePoint relativePosition,
        UnitBehavior behavior) {
      this.unitType = unitType;
      this.relativePosition = relativePosition;
      this.behavior = behavior;
    }
  }

  public static class Formation {

    public final List<TilePoint> spawnPoints;
    public final List<UnitStart> units;

    public Formation(List<TilePoint> spawnPoints, List<UnitStart> units) {
      this.spawnPoints = Collections.unmodifiableList(spawnPoints);
      this.units = Collections.unmodifiableList(units);
    }

    public TilePoint getUnitPos(int spawnIdx, int unitIdx) {
      return spawnPoints.get(spawnIdx).add(units.get(unitIdx).relativePosition);
    }
  }

  public static class Intel {

    //TODO Add reported and actual accuracy values, misidentification, false positive, etc.
    //TODO Allow items to persist between levels.
    public final int formationSpottedIdx;
    public final int numberOfUnits;
    public final SpotType spotType;

    public Intel(int formationSpottedIdx, int numberOfUnits,
        SpotType spotType) {
      this.formationSpottedIdx = formationSpottedIdx;
      this.numberOfUnits = numberOfUnits;
      this.spotType = spotType;
    }
  }

  public static class ShopItem {

    public final String name;
    public final int cost;
    public final String description;
    public final List<Intel> effects;

    public ShopItem(String name, int cost, String description,
        List<Intel> effects) {
      this.name = name;
      this.cost = cost;
      this.description = description;
      this.effects = Collections.unmodifiableList(effects);
    }
  }

  public static class UnitAllotment {

    public final String type;
    public final int count;

    public UnitAllotment(String type, int count) {
      this.type = type;
      this.count = count;
    }
  }
}
