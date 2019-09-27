package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.DeploymentSelection;
import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.LoadedResources;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.models.UnitStats;
import com.axlan.gdxtactics.screens.BattleView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;
import java.util.HashMap;
import java.util.Map;

public class BattleDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();
    LevelData levelData = LoadedResources.getLevelData();
    Map<String, UnitStats> unitStats = LoadedResources.getUnitStats();
    PlayerResources playerResources = new PlayerResources();
    playerResources.purchases.add(levelData.shopItems.get(0));
    playerResources.purchases.add(levelData.shopItems.get(1));
    HashMap<TilePoint, String> playerUnitPlacements = new HashMap<>();
    playerUnitPlacements.put(new TilePoint(3, 6), "tank");
    playerUnitPlacements.put(new TilePoint(4, 6), "tank");
    DeploymentSelection selection = new DeploymentSelection(new int[]{0}, playerUnitPlacements);
    this.setScreen(new BattleView(levelData, unitStats, playerResources, selection));
  }

}
