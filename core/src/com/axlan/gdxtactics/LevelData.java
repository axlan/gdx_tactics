package com.axlan.gdxtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;
import java.util.Map;

public class LevelData {

  enum UnitBehaviorType {
    MOVE,
    DEFEND,
    ATTACK
  }

  /** Description of a page of briefing dialogue */
  public static class BriefPage {
    /** The identification for the speaker */
    public String speaker;
    /** The page of dialogue */
    public String dialogue;
  }

  public static class ShopItem {
    public String name;
    public int cost;
    public String description;
  }

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
  }

  public String briefSetting;
  public BriefPage[] briefPages;
  public ShopItem[] shopItems;
  public GridPoint2 cameraCenter;
  public GridPoint2[] playerSpawnPoints;
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
