package com.axlan.gdxtactics.models;

import com.badlogic.gdx.Gdx;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Settings {

  public final String levelDataFile;
  public final String unitStatsDataFile;
  public final Sprites sprites;

  private Settings(String levelDataFile, String unitStatsDataFile,
      Sprites sprites) {
    this.levelDataFile = levelDataFile;
    this.unitStatsDataFile = unitStatsDataFile;
    this.sprites = sprites;
  }

  /**
   * This method deserializes the JSON read from the specified path into a LevelData object
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

  public static final class Sprites {

    public final String spriteAtlasFile;
    public final float spriteFrameDuration;
    public final int spriteMovementFramesPerTile;

    private Sprites(String spriteAtlasFile, float spriteFrameDuration,
        int spriteMovementFramesPerTile) {
      this.spriteAtlasFile = spriteAtlasFile;
      this.spriteFrameDuration = spriteFrameDuration;
      this.spriteMovementFramesPerTile = spriteMovementFramesPerTile;
    }
  }
}
