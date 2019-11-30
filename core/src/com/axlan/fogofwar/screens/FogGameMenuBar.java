package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.GameMenuBar;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuItem;

public class FogGameMenuBar extends GameMenuBar {

  private final MenuItem shopItem;

  /**
   * Callback to replace the current screen with the shop menu
   */
  public FogGameMenuBar(Runnable showSettings, Runnable showShop) {
    super(showSettings, LoadedResources.getGameStateManager());

    Menu shopMenu = new Menu("Intel Shop");
    shopItem = new MenuItem("Open Shop");
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

  @Override
  public Table getTable() {
    shopItem.setDisabled(LoadedResources.getGameStateManager().gameState.campaign.getItems().isEmpty());
    return super.getTable();
  }
}
