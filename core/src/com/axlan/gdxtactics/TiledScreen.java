package com.axlan.gdxtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

// TODO-P3 add capability for animated tiles

/**
 * Class for drawing and handling input for a 2D {@link TiledMap} with a {@link OrthographicCamera}
 */
public abstract class TiledScreen extends StageBasedScreen implements InputProcessor {

  protected final TiledMap map;
  private final SpriteBatch batch;
  private final OrthogonalTiledMapRenderer renderer;
  private final ShapeRenderer shapeRenderer;
  private final int tilesPerScreenWidth;
  private final float cameraSpeed;
  private final int edgeScrollSize;
  private final float[] cameraBounds = new float[4];
  private boolean keyMoveCameraToLeft = false;
  private boolean keyMoveCameraToTop = false;
  private boolean keyMoveCameraToRight = false;
  private boolean keyMoveCameraToBottom = false;
  private boolean mouseMoveCameraToLeft = false;
  private boolean mouseMoveCameraToTop = false;
  private boolean mouseMoveCameraToRight = false;
  private boolean mouseMoveCameraToBottom = false;
  private OrthographicCamera camera;

  /**
   * @param levelTmxFilename Location of TMX map use.
   *     <p>Layers from the map : 0 - under entities / 1+ - above entities
   * @param tilesPerScreenWidth This is the number of tiles that are visible at a time across the
   *     width of the screen. It sets the zoom level.
   * @param cameraSpeed How many pixels should the camera pan per second when scrolling across the
   *     map.
   * @param edgeScrollSize When the mouse is closer then this many pixels from the edge, scroll the
   *     map.
   */
  protected TiledScreen(
      String levelTmxFilename, int tilesPerScreenWidth, float cameraSpeed, int edgeScrollSize) {
    this.tilesPerScreenWidth = tilesPerScreenWidth;
    this.cameraSpeed = cameraSpeed;
    this.edgeScrollSize = edgeScrollSize;
    this.batch = new SpriteBatch();
    this.shapeRenderer = new ShapeRenderer();
    map = new TmxMapLoader().load(levelTmxFilename);

    renderer = new OrthogonalTiledMapRenderer(map, 1);
    Gdx.input.setInputProcessor(this);
    this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }

