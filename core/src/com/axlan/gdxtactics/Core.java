package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.models.UnitStats;
import com.axlan.gdxtactics.screens.BattleView;
import com.axlan.gdxtactics.screens.BriefingView;
import com.axlan.gdxtactics.screens.CompletionObserver;
import com.axlan.gdxtactics.screens.DeployView;
import com.axlan.gdxtactics.screens.StoreView;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.GridPoint2;
import com.kotcrab.vis.ui.VisUI;
import java.util.HashMap;

public class Core extends Game {

  private final PlayerResources playerResources = new PlayerResources();
  private LevelData levelData;
  private int[] enemySpawnSelections;
  private HashMap<GridPoint2, String> placements;
  private DeployView deployView;
  private HashMap<String, UnitStats> unitStats;

  private void showStore() {
    CompletionObserver observer =
        new CompletionObserver() {
          @Override
          public void onDone() {
            showDeployMap();
          }
        };
    StoreView storeView = new StoreView(observer, levelData, playerResources);
    this.setScreen(storeView);
  }

  private void showBriefing() {
    CompletionObserver observer =
        new CompletionObserver() {
          @Override
          public void onDone() {
            showStore();
          }
        };
    BriefingView briefingView = new BriefingView(observer, levelData);
    this.setScreen(briefingView);
  }

  private void showDeployMap() {
    CompletionObserver observer =
        new CompletionObserver() {
          @Override
          public void onDone() {
            showBattleMap();
          }
        };
    deployView = new DeployView(observer, levelData, playerResources);
    this.setScreen(deployView);
  }

  private void showBattleMap() {
    this.setScreen(new BattleView(levelData, unitStats, playerResources,
        deployView.getDeploymentSelections()));
    deployView = null;
  }

  @Override
  public void create() {
    VisUI.load();

    this.levelData = LevelData.loadFromJson("data/levels/demo.json");
    this.unitStats = UnitStats.loadFromJson("data/units/stats.json");

    this.showBriefing();
  }
}
