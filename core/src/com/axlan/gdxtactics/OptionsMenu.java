package com.axlan.gdxtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

/**
 * Class to provide in game menu options like saving and loading
 */
public class OptionsMenu extends Menu {

  /**
   * Callback to replace the current screen with the settings menu
   */
  private final Runnable showSettings;

  /**
   * GameStateManager for handling saves and loads
   */
  private GameStateManagerBase<?> gameStateManager;

  /**
   * Submenu to select save slot
   */
  private PopupMenu saveSubMenu;
  /**
   * Submenu to select load slot
   */
  private PopupMenu loadSubMenu;

  public PopupMenu getLoadMenu() {
    return loadSubMenu;
  }

  /**
   * Generate save/load menu items and callbacks based on current save slots
   */
  private void updateDataButtons() {
    String[] slotLabels = gameStateManager.getSlotLabels();

    saveSubMenu.clear();
    loadSubMenu.clear();
    boolean found = false;
    for (int i = 0; i < slotLabels.length; i++) {
      MenuItem item = new MenuItem(slotLabels[i]);
      saveSubMenu.addItem(item);
      final int slot = i;
      item.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              gameStateManager.save(slot);
              updateDataButtons();
            }
          });

      //noinspection StringEquality
      if (slotLabels[i] == GameStateManagerBase.EMPTY_LABEL) {
        continue;
      }
      found = true;
      item = new MenuItem(slotLabels[i]);
      item.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              gameStateManager.load(slot);
            }
          });
      loadSubMenu.addItem(item);
    }
    if (!found) {
      loadSubMenu.addItem(new MenuItem("No Saves"));
    }
  }

  public OptionsMenu(Runnable showSettings, GameStateManagerBase<?> gameStateManager) {
    super("Options");
    this.showSettings = showSettings;
    this.gameStateManager = gameStateManager;
    makeOptionsMenu();
    updateDataButtons();
  }

  private void makeOptionsMenu() {

    MenuItem saveItem = new MenuItem("Save");
    saveSubMenu = new PopupMenu();
    saveItem.setSubMenu(saveSubMenu);
    addItem(saveItem);

    MenuItem loadItem = new MenuItem("Load");
    loadSubMenu = new PopupMenu();
    loadItem.setSubMenu(loadSubMenu);
    addItem(loadItem);

    if (showSettings != null) {
      MenuItem settingsItem = new MenuItem("Settings");
      settingsItem.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              showSettings.run();
            }
          });
      addItem(settingsItem);
    }
    MenuItem quitItem = new MenuItem("Quit");
    quitItem.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            Gdx.app.exit();
          }
        });
    addItem(quitItem);
  }
}
