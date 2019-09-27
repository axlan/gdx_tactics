package com.axlan.gdxtactics.models;

import com.badlogic.gdx.Gdx;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;
import java.util.HashMap;

@SuppressWarnings({"WeakerAccess", "unused"})
public class UnitStats implements Cloneable {

  public final String type;
  public final int visionRange;
  public final int maxHealth;
  public final int attack;
  public final int movement;

  public UnitStats(String type, int visionRange, int maxHealth, int attack, int movement) {
    this.type = type;
    this.visionRange = visionRange;
    this.maxHealth = maxHealth;
    this.attack = attack;
    this.movement = movement;
  }

  /**
   * This method deserializes the JSON read from the specified path into a LevelData object
   *
   * @param projectPath path in the assets directory to JSON file to parse
   * @return a new list of Unit objects populated from the JSON file
   * @throws JsonIOException     if there was a problem reading from the Reader
   * @throws JsonSyntaxException if json is not a valid representation for an object of type
   */
  static HashMap<String, UnitStats> loadFromJson(String projectPath) {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapterFactory(new ImmutableListTypeAdapterFactory());
    Reader reader = Gdx.files.internal(projectPath).reader();
    UnitStats[] statArray = gson.create().fromJson(reader, UnitStats[].class);

    HashMap<String, UnitStats> statMap = new HashMap<>(statArray.length);
    for (UnitStats stat : statArray) {
      statMap.put(stat.type, stat);
    }
    return statMap;
  }

}

