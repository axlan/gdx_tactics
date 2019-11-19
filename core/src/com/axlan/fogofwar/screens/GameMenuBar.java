package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

// TODO-P2 make this more generic and move to gdxtactics

/**
 * Class to provide in game menu options like saving and loading
 */
public class GameMenuBar extends MenuBar {

    /**
     * Callback to replace the current screen with the settings menu
     */
    private static CompletionObserver showSettings;
    /**
     * Submenu to select save slot
     */
    private PopupMenu saveSubMenu;
    /**
     * Submenu to select load slot
     */
    private PopupMenu loadSubMenu;

    GameMenuBar() {
        super();
        this.addMenu(makeOptionsMenu());
        updateDataButtons();
    }

    /**
     * Sets the callback to use. Must be set before any instance of GameMenuBar is created.
     *
     * @param showSettings Callback to replace the current screen with the settings menu
     */
    public static void setShowSettings(CompletionObserver showSettings) {
        GameMenuBar.showSettings = showSettings;
    }

    /**
     * Generate save/load menu items and callbacks based on current save slots
     */
    private void updateDataButtons() {
        String[] slotLabels = GameStateManager.getSlotLabels();

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
                            GameStateManager.save(slot);
                            updateDataButtons();
                        }
                    });

            //noinspection StringEquality
            if (slotLabels[i] == GameStateManager.EMPTY_LABEL) {
                continue;
            }
            found = true;
            item = new MenuItem(slotLabels[i]);
            item.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            GameStateManager.load(slot);
                        }
                    });
            loadSubMenu.addItem(item);
        }
        if (!found) {
            loadSubMenu.addItem(new MenuItem("No Saves"));
        }
    }

    private Menu makeOptionsMenu() {
        Menu optionsMenu = new Menu("Options");

        MenuItem saveItem = new MenuItem("Save");
        saveSubMenu = new PopupMenu();
        saveItem.setSubMenu(saveSubMenu);
        optionsMenu.addItem(saveItem);

        MenuItem loadItem = new MenuItem("Load");
        loadSubMenu = new PopupMenu();
        loadItem.setSubMenu(loadSubMenu);
        optionsMenu.addItem(loadItem);

        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        showSettings.onDone();
                    }
                });
        optionsMenu.addItem(settingsItem);

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Gdx.app.exit();
                    }
                });
        optionsMenu.addItem(quitItem);
        return optionsMenu;
    }
}
