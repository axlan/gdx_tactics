package com.axlan.gdxtactics.models;

import com.axlan.gdxtactics.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import java.util.Collections;
import java.util.Map;

public final class LoadedResources {

  private static Settings settings;
  private static TextureAtlas textureAtlas;
  private static Map<String, UnitStats> unitStats;
  private static LevelData levelData;

  public static Settings getSettings() {
    return settings;
  }

  public static TextureAtlas getTextureAtlas() {
    return textureAtlas;
  }

  public static Map<String, UnitStats> getUnitStats() {
    return unitStats;
  }

  public static LevelData getLevelData() {
    return levelData;
  }

  public static void initializeGlobal() {
    settings = Settings.loadFromJson(Constants.SETTINGS_FILE);
    textureAtlas = new TextureAtlas(settings.sprites.spriteAtlasFile);
    unitStats = Collections.unmodifiableMap(UnitStats.loadFromJson(settings.unitStatsDataFile));
  }

  public static void initializeLevel() {
    levelData = LevelData.loadFromJson(settings.levelDataFile);
  }

}
