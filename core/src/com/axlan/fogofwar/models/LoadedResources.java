package com.axlan.fogofwar.models;

import com.axlan.gdxtactics.AnimatedSprite;
import com.axlan.gdxtactics.JsonLoader;
import com.axlan.gdxtactics.SpriteLookup;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import java.util.Collections;
import java.util.Map;

/**
 * Class for managing resources loaded from filesystem.
 *
 * <p><b>NOTE</b>: The initialization methods must be called at the start of the application.
 *
 * <p>All members of this class are static and immutable
 */
public final class LoadedResources {

  private static final String READ_ONLY_SETTINGS_FILE = "data/read_only_settings.json";
  private static final String EDITABLE_SETTINGS_FILE = "session/settings.json";
  private static final String DEFAULT_SETTINGS_FILE = "data/default_settings.json";

  private static ReadOnlySettings readOnlySettings;
  private static EditableSettings editableSettings;
  private static SpriteLookup spriteLookup;
  private static Map<String, UnitStats> unitStats;
  private static LevelData levelData;
  private static GameStateManager gameStateManager;

  /**
   * Get the class for handling the game state and saving / loading
   *
   * @return class for handling the game state and saving / loading * @return
   */
  public static GameStateManager getGameStateManager() {
    return gameStateManager;
  }

  /**
   * Get the read only settings that describe the applications behavior
   */
  public static ReadOnlySettings getReadOnlySettings() {
    return readOnlySettings;
  }

  /**
   * Get the editable settings that describe the applications behavior
   */
  public static EditableSettings getEditableSettings() {
    return editableSettings;
  }

  /**
   * Write the editable settings to JSON file
   */
  public static void writeEditableSettings() {
    JsonLoader.writeToJsonFile(EDITABLE_SETTINGS_FILE, editableSettings);
  }

  /** Get the SpriteLookup used to generate sprites */
  public static SpriteLookup getSpriteLookup() {
    return spriteLookup;
  }

  /**
   * Load an AnimatedSprite by name and pose using default setting for frameDuration and reverse
   *
   * @param sprite name of sprite
   * @param pose pose of sprite
   * @return Corresponding AnimatedSprite
   */
  public static AnimatedSprite<AtlasRegion> getAnimation(String sprite, Poses pose) {
    return spriteLookup.getAnimation(
            sprite, pose, LoadedResources.getReadOnlySettings().sprites.frameDuration, true);
  }

  /** Get the mapping of unit types to their corresponding stats. */
  static Map<String, UnitStats> getUnitStats() {
    return unitStats;
  }

  /** Get the description of the current level */
  public static LevelData getLevelData() {
    return levelData;
  }

  /** Load the resources used across all levels */
  public static void initializeGlobal() {
    EditableSettings.setDefaults(DEFAULT_SETTINGS_FILE);
    editableSettings = EditableSettings.loadFromJson(EDITABLE_SETTINGS_FILE);
    editableSettings.apply();
    readOnlySettings = ReadOnlySettings.loadFromJson(READ_ONLY_SETTINGS_FILE);
    spriteLookup = new SpriteLookup(new TextureAtlas(readOnlySettings.sprites.atlasFile));
    unitStats =
            Collections.unmodifiableMap(UnitStats.loadFromJson(readOnlySettings.unitStatsDataFile));
    gameStateManager = new GameStateManager();
  }

  // TODO-P1 Add concept of multiple levels

  /** Load the resources for the current level */
  public static void initializeLevel() {
    levelData = LevelData.loadFromJson(readOnlySettings.levelDataFile);
  }
}
