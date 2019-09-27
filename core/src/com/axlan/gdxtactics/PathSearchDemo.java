package com.axlan.gdxtactics;

import com.axlan.gdxtactics.logic.PathSearch;
import com.axlan.gdxtactics.logic.PathSearch.PathSearchNode;
import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.screens.PathVisualizer;
import com.axlan.gdxtactics.screens.TiledScreen.TileNode;
import com.axlan.gdxtactics.screens.TiledScreen.TileNode.TileState;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;

public class PathSearchDemo extends Game implements InputProcessor {

  private static TileNode goal;
  private static TileNode start;
  private ShapeRenderer shapeRenderer;
  private SpriteBatch spriteBatch;
  private TilePoint tileSize = new TilePoint(32, 32);
  private TileNode[][] tiles;
  private ArrayList<TilePoint> foundPath;
  private PathVisualizer pathVisualizer = new PathVisualizer(tileSize);
  private TilePoint startPoint = new TilePoint(1, 1);
  private TilePoint goalPoint = new TilePoint(5, 5);

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Keys.ENTER) {
      ArrayList<PathSearchNode> path = PathSearch.aStarSearch(start, goal);
      if (path == null) {
        initializeTileStates();
        System.out.println("Goal not reachable");
        return false;
      }
      foundPath = new ArrayList<>();
      for (PathSearchNode node : path) {
        foundPath.add(((TileNode) node).pos);
      }
      pathVisualizer.startAnimation("tank", foundPath, 10, 0.1f);
    } else if (keycode == Keys.R) {
      initializeTileStates();
    }
    return false;
  }

  private void touchHelper(int screenX, int screenY, boolean clear) {
    int posX = screenX / tileSize.x;
    int posY = tiles[0].length - screenY / tileSize.y - 1;
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
    foundPath = null;
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
    spriteBatch = new SpriteBatch();
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
        tile.pos = new TilePoint(r, c);
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
    start = tiles[startPoint.x][startPoint.y];
    start.state = TileState.START;
    goal = tiles[goalPoint.x][goalPoint.y];
    goal.setGoal();

    Gdx.input.setInputProcessor(this);
  }

  @Override
  public void render() {
    super.render();

    float delta = Gdx.graphics.getDeltaTime();
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
        int y = c * tileSize.y;

        shapeRenderer.rect(x, y, tileSize.x, tileSize.y);

        if (foundPath != null) {
          pathVisualizer.drawArrow(shapeRenderer, foundPath);
        }

      }
    }
    shapeRenderer.end();
    spriteBatch.begin();
    pathVisualizer.drawAnimatedSpritePath(delta, spriteBatch);
    spriteBatch.end();
  }

}
