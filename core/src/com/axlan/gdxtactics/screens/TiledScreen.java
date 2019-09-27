package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.logic.PathSearch.PathSearchNode;
import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.screens.TiledScreen.TileNode.TileState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;


public abstract class TiledScreen extends StageBasedScreen implements InputProcessor {

  boolean moveCameraToLeft = false;
  boolean moveCameraToTop = false;
  boolean moveCameraToRight = false;
  boolean moveCameraToBottom = false;
  private SpriteBatch batch;
  OrthographicCamera camera;
  private TiledMap map;
  private OrthogonalTiledMapRenderer renderer;
  private ShapeRenderer shapeRenderer;
  private ArrayList<TiledMapTileLayer> layers = new ArrayList<>();
  private Vector2 screenSize;
  Vector2 tileSize;
  TilePoint numTiles;
  private float[] cameraBounds = new float[4];
  private Vector3 cameraPosition;

  /* Methods */

  TiledScreen(String levelTmxFilename) {
    float cameraZoom = .5f;
    this.batch = new SpriteBatch();
    this.shapeRenderer = new ShapeRenderer();
    map = new TmxMapLoader().load(levelTmxFilename);

    /* Layers from the map : 0 - under entities / 1+ - above entities */
    for (int i = 0; i < map.getLayers().getCount(); i++) {
      layers.add((TiledMapTileLayer) map.getLayers().get(i));
    }

    Vector2 worldSize = new Vector2(layers.get(0).getWidth(), layers.get(0).getHeight());
    numTiles = new TilePoint((int) worldSize.x, (int) worldSize.y);
    screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    tileSize = new Vector2(layers.get(0).getTileWidth(), layers.get(0).getTileWidth());

    worldSize = worldSize.scl(tileSize);

    // Boundaries for the camera : left, top, right, bottom
    float horizontalMargin =
        (screenSize.x * cameraZoom > worldSize.x) ? (screenSize.x * cameraZoom - worldSize.x) / 2
            : 0;
    float verticalMargin =
        (screenSize.y * cameraZoom > worldSize.y) ? (screenSize.y * cameraZoom - worldSize.y) / 2
            : 0;
    cameraBounds[0] = (screenSize.x / 2 - horizontalMargin) * cameraZoom;
    cameraBounds[1] = worldSize.y - screenSize.y / 2 * cameraZoom + verticalMargin;
    cameraBounds[2] = worldSize.x - screenSize.x / 2 * cameraZoom + horizontalMargin;
    cameraBounds[3] = (screenSize.y / 2 - verticalMargin) * cameraZoom;

    System.out.println(worldSize);
    System.out.println(horizontalMargin);
    System.out.println(verticalMargin);
    System.out.println(cameraBounds[0]);
    System.out.println(cameraBounds[1]);
    System.out.println(cameraBounds[2]);
    System.out.println(cameraBounds[3]);

    renderer = new OrthogonalTiledMapRenderer(map, 1);
    camera = new OrthographicCamera(screenSize.x, screenSize.y); // (1080,720);
    camera.setToOrtho(false);
    camera.zoom = cameraZoom;
    cameraPosition = camera.position;
    Gdx.input.setInputProcessor(this);
  }

  /* To-Override Methods */

