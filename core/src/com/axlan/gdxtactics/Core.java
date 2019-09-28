package com.axlan.gdxtactics;

import com.axlan.gdxtactics.models.LoadedResources;
import com.axlan.gdxtactics.screens.BattleView;
import com.axlan.gdxtactics.screens.BriefingView;
import com.axlan.gdxtactics.screens.CompletionObserver;
import com.axlan.gdxtactics.screens.DeployView;
import com.axlan.gdxtactics.screens.StoreView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

public class Core extends Game {

  private void showStore() {
    CompletionObserver observer =
        new CompletionObserver() {
          @Override
          public void onDone() {
            showDeployMap();
          }
        };
    StoreView storeView = new StoreView(observer);
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
    BriefingView briefingView = new BriefingView(observer);
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
    DeployView deployView = new DeployView(observer);
    this.setScreen(deployView);
  }

  private void showBattleMap() {
    this.setScreen(new BattleView());
  }

  @Override
  public void create() {
    VisUI.load();
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();

    this.showBriefing();
  }
}
