package com.axlan.fogofwar.models;

import com.axlan.fogofwar.campaigns.CampaignBase;
import com.axlan.gdxtactics.GameStateManagerBase;
import com.axlan.gdxtactics.StringObserver;
import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * Class for storing the state of the current game session. State should be complete for saving and
 * reloading.
 */
public class GameStateManager extends GameStateManagerBase<GameState> {

  /**
   * Callback for updating the scene after a load
   */
  private final StringObserver loadSceneCallback;

  public GameStateManager(StringObserver loadSceneCallback) {
    this.loadSceneCallback = loadSceneCallback;
  }

  @Override
  protected Gson buildGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(CampaignBase.class, new CampaignBaseDeserializer());
    gsonBuilder.registerTypeAdapter(CampaignBase.class, new CampaignBaseSerializer());
    gsonBuilder.enableComplexMapKeySerialization();
    return gsonBuilder.create();
  }

  @Override
  protected GameState[] newGameStateArray(int length) {
    return new GameState[length];
  }

  @Override
  protected GameState newGameState(GameState orig) {
    return new GameState(orig);
  }

  @Override
  protected void fetchSavesFromPrefs() {
    //TODO-P2 use injection to make missing resources easier to replace
    if (Gdx.app != null) {
      fetchSavesFromPrefs(GameState.class);
    }
  }

  /**
   * Set the gamestate to the Json data in the specified file
   *
   * @param filepath external path to JSON file
   */
  public void loadFile(String filepath) {
    Reader handle;
    //TODO-P2 use injection to make missing resources easier to replace
    if (Gdx.app != null) {
      handle = Gdx.files.absolute(filepath).reader();
    } else {
      try {
        handle = new FileReader(filepath);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        handle = null;
      }
    }
    gameState = gson.fromJson(handle, GameState.class);
    loadSceneCallback.processString(gameState.scene);
  }

  @Override
  public void load(int slot) {
    super.load(slot);
    loadSceneCallback.processString(gameState.scene);
  }
}
