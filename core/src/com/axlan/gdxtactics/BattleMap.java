package com.axlan.gdxtactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.kotcrab.vis.ui.widget.VisTable;

public class BattleMap extends ApplicationAdapter implements InputProcessor {

  final VisTable rootTable = new VisTable();
  private boolean active = false;

  BattleMap() {

  }


  @Override
  public void render () {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//    batch.begin();
//    sprite.draw(batch);
//    batch.end();
//
//    stage.act(Gdx.graphics.getDeltaTime());
//    stage.draw();
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
    //sprite.setFlip(!sprite.isFlipX(),sprite.isFlipY());
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
