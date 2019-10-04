package com.axlan.fogofwar;

import com.axlan.fogofwar.models.GameStateManager;
import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.PlayerResources;
import com.axlan.fogofwar.screens.BattleView;
import com.axlan.gdxtactics.TilePoint;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;
import com.sun.tools.javac.util.List;
import java.util.HashMap;

/**
 * Class to run a demo of the {@link BattleView}
 */
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
        .setDeployments(List.from(new Integer[]{0}), playerUnitPlacements);
    this.setScreen(new BattleView());
  }

}