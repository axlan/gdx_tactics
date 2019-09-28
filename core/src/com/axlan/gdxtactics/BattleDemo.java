package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.GameStateManager;
import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.LoadedResources;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.screens.BattleView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;
import com.sun.tools.javac.util.List;
import java.util.HashMap;

public class BattleDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();
    LevelData levelData = LoadedResources.getLevelData();
    PlayerResources playerResources = GameStateManager.playerResources;
    playerResources.addMoney((int) 1e10);
    playerResources.makePurchase(levelData.shopItems.get(0));
    playerResources.makePurchase(levelData.shopItems.get(1));
    HashMap<TilePoint, String> playerUnitPlacements = new HashMap<>();
    playerUnitPlacements.put(new TilePoint(3, 6), "tank");
    playerUnitPlacements.put(new TilePoint(4, 6), "tank");
    GameStateManager.deploymentSelection
        .addDeployments(List.from(new Integer[]{0}), playerUnitPlacements);
    this.setScreen(new BattleView());
  }

}
