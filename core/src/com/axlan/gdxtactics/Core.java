package com.axlan.gdxtactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

public class Core extends ApplicationAdapter {
  private Stage stage;

  private ApplicationAdapter renderer;


  private StoreView storeView;
  private BriefingView briefingView;
  private BattleMap battleMap;

  private void showStore(LevelData levelData, PlayerResources playerResources) {
    this.storeView.setData(levelData, playerResources);
    stage.clear();
    stage.addActor(storeView.rootTable);
  }

  private void showBriefing(LevelData levelData) {
    this.briefingView.setLevelData(levelData);
    stage.clear();
    stage.addActor(briefingView.rootTable);
  }

  private void showBattleMap(LevelData levelData, PlayerResources playerResources) {
    stage.clear();
    stage.addActor(battleMap.rootTable);
    renderer = battleMap;
    InputMultiplexer im = new InputMultiplexer(stage, battleMap);
    Gdx.input.setInputProcessor(im);
  }

  @Override
  public void create () {
    VisUI.load();
    stage = new Stage(new ScreenViewport());

    final PlayerResources playerResources = new PlayerResources();

    final LevelData levelData = LevelData.loadFromJson("levels/demo.json");

    CompletionObserver observer = new CompletionObserver() {
      @Override
      public void onDone() {
        showStore(levelData, playerResources);
      }
    };
    this.briefingView = new BriefingView(observer);

    observer = new CompletionObserver() {
      @Override
      public void onDone() {
        showBattleMap(levelData, playerResources);
      }
    };
    this.storeView = new StoreView(observer);


    this.showBriefing(levelData);

    // ORDER IS IMPORTANT!
    InputMultiplexer im = new InputMultiplexer(stage);
    Gdx.input.setInputProcessor(im);
  }

  @Override
  public void render () {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    if (renderer != null) {
      renderer.render();
    }

    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
  }


}
