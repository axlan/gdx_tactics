package com.axlan.fogofwar.screens;

import com.axlan.gdxtactics.GameMenuBar;
import com.axlan.gdxtactics.GameStateManagerBase;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

public class FogGameMenuBar extends GameMenuBar {

  /**
   * Callback to replace the current screen with the shop menu
   */
  public FogGameMenuBar(Runnable showSettings, Runnable showShop, GameStateManagerBase<?> gameStateManager) {
    super(showSettings, gameStateManager);

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
    this.addMenu(shopMenu);
  }

}
