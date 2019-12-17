package com.axlan.fogofwar.models;

import com.axlan.fogofwar.screens.SceneLabel;
import com.axlan.gdxtactics.*;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

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
  private static GameStateManager gameStateManager;
  private static Runnable showSettings;
  private static Runnable showShop;

  public static OptionsMenu getOptionsMenu() {
    return new OptionsMenu(showSettings, gameStateManager);
  }

  public static Menu getShopMenu() {
    Menu shopMenu = new Menu("Intel Shop");
    MenuItem shopItem = new MenuItem("Open Shop");
    shopMenu.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            showShop.run();
          }
        });
    shopMenu.addItem(shopItem);
    return shopMenu;
  }

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
  public static AnimatedSprite<AtlasRegion> getAnimation(String sprite, Poses pose) {
    return spriteLookup.getAnimation(
        sprite, pose, LoadedResources.getReadOnlySettings().sprites.frameDuration, true);
  }

  /**
   * Get the mapping of unit types to their corresponding stats.
   */
  static Map<String, UnitStats> getUnitStats() {
    return unitStats;
  }

  /**
   * Load the resources used across all levels
   *
   * @param saveLoadObserver observer to call when a new save is loaded
   */
  public static void initializeGlobal(Consumer<SceneLabel> saveLoadObserver, Runnable showSettings, Runnable showShop) {

    EditableSettings.setDefaults(DEFAULT_SETTINGS_FILE);
    editableSettings = EditableSettings.loadFromJson(EDITABLE_SETTINGS_FILE);
    editableSettings.apply();
    readOnlySettings = ReadOnlySettings.loadFromJson(READ_ONLY_SETTINGS_FILE);
    //TODO-P2 use asset manager more widely, and actually monitor loading with progress bar or something
    FreeTypeFontGenerator.setMaxTextureSize(4096);
    FreeTypeFontScalingSkin.fontScaling = ((double) Gdx.graphics.getWidth()) / 2880.0;
    Skin skin = new FreeTypeFontScalingSkin(Gdx.files.internal("skins/custom/custom.json"));
    VisUI.load(skin);
    spriteLookup = new SpriteLookup(new TextureAtlas(readOnlySettings.sprites.atlasFile));
    unitStats =
        Collections.unmodifiableMap(UnitStats.loadFromJson(readOnlySettings.unitStatsDataFile));
    gameStateManager = new GameStateManager(saveLoadObserver);

    LoadedResources.showSettings = showSettings;
    LoadedResources.showShop = showShop;
  }

}
