package com.axlan.fogofwar;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.screens.BattleView;
import com.axlan.fogofwar.screens.BriefingView;
import com.axlan.fogofwar.screens.CompletionObserver;
import com.axlan.fogofwar.screens.DeployView;
import com.axlan.fogofwar.screens.StoreView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

/**
 * Main class for game. Supervises switching between the views and handling shared resources.
 */
public class Core extends Game {

  /**
   * Switch the screen to the {@link StoreView}
   */
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

  /**
   * Switch the screen to the {@link BriefingView}
   */
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

  /**
   * Switch the screen to the {@link DeployView}
   */
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

  /**
   * Switch the screen to the {@link BattleView}
   */
  private void showBattleMap() {
    this.setScreen(new BattleView());
  }

  @Override
  public void create() {
    VisUI.load();
    //TODO-P2 load custom skin
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();

    this.showBriefing();
  }
}
