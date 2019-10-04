package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.models.DeploymentSelection;
import com.axlan.gdxtactics.models.FieldedUnit;
import com.axlan.gdxtactics.models.FieldedUnit.State;
import com.axlan.gdxtactics.models.GameStateManager;
import com.axlan.gdxtactics.models.LoadedResources;
import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.models.UnitStats;
import com.axlan.gdxtactics.screens.SpriteLookup.Poses;
import com.badlogic.gdx.Gdx;
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
  private HashMap<TilePoint, FieldedUnit> playerUnits = new HashMap<>();
  /**
   * Key to {@link #playerUnits} for the unit currently being issued a command
   */
  private TilePoint selected = null;
  /** Key to {@link #playerUnits} for the unit currently in a movement animation */
  private TilePoint moving = null;
  /** Keeps track of time for selecting frames for animations */
  private float elapsedTime = 0;
  /** Used to draw potential paths and movement animations on the map */
  private PathVisualizer pathVisualizer;

  public BattleView() {
    super("maps/" + LoadedResources.getLevelData().mapName + ".tmx",
        LoadedResources.getSettings().tilesPerScreenWidth,
        LoadedResources.getSettings().cameraSpeed,
        LoadedResources.getSettings().edgeScrollSize);
    Map<String, UnitStats> unitStats = LoadedResources.getUnitStats();
    pathVisualizer = new PathVisualizer(getTilePixelSize());
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
        sprite = SpriteLookup.getAnimation(unit.stats.type,
            Poses.LEFT);
      } else if (unit.state == State.IDLE || unit.state == State.DONE) {
        sprite = SpriteLookup.getAnimation(unit.stats.type,
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
    if (selected != null) {
      TilePoint playerPos = screenToTile(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
      if (!playerPos.equals(selected) && isTilePassable(playerPos)) {
        //TODO-P1 Make enemy units not passable
        //TODO-P1 Cache steps from these calculations
        List<TilePoint> points = getShortestPath(selected, playerPos);
        shapeRenderer.setColor(Color.BLUE);
        pathVisualizer.drawArrow(shapeRenderer, points);
      }
    }
    shapeRenderer.end();
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    TilePoint playerPos = screenToTile(new Vector2(screenX, screenY));

    if (selected != null) {
      if (!playerPos.equals(selected) && isTilePassable(playerPos)) {
        FieldedUnit unit = playerUnits.get(selected);
        unit.state = State.MOVING;
        playerUnits.remove(selected);
        playerUnits.put(playerPos, unit);
        moving = playerPos;
        //TODO-P1 Make enemy units not passable
        //TODO-P1 Cache steps from these calculations
        List<TilePoint> points = getShortestPath(selected, playerPos);
        pathVisualizer.startAnimation(unit.stats.type, points,
            LoadedResources.getSettings().sprites.movementDurationPerTile,
            LoadedResources.getSettings().sprites.frameDuration);
      } else {
        playerUnits.get(selected).state = State.IDLE;
      }
      selected = null;
    }
    if (playerUnits.containsKey(playerPos) && playerUnits.get(playerPos).state == State.IDLE) {
      selected = playerPos;
      playerUnits.get(playerPos).state = State.SELECTED;
    }

    return super.touchDown(screenX, screenY, pointer, button);
  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  public void updateScreen(float delta) {
    elapsedTime += delta;
  }

}