  /**
   * Render entities between background and foreground layers
   *
   * @param delta time since last update
   * @param batch batch to draw on. Begin must be called before use.
   * @param shapeRenderer shape render to draw on. Begin must be called before use.
   */
  protected abstract void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer);

  /**
   * Draw on top of all other elements, including any UI added to stage.
   *
   * @param delta time since last update
   * @param batch batch to draw on. Begin must be called before use.
   * @param shapeRenderer shape render to draw on. Begin must be called before use.
   */
  protected abstract void renderAboveUI(
      float delta, SpriteBatch batch, ShapeRenderer shapeRenderer);

  /**
   * Draw on top of background and foreground, but below UI elements
   *
   * @param delta         time since last update
   * @param batch         batch to draw on. Begin must be called before use.
   * @param shapeRenderer shape render to draw on. Begin must be called before use.
   */
  protected abstract void renderAboveForeground(
      float delta, SpriteBatch batch, ShapeRenderer shapeRenderer);

  /**
   * Update state before drawing
   *
   * @param delta time since last update
   */
  protected abstract void updateScreen(float delta);

  /* Utils methods */

  /**
   * Coverts from a pixel on the screen to a pixel before the camera transformation
   *
   * @param screenCoord pixel location on the screen
   * @return pixel location before the camera transformation
   */
  protected TilePoint screenToWorld(Vector2 screenCoord) {
    Vector3 proj = camera.unproject(new Vector3(screenCoord, 0));
    return new TilePoint(proj.x, proj.y);
  }

  /**
   * Coverts from a pixel before the camera transformation to a pixel on the screen
   *
   * @param worldCoord pixel location before the camera transformation
   * @return pixel location on the screen
   */
  @SuppressWarnings("WeakerAccess")
  Vector2 worldToScreen(Vector2 worldCoord) {
    Vector3 proj = camera.project(new Vector3(worldCoord, 0));
    return new Vector2(proj.x, proj.y);
  }

  /**
   * Coverts from a pixel on the screen to the tile index it was in
   *
   * @param screenCoord pixel location on the screen
   * @return tile index the screen pixel was in
   */
  protected TilePoint screenToTile(Vector2 screenCoord) {
    TilePoint worldCoord = screenToWorld(screenCoord);
    return worldCoord.divBy(getTilePixelSize());
  }

  /**
   * Coverts a tile index to a pixel before the camera transformation
   *
   * @param tileCoord index of a tile in the map
   * @return pixel location of corner of tile before the camera transformation
   */
  protected TilePoint tileToWorld(TilePoint tileCoord) {
    return tileCoord.mult(getTilePixelSize());
  }

  /**
   * Coverts a tile index to a pixel on screen
   *
   * @param tileCoord index of a tile in the map
   * @return pixel location of center of tile on screen
   */
  protected Vector2 tileToScreen(TilePoint tileCoord) {
    TilePoint worldCoordTilePoint = tileCoord.mult(getTilePixelSize()).add(getTilePixelSize().divBy(2));
    Vector2 worldCoord = worldCoordTilePoint.toVector2();
    return worldToScreen(worldCoord);
  }

  private void renderCamera(float delta) {

    // Translation
    if (mouseMoveCameraToLeft || keyMoveCameraToLeft) {
      camera.position.x -= cameraSpeed * delta;
    }
    if (mouseMoveCameraToTop || keyMoveCameraToTop) {
      camera.position.y += cameraSpeed * delta;
    }
    if (mouseMoveCameraToRight || keyMoveCameraToRight) {
      camera.position.x += cameraSpeed * delta;
    }
    if (mouseMoveCameraToBottom || keyMoveCameraToBottom) {
      camera.position.y -= cameraSpeed * delta;
    }

    // Position compared to cameraBounds
    if (camera.position.x < cameraBounds[0]) {
      camera.position.x = cameraBounds[0]; // left
    }
    if (camera.position.y > cameraBounds[1]) {
      camera.position.y = cameraBounds[1]; // top
    }
    if (camera.position.x > cameraBounds[2]) {
      camera.position.x = cameraBounds[2]; // right
    }
    if (camera.position.y < cameraBounds[3]) {
      camera.position.y = cameraBounds[3]; // bottom
    }

    // Clear screan
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // update camera & refresh view
    camera.update();
    renderer.setView(camera);
  }

  /**
   * Get the rectangle of pixels a tile is draw at before the camera transformation
   *
   * @param point index of tile
   * @return rectangle covering where the tile is draw before the camera transformation
   */
  protected Rectangle getTileWorldRect(TilePoint point) {
    TilePoint worldPoint = tileToWorld(point);
    return new Rectangle(worldPoint.x, worldPoint.y, getTilePixelSize().x, getTilePixelSize().y);
  }

  /**
   * @return The size of the world map in tiles
   */
  private TilePoint getMapTileSize() {
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    return new TilePoint(layer.getWidth(), layer.getHeight());
  }

  /**
   * @return The size of a tile in pixels
   */
  protected TilePoint getTilePixelSize() {
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    return new TilePoint(layer.getTileWidth(), layer.getTileWidth());
  }

  /**
   * @return The size of the world map in pixels
   */
  @SuppressWarnings("WeakerAccess")
  protected TilePoint getMapPixelSize() {
    return getMapTileSize().mult(getTilePixelSize());
  }

  @Override
  public void show() {
    InputMultiplexer im = new InputMultiplexer(stage, this);
    Gdx.input.setInputProcessor(im);
  }

  @Override
  public void render(float delta) {
    keyMoveCameraToLeft = (Gdx.input.isKeyPressed(Input.Keys.LEFT));
    keyMoveCameraToRight = (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
    keyMoveCameraToBottom = (Gdx.input.isKeyPressed(Input.Keys.DOWN));
    keyMoveCameraToTop = (Gdx.input.isKeyPressed(Input.Keys.UP));

    // Camera rendering
    renderCamera(delta);

    // Map rendering
    int[] backgroundLayers = {0};
    int[] foregroundLayers = new int[map.getLayers().size() - 1];
    for (int i = 1; i < map.getLayers().size(); i++) {
      foregroundLayers[i - 1] = i;
    }

    /* Update & Render entities between layers */
    updateScreen(delta);
    renderer.render(backgroundLayers);
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(
        GL20.GL_SRC_ALPHA,
        GL20.GL_ONE_MINUS_SRC_ALPHA); // <<< this line here makes the magic we're after

    this.batch.setProjectionMatrix(camera.combined);
    this.shapeRenderer.setProjectionMatrix(camera.combined);

    renderScreen(delta, this.batch, this.shapeRenderer);
    renderer.render(foregroundLayers);

    renderAboveForeground(delta, this.batch, this.shapeRenderer);

    stage.act(delta);
    stage.draw();

    renderAboveUI(delta, this.batch, this.shapeRenderer);
  }

  @Override
  public void resize(int width, int height) {

    float cameraZoom = ((float) tilesPerScreenWidth * getTilePixelSize().x) / (float) width;

    // Boundaries for the camera : left, top, right, bottom
    float horizontalMargin =
        (width * cameraZoom > getMapPixelSize().x)
            ? (width * cameraZoom - getMapPixelSize().x) / 2
            : 0;
    float verticalMargin =
        (height * cameraZoom > getMapPixelSize().y)
            ? (height * cameraZoom - getMapPixelSize().y) / 2
            : 0;
    cameraBounds[0] = ((float) width / 2 - horizontalMargin) * cameraZoom;
    cameraBounds[1] = getMapPixelSize().y - (float) height / 2 * cameraZoom + verticalMargin;
    cameraBounds[2] = getMapPixelSize().x - (float) width / 2 * cameraZoom + horizontalMargin;
    cameraBounds[3] = ((float) height / 2 - verticalMargin) * cameraZoom;
    camera = new OrthographicCamera(width, height); // (1080,720);
    camera.setToOrtho(false);
    camera.zoom = cameraZoom;
    super.resize(width, height);
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
    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();
    mouseMoveCameraToLeft = (screenX > 0 && screenX < edgeScrollSize);
    mouseMoveCameraToTop = (screenY > 0 && screenY < edgeScrollSize);
    mouseMoveCameraToRight = (screenX > width - edgeScrollSize && screenX < width);
    mouseMoveCameraToBottom = (screenY > height - edgeScrollSize && screenY < height);
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}
