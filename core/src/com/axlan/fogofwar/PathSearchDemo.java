package com.axlan.fogofwar;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.PathSearch;
import com.axlan.gdxtactics.PathSearch.PathSearchNode;
import com.axlan.gdxtactics.PathVisualizer;
import com.axlan.gdxtactics.TilePoint;
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
import java.util.List;

/**
 * Class to run a demo to test the functionality of {@link PathSearch} and {@link PathVisualizer}
 */
public class PathSearchDemo extends Game implements InputProcessor {

  private static DemoTileNode goal;
  private static DemoTileNode start;
  private final TilePoint tileSize = new TilePoint(32, 32);
  private ShapeRenderer shapeRenderer;
  private SpriteBatch spriteBatch;
  private PathVisualizer pathVisualizer;
  private final TilePoint startPoint = new TilePoint(1, 1);
  private ArrayList<TilePoint> foundPath;
  private final TilePoint goalPoint = new TilePoint(5, 5);
  private DemoTileNode[][] tiles;

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Keys.ENTER) {
      ArrayList<PathSearchNode> path = PathSearch.runSearchByGoal(start, goal);
      if (path == null) {
        initializeTileStates();
        System.out.println("Goal not reachable");
        return false;
      }
      foundPath = new ArrayList<>();
      for (PathSearchNode node : path) {
        foundPath.add(((DemoTileNode) node).pos);
      }
      pathVisualizer.startAnimation("tank", foundPath,
          LoadedResources.getSettings().sprites.movementDurationPerTile,
          LoadedResources.getSettings().sprites.frameDuration);
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
    if (tiles[posX][posY] != goal && tiles[posX][posY] != start) {
      tiles[posX][posY].blocked = !clear;
    }
  }

  private void initializeTileStates() {

    for (DemoTileNode[] row : tiles) {
      for (DemoTileNode tile : row) {
        tile.blocked = false;
      }
    }
    foundPath = null;
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

  @Override
  public void create() {
    LoadedResources.initializeGlobal();
    pathVisualizer = new PathVisualizer(tileSize, LoadedResources.getSpriteLookup());
    shapeRenderer = new ShapeRenderer();
    spriteBatch = new SpriteBatch();
    int tileWidth = Gdx.graphics.getWidth() / tileSize.x;
    int tileHeight = Gdx.graphics.getHeight() / tileSize.y;
    tiles = new DemoTileNode[tileWidth][tileHeight];
    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        tiles[r][c] = new DemoTileNode();
      }
    }

    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        DemoTileNode tile = tiles[r][c];
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
    goal = tiles[goalPoint.x][goalPoint.y];
    goal.setGoal();

    Gdx.input.setInputProcessor(this);
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
  public void render() {
    super.render();

    float delta = Gdx.graphics.getDeltaTime();
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    shapeRenderer.begin(ShapeType.Filled);
    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        if (tiles[r][c] == start) {
          shapeRenderer.setColor(Color.GREEN);
        } else if (tiles[r][c] == goal) {
          shapeRenderer.setColor(Color.RED);
        } else if (tiles[r][c].blocked) {
          shapeRenderer.setColor(Color.BLACK);
        } else {
          shapeRenderer.setColor(Color.WHITE);
        }
        int x = r * tileSize.x;
        int y = c * tileSize.y;

        shapeRenderer.rect(x, y, tileSize.x, tileSize.y);

        if (foundPath != null) {
          shapeRenderer.setColor(Color.BLUE);
          pathVisualizer.drawArrow(shapeRenderer, foundPath);
        }

      }
    }
    shapeRenderer.end();
    spriteBatch.begin();
    pathVisualizer.drawAnimatedSpritePath(delta, spriteBatch);
    spriteBatch.end();
  }

  /**
   * class to store description of 2D tile game map to search for shortest movement paths
   */
  public static class DemoTileNode implements PathSearchNode {

    private static DemoTileNode goal;
    final ArrayList<DemoTileNode> neighbors = new ArrayList<>();
    TilePoint pos;
    boolean blocked = false;

    /**
     * Sets this Node as the path search goal
     */
    void setGoal() {
      DemoTileNode.goal = this;
    }

    @Override
    public int heuristics() {
      return Math.abs(goal.pos.x - pos.x) + Math.abs(goal.pos.y - pos.y);
    }

    @Override
    public int edgeWeight(PathSearchNode neighbor) {
      return 1;
    }

    @Override
    public List<PathSearchNode> getNeighbors() {
      ArrayList<PathSearchNode> tmp = new ArrayList<>();
      for (DemoTileNode neighbor : neighbors) {
        if (!neighbor.blocked) {
          tmp.add(neighbor);
        }
      }
      return tmp;
    }

  }

}
