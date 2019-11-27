package com.axlan.fogofwar;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.screens.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.kotcrab.vis.ui.VisUI;

import java.util.function.Consumer;

// TODO-P2 Add campaign component. You have set pool of troops and decide how many to commit to each
// battle. They will take time to be usable again or have penalty of fuel.
// TODO-P3 Add random scenario generation.
// TODO-P2 Add music and sound effects.
// TODO-P2 Add unit tests with CI. (Probably easiest with web output, and Selenium?)
// TODO-P3 Use AssetManager to properly load and unload assets to fix "finished with non-zero exit
// value -1"
// TODO-P3 Add proper logging framework

/**
 * Main class for game. Supervises switching between the views and handling shared resources.
 */
public class Core extends Game {

  private FogGameMenuBar menuBar;

  private final String forceLoadSaveFile;

  public Core() {
    super();
    forceLoadSaveFile = null;
  }

  public Core(String forceLoadSaveFile) {
    super();
    this.forceLoadSaveFile = forceLoadSaveFile;
  }

  /**
   * Switch the screen to the {@link SettingsScreen}
   */
  private void showSettings() {
    SettingsScreen storeView = new SettingsScreen(getResumeCompletionObserver());
    this.setScreen(storeView);
  }

  /**
   * Switch the screen to the {@link TitleScreen}
   */
  private void showTitle() {
    Consumer<TitleScreen.TitleSelection> observer =
        val -> {
          switch (val) {
            case NEW_GAME:
              showNewGame();
              break;
            case QUIT:
              Gdx.app.exit();
              break;
            case SETTINGS:
              showSettings();
              break;
            case LOAD_GAME:
              // TODO-P1 Load game Menu
          }
        };
    TitleScreen titleScreen = new TitleScreen(observer);
    this.setScreen(titleScreen);
  }

  /**
   * Factory for Runnable that will return to the current screen
   * @return generated Runnable
   */
  private Runnable getResumeCompletionObserver() {
    Screen hiddenScreen = this.getScreen();
    return () -> setScreen(hiddenScreen);
  }

  /**
   * Switch the screen to the {@link StoreView}
   */
  private void showStore() {
    StoreView storeView = new StoreView(getResumeCompletionObserver());
    this.setScreen(storeView);
  }

  /**
   * Switch the screen to the {@link OverWorldMap}
   */
  private void showCampaignMap() {
    LoadedResources.getGameStateManager().gameState.scene = SceneLabel.CAMPAIGN_MAP;
    Runnable observer = () -> {
      LoadedResources.getGameStateManager().gameState.scene = SceneLabel.PRE_BATTLE_BRIEF;
      showBriefing();
    };
    OverWorldMap overWorldMap = new OverWorldMap(observer, menuBar);
    this.setScreen(overWorldMap);
  }


  /**
   * Switch the screen to the {@link BriefingView}
   */
  private void showBriefing() {
    Runnable observer =
        () -> {
          switch (LoadedResources.getGameStateManager().gameState.scene) {
            case PRE_BATTLE_BRIEF:
              showBattleMap();
              break;
            case PRE_MAP_BRIEF:
            default:
              showCampaignMap();
              break;
          }
        };
    BriefingView briefingView = new BriefingView(observer);
    this.setScreen(briefingView);
  }

  /**
   * Switch the screen to the {@link BriefingView}
   */
  private void showNewGame() {
    Runnable observer =
        () -> {
          if (LoadedResources.getGameStateManager().gameState != null) {
            LoadedResources.getGameStateManager().gameState.scene = SceneLabel.PRE_MAP_BRIEF;
            showBriefing();
          } else {
            showTitle();
          }
        };
    NewGameView newGameView = new NewGameView(observer);
    this.setScreen(newGameView);
  }


  /**
   * Switch the screen to the {@link DeployView}
   */
  private void showDeployMap() {
    LoadedResources.getGameStateManager().gameState.scene = SceneLabel.DEPLOY_MAP;
    Runnable observer = this::showBattleMap;
    DeployView deployView = new DeployView(observer);
    this.setScreen(deployView);
  }

  /** Switch the screen to the {@link BattleView} */
  private void showBattleMap() {
    LoadedResources.getGameStateManager().gameState.scene = SceneLabel.BATTLE_MAP;
    this.setScreen(new BattleView(menuBar));
  }

  /**
   * Switch to the correct scene after loading a save file
   *
   * @param scene string identifier of scene to start
   */
  private void resumeSceneForLoad(SceneLabel scene) {
    switch (scene) {
      case BATTLE_MAP:
        showBattleMap();
        break;
      case DEPLOY_MAP:
        showDeployMap();
        break;
      case PRE_MAP_BRIEF:
      case PRE_BATTLE_BRIEF:
        showBriefing();
        break;
      case CAMPAIGN_MAP:
        showCampaignMap();
        break;
    }
  }

  @Override
  public void create() {

    VisUI.load();
    // TODO-P2 load custom skin
    LoadedResources.initializeGlobal(this::resumeSceneForLoad);
    // Set to callback to be able to show the settings menu from other screens
    Runnable menuSettingsCallback = this::showSettings;
    // Set to callback to be able to show the settings menu from other screens
    Runnable menuShopCallback = this::showStore;
    menuBar = new FogGameMenuBar(menuSettingsCallback, menuShopCallback, LoadedResources.getGameStateManager());
    if (forceLoadSaveFile == null) {
      this.showTitle();
    } else {
      LoadedResources.getGameStateManager().loadFile(forceLoadSaveFile);
    }
  }
}
