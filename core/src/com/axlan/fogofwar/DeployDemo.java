package com.axlan.fogofwar;

import com.axlan.fogofwar.models.GameStateManager;
import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.screens.CompletionObserver;
import com.axlan.fogofwar.screens.DeployView;
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
      GameStateManager.gameState.playerResources.addMoney((int) 1e10);
      GameStateManager.gameState.playerResources.makePurchase(levelData.shopItems.get(0));
      GameStateManager.gameState.playerResources.makePurchase(levelData.shopItems.get(1));
    this.setScreen(new DeployView(new CompletionObserver() {
      @Override
      public void onDone() {

      }
    }));
  }

}
