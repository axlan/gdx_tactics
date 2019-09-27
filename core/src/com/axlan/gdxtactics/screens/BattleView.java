package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.logic.PathSearch;
import com.axlan.gdxtactics.logic.PathSearch.PathSearchNode;
import com.axlan.gdxtactics.models.DeploymentSelection;
import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.models.UnitStats;
import com.axlan.gdxtactics.screens.FieldedUnit.State;
import com.axlan.gdxtactics.screens.SpriteLookup.Poses;
import com.axlan.gdxtactics.screens.TiledScreen.TileNode.TileState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BattleView extends TiledScreen {

  private Map<String, UnitStats> unitStats;
  private HashMap<TilePoint, FieldedUnit> playerUnits = new HashMap<>();
  private TilePoint selected = null;
  private TilePoint moving = null;
  private float elapsedTime = 0;
  private PathVisualizer pathVisualizer;

  public BattleView(LevelData levelData, Map<String, UnitStats> unitStats,
      PlayerResources playerResources, DeploymentSelection deploymentSelection) {
    super("maps/" + levelData.mapName + ".tmx");
    this.unitStats = unitStats;
    pathVisualizer = new PathVisualizer(new TilePoint((int) tileSize.x, (int) tileSize.y));
    for (TilePoint point : deploymentSelection.playerUnitPlacements.keySet()) {
      String unitType = deploymentSelection.playerUnitPlacements.get(point);
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
            Poses.LEFT, 0.1f);
      } else if (unit.state == State.IDLE || unit.state == State.DONE) {
        sprite = SpriteLookup.getAnimation(unit.stats.type,
            Poses.IDLE, 0.1f);
      }
      if (sprite != null) {
        Vector2 worldPos = tileToWorld(point);
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
        //TODO Make enemy units not passable
        //TODO Cache steps from these calculations
        TileNode[][] tileNodes = getTileNodes();
        TileNode start = tileNodes[selected.x][selected.y];
        TileNode goal = tileNodes[playerPos.x][playerPos.y];
        start.state = TileState.START;
        goal.setGoal();
        ArrayList<PathSearchNode> path = PathSearch.aStarSearch(start, goal);
        ArrayList<TilePoint> points = new ArrayList<>();
        for (PathSearchNode node : path) {
          points.add(((TileNode) node).pos);
        }
        pathVisualizer.drawArrow(shapeRenderer, points);
      }
    }
    shapeRenderer.end();
  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  public void updateScreen(float delta) {
    elapsedTime += delta;
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
        //TODO Make enemy units not passable
        //TODO Cache steps from these calculations
        TileNode[][] tileNodes = getTileNodes();
        TileNode start = tileNodes[selected.x][selected.y];
        TileNode goal = tileNodes[playerPos.x][playerPos.y];
        start.state = TileState.START;
        goal.setGoal();
        ArrayList<PathSearchNode> path = PathSearch.aStarSearch(start, goal);
        ArrayList<TilePoint> points = new ArrayList<>();
        for (PathSearchNode node : path) {
          points.add(((TileNode) node).pos);
        }
        pathVisualizer.startAnimation(unit.stats.type, points, 10, 0.1f);
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
}
