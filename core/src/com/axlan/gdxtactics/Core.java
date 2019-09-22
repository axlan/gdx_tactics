package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.screens.BriefingView;
import com.axlan.gdxtactics.screens.CompletionObserver;
import com.axlan.gdxtactics.screens.DeployView;
import com.axlan.gdxtactics.screens.StoreView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

public class Core extends Game {

  private final PlayerResources playerResources = new PlayerResources();
  private LevelData levelData;

  private void showStore() {
    CompletionObserver observer =
        new CompletionObserver() {
          @Override
          public void onDone() {
            showBattleMap();
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

  private void showBattleMap() {
    this.setScreen(new DeployView(levelData, playerResources));
  }

  @Override
  public void create() {
    VisUI.load();

    levelData = LevelData.loadFromJson("levels/demo.json");

    this.showBriefing();
  }
}