  /**
   * Meant to be overrided : render & update entities between layers
   */
  public abstract void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer);

  public abstract void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer);

  public abstract void updateScreen(float delta);

  TileNode[][] getTileNodes() {
    int tileWidth = numTiles.x;
    int tileHeight = numTiles.y;
    TileNode[][] tiles = new TileNode[tileWidth][tileHeight];
    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        tiles[r][c] = new TileNode();
      }
    }

    for (int r = 0; r < tiles.length; r++) {
      for (int c = 0; c < tiles[r].length; c++) {
        TileNode tile = tiles[r][c];
        tile.pos = new TilePoint(r, c);
        if (!isTilePassable(tile.pos)) {
          tile.state = TileState.BLOCKED;
        }
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
    return tiles;
  }

  boolean isTilePassable(TilePoint point) {
    if (point.x < 0 || point.x >= numTiles.x || point.y < 0 || point.y >= numTiles.y) {
      return false;
    }
    TiledMapTileLayer tileLayer = (TiledMapTileLayer) getMap().getLayers().get(0);
    return (boolean) tileLayer.getCell(point.x, point.y).getTile().getProperties().get("passable");
  }

  /* Utils methods */

  Vector2 screenToWorld(Vector2 screenCoord) {
    Vector3 proj = camera.unproject(new Vector3(screenCoord, 0));
    return new Vector2(proj.x, proj.y);
  }

  Vector2 worldToScreen(Vector2 worldCoord) {
    Vector3 proj = camera.project(new Vector3(worldCoord, 0));
    return new Vector2(proj.x, proj.y);
  }

  TilePoint screenToTile(Vector2 screenCoord) {
    Vector2 worldCoord = screenToWorld(screenCoord);
    int tileX = (int) (worldCoord.x / tileSize.x);
    int tileY = (int) (worldCoord.y / tileSize.y);
    return new TilePoint(tileX, tileY);
  }

  Vector2 tileToWorld(TilePoint tileCoord) {
    return new Vector2(tileCoord.x, tileCoord.y).scl(tileSize);
  }

  Vector2 tileToScreen(TilePoint tileCoord) {
    Vector2 worldCoord = new Vector2(tileCoord.x, tileCoord.y).scl(tileSize);
    return worldToScreen(worldCoord);
  }

  private void renderCamera(float delta) {
    float cameraSpeed = 300;

    // Translation
    if (moveCameraToLeft) {
      cameraPosition.x -= cameraSpeed * delta;
    }
    if (moveCameraToTop) {
      cameraPosition.y += cameraSpeed * delta;
    }
    if (moveCameraToRight) {
      cameraPosition.x += cameraSpeed * delta;
    }
    if (moveCameraToBottom) {
      cameraPosition.y -= cameraSpeed * delta;
    }

    // Position compared to cameraBounds
    if (cameraPosition.x < cameraBounds[0]) {
      cameraPosition.x = cameraBounds[0]; // left
    }
    if (cameraPosition.y > cameraBounds[1]) {
      cameraPosition.y = cameraBounds[1]; // top
    }
    if (cameraPosition.x > cameraBounds[2]) {
      cameraPosition.x = cameraBounds[2]; // right
    }
    if (cameraPosition.y < cameraBounds[3]) {
      cameraPosition.y = cameraBounds[3]; // bottom
    }

    // Update camera position
    camera.position.set(cameraPosition);

    // Clear screan
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // update camera & refresh view
    camera.update();
    renderer.setView(camera);
  }

  /* Getters & Setters */

  public TiledMap getMap() {
    return map;
  }

  public Rectangle getTileRect(TilePoint point) {
    Vector2 worldPoint = tileToWorld(point);
    return new Rectangle(worldPoint.x, worldPoint.y, tileSize.x, tileSize.y);
  }


  /* Implemented com.axlan.gdxtactics.Game.Screen Methods */

  @Override
  public void show() {
    InputMultiplexer im = new InputMultiplexer(stage, this);
    Gdx.input.setInputProcessor(im);
  }

  @Override
  public void render(float delta) {
//    Gdx.gl.glEnable(GL20.GL_BLEND);
//    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

//    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);   // <<< this line here makes the magic we're after


    // Camera rendering
    renderCamera(delta);

    // Map rendering
    int[] backgroundLayers = {0};
    int[] foregroundLayers = new int[layers.size() - 1];
    for (int i = 1; i < layers.size(); i++) {
      foregroundLayers[i - 1] = i;
    }

    /* Update & Render entities between layers */
    renderer.render(backgroundLayers);
    updateScreen(delta);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,
        GL20.GL_ONE_MINUS_SRC_ALPHA);   // <<< this line here makes the magic we're after

    this.batch.setProjectionMatrix(camera.combined);
    this.shapeRenderer.setProjectionMatrix(camera.combined);

    renderScreen(delta, this.batch, this.shapeRenderer);
    renderer.render(foregroundLayers);

    stage.act(delta);
    stage.draw();

    renderAboveUI(delta, this.batch, this.shapeRenderer);
  }

  public static class TileNode implements PathSearchNode {

    private static TileNode goal;
    public TilePoint pos;
    public ArrayList<TileNode> neighbors = new ArrayList<>();
    public TileState state = TileState.OPEN;

    public void setGoal() {
      TileNode.goal = this;
      this.state = TileState.END;
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
    public ArrayList<PathSearchNode> getNeighbors() {
      ArrayList<PathSearchNode> tmp = new ArrayList<>();
      for (TileNode neighbor : neighbors) {
        if (neighbor.state != TileState.BLOCKED) {
          tmp.add(neighbor);
        }
      }
      return tmp;
    }

    public enum TileState {
      OPEN,
      BLOCKED,
      START,
      END
    }
  }

  @Override
  public void resize(int width, int height) {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void dispose() {
  }

  /* Implemented InputProcessor Methods */

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

    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    // Vector3 clickCoordinates = new Vector3(screenX,screenY,0);
    // cameraPosition = camera.unproject(clickCoordinates);
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    moveCameraToLeft = (screenX > 0 && screenX < tileSize.x);
    moveCameraToTop = (screenY > 0 && screenY < tileSize.y);
    moveCameraToRight = (screenX > screenSize.x - tileSize.x && screenX < screenSize.x);
    moveCameraToBottom = (screenY > screenSize.y - tileSize.y && screenY < screenSize.y);
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}
