package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.logic.PathSearch;
import com.axlan.gdxtactics.logic.PathSearch.PathSearchNode;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleView extends TiledScreen {


  List<TilePoint> getShortestPath(BattleTileNode start, BattleTileNode goal) {
    start.goal = goal;
    ArrayList<PathSearchNode> path = PathSearch.aStarSearch(start, goal);
    ArrayList<TilePoint> points = new ArrayList<>();
    for (PathSearchNode node : path) {
      points.add(((BattleTileNode) node).pos);
    }
    return points;
  }

  private Map<String, UnitStats> unitStats;
  private HashMap<TilePoint, FieldedUnit> playerUnits = new HashMap<>();
  private TilePoint selected = null;
  private TilePoint moving = null;
  private float elapsedTime = 0;
  private PathVisualizer pathVisualizer;

  public BattleView() {
    super("maps/" + LoadedResources.getLevelData().mapName + ".tmx");
    this.unitStats = LoadedResources.getUnitStats();
    pathVisualizer = new PathVisualizer(new TilePoint((int) tileSize.x, (int) tileSize.y));
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
        //TODO-P1 Make enemy units not passable
        //TODO-P1 Cache steps from these calculations
        BattleTileNode start = new BattleTileNode(selected);
        BattleTileNode goal = new BattleTileNode(playerPos);
        List<TilePoint> points = getShortestPath(start, goal);
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
        BattleTileNode start = new BattleTileNode(selected);
        BattleTileNode goal = new BattleTileNode(playerPos);
        List<TilePoint> points = getShortestPath(start, goal);
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

  /**
   * class to store description of 2D tile game map to search for shortest movement paths
   */
  public class BattleTileNode implements PathSearchNode {

    TilePoint pos;
    private BattleTileNode goal = null;

    BattleTileNode(TilePoint pos) {
      this.pos = pos;
    }

    BattleTileNode(TilePoint pos, BattleTileNode goal) {
      this.pos = pos;
      this.goal = goal;
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
      if (pos.x < numTiles.x - 1) {
        TilePoint neighborPos = pos.add(1, 0);
        if (isTilePassable(neighborPos)) {
          tmp.add(new BattleTileNode(neighborPos, goal));
        }
      }
      if (pos.x > 0) {
        TilePoint neighborPos = pos.sub(1, 0);
        if (isTilePassable(neighborPos)) {
          tmp.add(new BattleTileNode(neighborPos, goal));
        }
      }
      if (pos.y < numTiles.y - 1) {
        TilePoint neighborPos = pos.add(0, 1);
        if (isTilePassable(neighborPos)) {
          tmp.add(new BattleTileNode(neighborPos, goal));
        }
      }
      if (pos.y > 0) {
        TilePoint neighborPos = pos.sub(0, 1);
        if (isTilePassable(neighborPos)) {
          tmp.add(new BattleTileNode(neighborPos, goal));
        }
      }
      return tmp;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || o.getClass() != this.getClass()) {
        return false;
      }
      BattleTileNode g = (BattleTileNode) o;
      return this.pos.equals(g.pos);
    }

    @Override
    public int hashCode() {
      return pos.hashCode();
    }
  }
}
