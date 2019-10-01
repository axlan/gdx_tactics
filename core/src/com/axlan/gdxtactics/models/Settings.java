package com.axlan.gdxtactics.models;

import com.badlogic.gdx.Gdx;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;

/**
 * Class for loading and storing settomgs from JSON. The structure of the JSON should mimic the
 * structure of the class.
 *
 * <p> All lists loaded are unmodifiable
 * <p> A static instance is managed by {@link LoadedResources}
 */
@SuppressWarnings({"WeakerAccess"})
public final class Settings {

  /**
   * JSON file to load level data from
   */
  public final String levelDataFile;
  /** JSON file to load unit stats from */
  public final String unitStatsDataFile;
  /**
   * Settings controlling how sprites are drawn
   */
  public final SpritesSettings sprites;

  private Settings(String levelDataFile, String unitStatsDataFile,
      SpritesSettings sprites) {
    this.levelDataFile = levelDataFile;
    this.unitStatsDataFile = unitStatsDataFile;
    this.sprites = sprites;
  }

  /**
   * This method deserializes the JSON read from the specified path into a Settings object
   *
   * @param projectPath path in the assets directory to JSON file to parse
   * @return a new instance of LevelData populated from the JSON file
   * @throws JsonIOException     if there was a problem reading from the Reader
   * @throws JsonSyntaxException if json is not a valid representation for an object of type
   */
  @SuppressWarnings("SameParameterValue")
  static Settings loadFromJson(String projectPath) {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapterFactory(new ImmutableListTypeAdapterFactory());
    Reader reader = Gdx.files.internal(projectPath).reader();
    return gson.create().fromJson(reader, Settings.class);
  }

  /**
   * Settings controlling how sprites are drawn
   */
  public static final class SpritesSettings {

    /**
     * Atlas file containing sprites
     */
    public final String atlasFile;
    /**
     * Seconds to show each frame in animated sprite
     */
    public final float frameDuration;
    /**
     * How many seconds should it take the sprite to cross each tile during movement
     */
    public final float movementDurationPerTile;

    @SuppressWarnings("unused")
    private SpritesSettings(String atlasFile, float frameDuration,
        float movementDurationPerTile) {
      this.atlasFile = atlasFile;
      this.frameDuration = frameDuration;
      this.movementDurationPerTile = movementDurationPerTile;
    }
  }
}
