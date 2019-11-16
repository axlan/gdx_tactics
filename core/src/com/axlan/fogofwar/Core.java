package com.axlan.fogofwar;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.screens.*;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

//TODO-P2 Add campaign component. You have set pool of troops and decide how many to commit to each battle. They will take time to be usable again or have penalty of fuel.
//TODO-P3 Add random scenario generation.
//TODO-P2 Add music and sound effects.
//TODO-P2 Add unit tests with CI. (Probably easiest with web output, and Selenium?)

/**
 * Main class for game. Supervises switching between the views and handling shared resources.
 */
public class Core extends Game {

    /**
     * Switch the screen to the {@link TitleScreen}
     */
    private void showTitle() {
        TitleSelectionObserver observer =
                new TitleSelectionObserver() {
                    @Override
                    public void onDone(TitleSelection selection) {
                        switch (selection) {
                            case NEW_GAME:
                                showBriefing();
                                break;
                            case LOAD_GAME:
                                //TODO-P1 Load game Menu
                            case SETTINGS:
                                //TODO-P1 Settings menu
                                throw new RuntimeException("Not Implemented");
                        }
                    }
                };
        TitleScreen titleScreen = new TitleScreen(observer);
        this.setScreen(titleScreen);
    }

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

      this.showTitle();
  }
}
