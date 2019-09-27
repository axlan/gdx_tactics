package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.LoadedResources;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.screens.BattleView;
import com.axlan.gdxtactics.screens.BriefingView;
import com.axlan.gdxtactics.screens.CompletionObserver;
import com.axlan.gdxtactics.screens.DeployView;
import com.axlan.gdxtactics.screens.StoreView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;
import java.util.HashMap;

public class Core extends Game {

  private final PlayerResources playerResources = new PlayerResources();
  private int[] enemySpawnSelections;
  private HashMap<TilePoint, String> placements;
  private DeployView deployView;

  private void showStore() {
    CompletionObserver observer =
        new CompletionObserver() {
          @Override
          public void onDone() {
            showDeployMap();
          }
        };
    StoreView storeView = new StoreView(observer, LoadedResources.getLevelData(), playerResources);
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
    BriefingView briefingView = new BriefingView(observer, LoadedResources.getLevelData());
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
    deployView = new DeployView(observer, LoadedResources.getLevelData(), playerResources);
    this.setScreen(deployView);
  }

  private void showBattleMap() {
    this.setScreen(new BattleView(LoadedResources.getLevelData(), LoadedResources.getUnitStats(),
        playerResources,
        deployView.getDeploymentSelections()));
    deployView = null;
  }

  @Override
  public void create() {
    VisUI.load();
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();

    this.showBriefing();
  }
}
