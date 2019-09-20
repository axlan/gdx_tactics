package com.axlan.gdxtactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

public class Core extends ApplicationAdapter implements InputProcessor {
  private Stage stage;

  private SpriteBatch batch;
  private Sprite sprite;

  private StoreView storeView;
  private BriefingView briefingView;

  private void showStore(LevelData levelData) {
    this.storeView.setData(levelData, new PlayerResources());
    stage.clear();
    stage.addActor(storeView.rootTable);
  }

  private void showBriefing(LevelData levelData) {
    this.briefingView.setLevelData(levelData);
    stage.clear();
    stage.addActor(briefingView.rootTable);
  }

  @Override
  public void create () {
    VisUI.load();
    stage = new Stage(new ScreenViewport());

    final LevelData levelData = LevelData.loadFromJson("levels/demo.json");

    CompletionObserver observer = new CompletionObserver() {
      @Override
      public void onDone() {
        showStore(levelData);
      }
    };
    this.briefingView = new BriefingView(observer);

    observer = new CompletionObserver() {
      @Override
      public void onDone() {
        stage.clear();
      }
    };
    this.storeView = new StoreView(observer);


    this.showBriefing(levelData);

    batch = new SpriteBatch();
    sprite = new Sprite(new Texture(Gdx.files.internal("badlogic.jpg")));
    sprite.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

    Timer.schedule(new Timer.Task() {
      @Override
      public void run() {
        sprite.setFlip(false,!sprite.isFlipY());
      }
    },10,10,10000);


    // ORDER IS IMPORTANT!
    InputMultiplexer im = new InputMultiplexer(stage,this);
    Gdx.input.setInputProcessor(im);
  }

  @Override
  public void render () {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    batch.begin();
    sprite.draw(batch);
    batch.end();

    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
  }


  @Override
  public boolean keyDown(int keycode) {
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    sprite.setFlip(!sprite.isFlipX(),sprite.isFlipY());
    return true;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}
