package com.axlan.gdxtactics;

import com.axlan.gdxtactics.logic.PathSearch;
import com.axlan.gdxtactics.logic.PathSearch.PathSearchNode;
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
import java.util.ArrayList;

public class PathSearchDemo extends Game implements InputProcessor {

  private static TileNode goal;
  private static TileNode start;
  private ShapeRenderer shapeRenderer;
  private GridPoint2 tileSize = new GridPoint2(10, 10);
  private TileNode[][] tiles;

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Keys.ENTER) {
      ArrayList<PathSearchNode> path = PathSearch.aStarSearch(start, goal);
      if (path == null) {
        initializeTileStates();
        System.out.println("Goal not reachable");
        return false;
      }
      for (PathSearchNode node : path) {
        ((TileNode) node).state = TileState.START;
      }

    } else if (keycode == Keys.R) {
      initializeTileStates();
    }
    return false;
  }

  private void touchHelper(int screenX, int screenY, boolean clear) {
    int posX = screenX / tileSize.x;
    int posY = screenY / tileSize.y;
    if (posX < 0 || posX >= tiles.length || posY < 0 || posY >= tiles[0].length) {
      return;
    }
    if (!clear) {
      if (tiles[posX][posY].state == TileState.OPEN) {
        tiles[posX][posY].state = TileState.BLOCKED;
      }
    } else {
      if (tiles[posX][posY].state == TileState.BLOCKED) {
        tiles[posX][posY].state = TileState.OPEN;
      }
    }
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
    touchHelper(screenX, screenY, button != Input.Buttons.LEFT);
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    touchHelper(screenX, screenY, !Gdx.input.isButtonPressed(Input.Buttons.LEFT));
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  private void initializeTileStates() {

    for (TileNode[] row : tiles) {
      for (TileNode tile : row) {
        tile.state = TileState.OPEN;
      }
    }
    tiles[10][10].state = TileState.START;
    tiles[40][40].state = TileState.END;
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
    tiles = new TileNode[tileWidth][tileHeight];
    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        tiles[r][c] = new TileNode();
      }
    }

    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        TileNode tile = tiles[r][c];
        tile.pos = new GridPoint2(r, c);
        if (r < tileWidth - 1) {
          tile.neighbors.add(tiles[r + 1][c]);
        }
        if (r > 0) {
          tile.neighbors.add(tiles[r - 1][c]);
        }
        if (c < tileHeight - 1) {
          tile.neighbors.add(tiles[r][c + 1]);
        }
        if (c > 0) {
          tile.neighbors.add(tiles[r][c - 1]);
        }
      }
    }
    tiles[10][10].state = TileState.START;
    tiles[40][40].state = TileState.END;
    goal = tiles[40][40];
    start = tiles[10][10];
    Gdx.input.setInputProcessor(this);
  }

  @Override
  public void render() {
    super.render();
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    shapeRenderer.begin(ShapeType.Filled);
    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        switch (tiles[r][c].state) {
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

  static class TileNode implements PathSearchNode {

    GridPoint2 pos;
    ArrayList<TileNode> neighbors = new ArrayList<>();
    TileState state = TileState.OPEN;

    @Override
    public int heuristics() {
      return Math.abs(goal.pos.x - pos.x) + Math.abs(goal.pos.y - pos.y);
    }

    @Override
    public ArrayList<PathSearchNode> getNeighbors() {
      ArrayList<PathSearchNode> tmp = new ArrayList<>();
      for (TileNode neighbor : neighbors) {
        if (neighbor.state != TileState.BLOCKED) {
          tmp.add(neighbor);
        }
      }
      return tmp;
    }
  }

  enum TileState {
    OPEN,
    BLOCKED,
    START,
    END
  }
}
