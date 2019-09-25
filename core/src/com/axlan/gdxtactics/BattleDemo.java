package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.DeploymentSelection;
import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.models.UnitStats;
import com.axlan.gdxtactics.screens.BattleView;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.GridPoint2;
import com.kotcrab.vis.ui.VisUI;
import java.util.HashMap;

public class BattleDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    LevelData levelData = LevelData.loadFromJson("data/levels/demo.json");
    HashMap<String, UnitStats> unitStats = UnitStats.loadFromJson("data/units/stats.json");
    PlayerResources playerResources = new PlayerResources();
    playerResources.purchases.add(levelData.shopItems[0]);
    playerResources.purchases.add(levelData.shopItems[1]);
    HashMap<GridPoint2, String> playerUnitPlacements = new HashMap<>();
    playerUnitPlacements.put(new GridPoint2(3, 6), "tank");
    playerUnitPlacements.put(new GridPoint2(4, 6), "tank");
    DeploymentSelection selection = new DeploymentSelection(new int[]{0}, playerUnitPlacements);
    this.setScreen(new BattleView(levelData, unitStats, playerResources, selection));
  }

}
