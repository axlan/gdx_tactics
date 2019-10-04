package com.axlan.fogofwar.models;

import com.axlan.gdxtactics.AnimatedSprite;
import com.axlan.gdxtactics.SpriteLookup;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import java.util.Collections;
import java.util.Map;

/**
 * Class for managing resources loaded from filesystem.
 * <p><b>NOTE</b>: The initialization methods must be called at the start of the application.
 *
 * <P>All members of this class are static and immutable
 */
public final class LoadedResources {

  private static final String SETTINGS_FILE = "data/settings.json";

  private static Settings settings;
  private static SpriteLookup spriteLookup;
  private static Map<String, UnitStats> unitStats;
  private static LevelData levelData;

  /**
   * Get the settings that describe the applications behavior
   */
  public static Settings getSettings() {
    return settings;
  }

  /**
   * Get the SpriteLookup used to generate sprites
   */
  public static SpriteLookup getSpriteLookup() {
    return spriteLookup;
  }

  /**
   * Load an AnimatedSprite by name and pose using default setting for frameDuration and reverse
   *
   * @param sprite name of sprite
   * @param pose   pose of sprite
   * @return Corresponding AnimatedSprite
   */
  public static AnimatedSprite<AtlasRegion> getAnimation(
      String sprite, Poses pose) {
    return spriteLookup
        .getAnimation(sprite, pose, LoadedResources.getSettings().sprites.frameDuration, true);
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
    settings = Settings.loadFromJson(SETTINGS_FILE);
    spriteLookup = new SpriteLookup(new TextureAtlas(settings.sprites.atlasFile));
    unitStats = Collections.unmodifiableMap(UnitStats.loadFromJson(settings.unitStatsDataFile));
  }

  //TODO-P1 Add concept of multiple levels

  /** Load the resources for the current level */
  public static void initializeLevel() {
    levelData = LevelData.loadFromJson(settings.levelDataFile);
  }

}
