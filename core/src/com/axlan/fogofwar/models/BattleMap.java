package com.axlan.fogofwar.models;

import static com.axlan.gdxtactics.Utilities.listGet2d;

import com.axlan.gdxtactics.PathSearch;
import com.axlan.gdxtactics.PathSearch.AStarSearchResult;
import com.axlan.gdxtactics.PathSearch.PathSearchNode;
import com.axlan.gdxtactics.TilePoint;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class for querying information about the current battle map (pathing, tile properties, etc.)
 */
public class BattleMap {


  /**
   * The length and width of the map in tiles
   */
  private final TilePoint mapSize;
  /**
   * Unmodifiable 2D list of the properties of each tile in the map
   */
  private final List<List<TileProperties>> tileProperties;
  /**
   * Goal for current path search
   */
  private TilePoint tileNodeGoal = null;
  /**
   * Context for current path search
   */
  private Object tileNodeContext = null;

  public BattleMap(final TiledMap map) {
    TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
    mapSize = new TilePoint(layer.getWidth(), layer.getHeight());

    TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(0);
    ArrayList<List<TileProperties>> tmpTileProperties = new ArrayList<>();
    for (int r = 0; r < mapSize.x; r++) {
      ArrayList<TileProperties> tmpColumn = new ArrayList<>();
      for (int c = 0; c < mapSize.y; c++) {
        tmpColumn.add(new TileProperties(tileLayer.getCell(r, c).getTile().getProperties()));
      }
      tmpTileProperties.add(Collections.unmodifiableList(tmpColumn));
    }
    tileProperties = Collections.unmodifiableList(tmpTileProperties);
  }

  /**
   * Check if a tile in the map can be passed through. Tiles in the TMX map need a "passable"
   * property or it will throw a  ClassCastException
   *
   * @param point   the 2D index of the tile of interest
   * @param context additional context to decide which tiles are passable
   * @return whether the tile can be passed through
   */
  public boolean isTilePassable(TilePoint point, Object context) {
    if (point.x < 0 || point.x >= mapSize.x || point.y < 0
        || point.y >= mapSize.y) {
      return false;
    }
    if (context instanceof List<?>) {
      @SuppressWarnings("unchecked") List<TilePoint> blockedTiles = (List<TilePoint>) context;
      return listGet2d(tileProperties, point.x, point.y).passable && !blockedTiles.contains(point);
    } else if (context instanceof Map<?, ?>) {
      @SuppressWarnings("unchecked") Map<TilePoint, FieldedUnit> blockedTiles = (Map<TilePoint, FieldedUnit>) context;
      return listGet2d(tileProperties, point.x, point.y).passable && !blockedTiles
          .containsKey(point);
    }
    throw new ClassCastException("Bad type for context");
  }

  /**
   * Get the shortest path between two locations on the map avoiding blocked tiles.
   *
   * @param startPos Starting tile index
   * @param goalPos  Ending tile index
   * @return The adjacent tiles to move through to go from start to goal
   */
  public List<TilePoint> getShortestPath(TilePoint startPos, TilePoint goalPos,
      Object context) {
    BattleTileNode start = new BattleTileNode(startPos);
    BattleTileNode goal = new BattleTileNode(goalPos);
    tileNodeGoal = goalPos;
    tileNodeContext = context;
    ArrayList<PathSearchNode> path = PathSearch.runSearchByGoal(start, goal);
    ArrayList<TilePoint> points = new ArrayList<>();
    if (path != null) {
      for (PathSearchNode node : path) {
        points.add(((BattleTileNode) node).pos);
      }
    }
    return points;
  }

  /**
   * Get all points that are <= distanceLimit from startPos
   *
   * @param startPos      point to search from
   * @param distanceLimit distance to search
   * @param context       context to determine if tiles can be passed through
   * @return set of points that are <= distanceLimit
   */
  public List<TilePoint> getPointsWithinRange(TilePoint startPos, int distanceLimit,
      Object context) {
    BattleTileNode start = new BattleTileNode(startPos);
    tileNodeGoal = null;
    tileNodeContext = context;
    AStarSearchResult result = PathSearch.runSearchByDistance(start, distanceLimit);
    ArrayList<TilePoint> points = new ArrayList<>();
    for (PathSearchNode node : result.valueMap.keySet()) {
      points.add(((BattleTileNode) node).pos);
    }
    return points;
  }

  static class TileProperties {

    final static String passableKey = "passable";

    final boolean passable;

    TileProperties(MapProperties mapProperties) {
      this.passable = mapProperties.get(passableKey, false, Boolean.class);
    }
  }

  /**
   * class to wrap 2D game map tiles to search for shortest movement paths
   */
  class BattleTileNode implements PathSearchNode {

    final TilePoint pos;

    BattleTileNode(TilePoint pos) {
      this.pos = pos;
    }

    @Override
    public int heuristics() {
      if (tileNodeGoal == null) {
        return 0;
      }
      return Math.abs(tileNodeGoal.x - pos.x) + Math.abs(tileNodeGoal.y - pos.y);
    }

    @Override
    public int edgeWeight(PathSearchNode neighbor) {
      return 1;
    }

    @Override
    public List<PathSearchNode> getNeighbors() {
      ArrayList<PathSearchNode> tmp = new ArrayList<>();
      if (pos.x < mapSize.x - 1) {
        TilePoint neighborPos = pos.add(1, 0);
        if (isTilePassable(neighborPos, tileNodeContext)) {
          tmp.add(new BattleTileNode(neighborPos));
        }
      }
      if (pos.x > 0) {
        TilePoint neighborPos = pos.sub(1, 0);
        if (isTilePassable(neighborPos, tileNodeContext)) {
          tmp.add(new BattleTileNode(neighborPos));
        }
      }
      if (pos.y < mapSize.y - 1) {
        TilePoint neighborPos = pos.add(0, 1);
        if (isTilePassable(neighborPos, tileNodeContext)) {
          tmp.add(new BattleTileNode(neighborPos));
        }
      }
      if (pos.y > 0) {
        TilePoint neighborPos = pos.sub(0, 1);
        if (isTilePassable(neighborPos, tileNodeContext)) {
          tmp.add(new BattleTileNode(neighborPos));
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
