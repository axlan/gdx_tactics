package com.axlan.gdxtactics.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;
import java.util.Map;

//TODO Reorganize assets shared across game to make getting / setting clearer without passing around tons of references
public class LevelData {

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
  public GridPoint2 cameraCenter;

  /** Description of a page of briefing dialogue */
  public static class BriefPage {

    /** The identification for the speaker */
    public String speaker;
    /** The page of dialogue */
    public String dialogue;
  }

  public UnitAllotment[] playerUnits;

  public static class UnitBehavior {

    public UnitBehaviorType behaviorType;
    public Map<String, String> args;
  }

  public static class UnitStart {

    public String unitType;
    public GridPoint2 relativePosition;
    public UnitBehavior behavior;
  }

  public static class Formation {

    public GridPoint2[] spawnPoints;
    public UnitStart[] units;

    public GridPoint2 getUnitPos(int spawnIdx, int unitIdx) {
      return spawnPoints[spawnIdx].cpy().add(units[unitIdx].relativePosition);
    }
  }

  public static class Intel {

    //TODO Add reported and actual accuracy values, misidentification, false positive, etc.
    //TODO Allow items to persist between levels.
    public int formationSpottedIdx;
    public int numberOfUnits;
    public SpotType spotType;
  }

  public String briefSetting;
  public BriefPage[] briefPages;
  public ShopItem[] shopItems;

  public static class ShopItem {

    public String name;
    public int cost;
    public String description;
    public Intel[] effects;
  }

  public GridPoint2[] playerSpawnPoints;

  public static class UnitAllotment {

    public String type;
    public int count;
  }

  public Formation[] enemyFormations;
  public String mapName;

  /**
   * This method deserializes the JSON read from the specified path into a LevelData object
   *
   * @param projectPath path in the assets directory to JSON file to parse
   * @return a new instance of LevelData populated from the JSON file
   * @throws JsonIOException if there was a problem reading from the Reader
   * @throws JsonSyntaxException if json is not a valid representation for an object of type
   */
  public static LevelData loadFromJson(String projectPath) {
    Gson gson = new Gson();
    Reader reader = Gdx.files.internal(projectPath).reader();
    return gson.fromJson(reader, LevelData.class);
  }
}
