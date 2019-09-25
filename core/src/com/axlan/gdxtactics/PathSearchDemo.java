package com.axlan.gdxtactics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import java.util.Arrays;

public class PathSearchDemo extends Game implements InputProcessor {

  ShapeRenderer shapeRenderer;
  GridPoint2 tileSize = new GridPoint2(10, 10);
  TileState[][] tiles;

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Keys.ENTER) {

    }
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
    int posX = screenX / tileSize.x;
    int posY = screenY / tileSize.y;
    if (posX < 0 || posX >= tiles.length || posY < 0 || posY >= tiles[0].length) {
      return false;
    }
    if (button == Input.Buttons.LEFT) {
      if (tiles[posX][posY] == TileState.OPEN) {
        tiles[posX][posY] = TileState.BLOCKED;
      }
    } else {
      if (tiles[posX][posY] == TileState.BLOCKED) {
        tiles[posX][posY] = TileState.OPEN;
      }
    }

    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    int posX = screenX / tileSize.x;
    int posY = screenY / tileSize.y;
    if (posX < 0 || posX >= tiles.length || posY < 0 || posY >= tiles[0].length) {
      return false;
    }

    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
      if (tiles[posX][posY] == TileState.OPEN) {
        tiles[posX][posY] = TileState.BLOCKED;
      }
    } else {
      if (tiles[posX][posY] == TileState.BLOCKED) {
        tiles[posX][posY] = TileState.OPEN;
      }
    }
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

  @Override
  public void create() {
    shapeRenderer = new ShapeRenderer();
    int tileWidth = Gdx.graphics.getWidth() / tileSize.x;
    int tileHeight = Gdx.graphics.getHeight() / tileSize.y;
    tiles = new TileState[tileWidth][tileHeight];
    for (TileState[] row : tiles) {
      Arrays.fill(row, TileState.OPEN);
    }
    tiles[10][10] = TileState.START;
    tiles[40][40] = TileState.END;
    Gdx.input.setInputProcessor(this);
  }

  @Override
  public void render() {
    super.render();
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    shapeRenderer.begin(ShapeType.Filled);
    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        switch (tiles[r][c]) {
          case OPEN:
            shapeRenderer.setColor(Color.WHITE);
            break;
          case BLOCKED:
            shapeRenderer.setColor(Color.BLACK);
            break;
          case START:
            shapeRenderer.setColor(Color.GREEN);
            break;
          case END:
            shapeRenderer.setColor(Color.RED);
            break;
        }
        int x = r * tileSize.x;
        int y = Gdx.graphics.getHeight() - (c + 1) * tileSize.y;

        shapeRenderer.rect(x, y, tileSize.x, tileSize.y);

      }
    }
    shapeRenderer.end();
  }

  enum TileState {
    OPEN,
    BLOCKED,
    START,
    END
  }
}
