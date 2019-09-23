package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.screens.DeployView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

public class DeployDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    LevelData levelData = LevelData.loadFromJson("levels/demo.json");
    PlayerResources playerResources = new PlayerResources();
    playerResources.purchases.add(levelData.shopItems[0]);
    playerResources.purchases.add(levelData.shopItems[1]);
    this.setScreen(new DeployView(levelData, playerResources));
  }

}
