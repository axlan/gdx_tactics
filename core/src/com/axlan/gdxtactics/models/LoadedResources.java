package com.axlan.gdxtactics.models;

import com.axlan.gdxtactics.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import java.util.Collections;
import java.util.Map;

/**
 * Class for managing resources loaded from filesystem.
 * <p><b>NOTE</b>: The initialization methods must be called at the start of the application.
 *
 * <P>All members of this class are static and immutable
 */
public final class LoadedResources {

  private static Settings settings;
  private static TextureAtlas textureAtlas;
  private static Map<String, UnitStats> unitStats;
  private static LevelData levelData;

  /**
   * Get the settings that describe the applications behavior
   */
  public static Settings getSettings() {
    return settings;
  }

  /** Get the TextureAtlas used to generate sprites */
  public static TextureAtlas getTextureAtlas() {
    return textureAtlas;
  }

  /** Get the mapping of unit types to their corresponding stats. */
  public static Map<String, UnitStats> getUnitStats() {
    return unitStats;
  }

  /** Get the description of the current level */
  public static LevelData getLevelData() {
    return levelData;
  }

  /** Load the resources used across all levels */
  public static void initializeGlobal() {
    settings = Settings.loadFromJson(Constants.SETTINGS_FILE);
    textureAtlas = new TextureAtlas(settings.sprites.atlasFile);
    unitStats = Collections.unmodifiableMap(UnitStats.loadFromJson(settings.unitStatsDataFile));
  }

  //TODO-P1 Add concept of multiple levels

  /** Load the resources for the current level */
  public static void initializeLevel() {
    levelData = LevelData.loadFromJson(settings.levelDataFile);
  }

}
