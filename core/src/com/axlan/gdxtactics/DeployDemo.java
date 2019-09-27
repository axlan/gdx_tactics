package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.LoadedResources;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.screens.CompletionObserver;
import com.axlan.gdxtactics.screens.DeployView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

public class DeployDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();
    LevelData levelData = LoadedResources.getLevelData();
    PlayerResources playerResources = new PlayerResources();
    playerResources.purchases.add(levelData.shopItems.get(0));
    playerResources.purchases.add(levelData.shopItems.get(1));
    this.setScreen(new DeployView(new CompletionObserver() {
      @Override
      public void onDone() {

      }
    }, levelData, playerResources));
  }

}
