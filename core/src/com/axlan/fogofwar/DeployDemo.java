package com.axlan.fogofwar;

import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.screens.DeployView;
import com.axlan.gdxtactics.CompletionObserver;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

/**
 * Class to run a demo of the {@link DeployView}
 */
public class DeployDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();
    LevelData levelData = LoadedResources.getLevelData();
    LoadedResources.getGameStateManager().gameState.playerResources.addMoney((int) 1e10);
    LoadedResources.getGameStateManager()
        .gameState
        .playerResources
        .makePurchase(levelData.shopItems.get(0));
    LoadedResources.getGameStateManager()
        .gameState
        .playerResources
        .makePurchase(levelData.shopItems.get(1));
    this.setScreen(
        new DeployView(
            new CompletionObserver() {
              @Override
              public void onDone() {
              }
            }));
  }
}
