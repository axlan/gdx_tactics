package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.DeploymentSelection;
import com.axlan.fogofwar.models.FieldedUnit;
import com.axlan.fogofwar.models.FieldedUnit.State;
import com.axlan.fogofwar.models.GameStateManager;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.UnitStats;
import com.axlan.gdxtactics.AnimatedSprite;
import com.axlan.gdxtactics.PathVisualizer;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.axlan.gdxtactics.TilePoint;
import com.axlan.gdxtactics.TiledScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A screen to play out the turn based battle. Player commands their troops against enemy AI.
 */
public class BattleView extends TiledScreen {

  /**
   * Mapping of points on the map, to the players units on that tile.
   */
  private final HashMap<TilePoint, FieldedUnit> playerUnits = new HashMap<>();
  /**
   * Key to {@link #playerUnits} for the unit currently being issued a command
   */
  private TilePoint selected = null;
  /** Key to {@link #playerUnits} for the unit currently in a movement animation */
  private TilePoint moving = null;
  /** Keeps track of time for selecting frames for animations */
  private float elapsedTime = 0;
  /** Used to draw potential paths and movement animations on the map */
  private final PathVisualizer pathVisualizer;
  /**
   * Path from {@link #selected} to the mouse location
   */
  private List<TilePoint> selectedUnitPath = null;

  public BattleView() {
    super("maps/" + LoadedResources.getLevelData().mapName + ".tmx",
        LoadedResources.getSettings().tilesPerScreenWidth,
        LoadedResources.getSettings().cameraSpeed,
        LoadedResources.getSettings().edgeScrollSize);
    Map<String, UnitStats> unitStats = LoadedResources.getUnitStats();
    pathVisualizer = new PathVisualizer(getTilePixelSize(), LoadedResources.getSpriteLookup());
    DeploymentSelection deploymentSelection = GameStateManager.deploymentSelection;
    for (TilePoint point : deploymentSelection.getPlayerUnitPlacements().keySet()) {
      String unitType = deploymentSelection.getPlayerUnitPlacements().get(point);
      playerUnits.put(point, new FieldedUnit(unitStats.get(unitType)));
    }

  }

  @Override
  public void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
    batch.begin();
    for (TilePoint point : playerUnits.keySet()) {
      FieldedUnit unit = playerUnits.get(point);
      AnimatedSprite<AtlasRegion> sprite = null;
      if (unit.state == State.SELECTED) {
        sprite = LoadedResources.getAnimation(unit.stats.type,
            Poses.LEFT);
      } else if (unit.state == State.IDLE || unit.state == State.DONE) {
        sprite = LoadedResources.getAnimation(unit.stats.type,
            Poses.IDLE);
      }
      if (sprite != null) {
        TilePoint worldPos = tileToWorld(point);
        sprite.setPosition(worldPos.x, worldPos.y);
        if (unit.state == State.DONE) {
          sprite.setColor(Color.GRAY);
        }
        sprite.draw(batch, elapsedTime);
      }
    }
    if (moving != null) {
      if (pathVisualizer.drawAnimatedSpritePath(delta, batch)) {
        FieldedUnit unit = playerUnits.get(moving);
        unit.state = State.DONE;
        moving = null;
      }
    }
    batch.end();
    shapeRenderer.begin(ShapeType.Filled);
    if (selectedUnitPath != null) {
      shapeRenderer.setColor(Color.BLUE);
      pathVisualizer.drawArrow(shapeRenderer, selectedUnitPath);
    }
    shapeRenderer.end();
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    TilePoint playerPos = screenToTile(new Vector2(screenX, screenY));

    if (selected != null && selectedUnitPath != null) {
      if (!playerUnits.containsKey(playerPos) && isTilePassable(playerPos)) {
        FieldedUnit unit = playerUnits.get(selected);
        unit.state = State.MOVING;
        playerUnits.remove(selected);
        playerUnits.put(playerPos, unit);
        moving = playerPos;
        pathVisualizer.startAnimation(unit.stats.type, selectedUnitPath,
            LoadedResources.getSettings().sprites.movementDurationPerTile,
            LoadedResources.getSettings().sprites.frameDuration);
      } else {
        playerUnits.get(selected).state = State.IDLE;
      }
      selected = null;
      selectedUnitPath = null;
    }
    if (playerUnits.containsKey(playerPos) && playerUnits.get(playerPos).state == State.IDLE) {
      selected = playerPos;
      playerUnits.get(playerPos).state = State.SELECTED;
    }

    return super.touchDown(screenX, screenY, pointer, button);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    super.mouseMoved(screenX, screenY);
    if (selected != null) {
      TilePoint playerPos = screenToTile(new Vector2(screenX, screenY));
      // Only recalculate path when mouse has moved onto new tile.
      if (selectedUnitPath != null && playerPos.equals(
          selectedUnitPath.get(selectedUnitPath.size() - 1))) {
        return false;
      }
      if (!playerPos.equals(selected) && isTilePassable(playerPos)) {
        selectedUnitPath = getShortestPath(selected, playerPos);
      }
    }
    return false;
  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  public void updateScreen(float delta) {
    elapsedTime += delta;
  }

}
