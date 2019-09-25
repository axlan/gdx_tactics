package com.axlan.gdxtactics.models;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;
import java.util.HashMap;

public class UnitStats implements Cloneable {

  private String type;
  private int visionRange;
  private int maxHealth;
  private int currentHealth;
  private int attack;
  private int movement;

  /**
   * This method deserializes the JSON read from the specified path into a LevelData object
   *
   * @param projectPath path in the assets directory to JSON file to parse
   * @return a new list of Unit objects populated from the JSON file
   * @throws JsonIOException     if there was a problem reading from the Reader
   * @throws JsonSyntaxException if json is not a valid representation for an object of type
   */
  public static HashMap<String, UnitStats> loadFromJson(String projectPath) {
    Gson gson = new Gson();

    Reader reader = Gdx.files.internal(projectPath).reader();
    UnitStats[] statArray = gson.fromJson(reader, UnitStats[].class);
    HashMap<String, UnitStats> statMap = new HashMap<>(statArray.length);
    for (UnitStats stat : statArray) {
      statMap.put(stat.type, stat);
    }
    return statMap;
  }

  public String getType() {
    return type;
  }

  public int getVisionRange() {
    return visionRange;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public int getCurrentHealth() {
    return currentHealth;
  }

  public int getAttack() {
    return attack;
  }

  public int getMovement() {
    return movement;
  }

  @Override
  public UnitStats clone() {
    UnitStats clone = null;
    try {
      clone = (UnitStats) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return clone;
  }


}

