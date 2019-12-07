package com.axlan.fogofwar.logic;

import com.axlan.fogofwar.models.BattleMap;
import com.axlan.fogofwar.models.BattleState;
import com.axlan.fogofwar.models.FieldedUnit;
import com.axlan.fogofwar.models.FieldedUnit.State;
import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.LevelData.UnitBehavior;
import com.axlan.gdxtactics.JsonLoader;
import com.axlan.gdxtactics.PathSearch;
import com.axlan.gdxtactics.TilePoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.axlan.gdxtactics.Utilities.listGetTail;

// TODO-P2 Add comments
// TODO-P2 Add additional AI types
// TODO-P3 Work on improving AIs
// TODO-P3 improve system for allowing multiple AIs for development
// TODO-P3 make enemy actions respect fog of war
// TODO-P3 make MOVE understand how to improve state even if blocked off

/**
 * Class for controlling AI units
 */
public class EnemyAi {

  /**
   * Level data that specifies AI behavior
   */
  private final LevelData levelData;
  /**
   * BattleView that keeps track of unit states and map information
   */
  private final BattleState battleState;
  /**
   * Class for accessing map data for pathing
   */
  private final BattleMap battleMap;

  public EnemyAi(LevelData levelData, BattleState battleState, BattleMap battleMap) {
    this.levelData = levelData;
    this.battleState = battleState;
    this.battleMap = battleMap;
  }

  /**
   * Basic implementation of AI for units that try to attack closest unit
   * Note: Assumes attack range of 1
   *
   * @param args parameters for specifying attack
   * @return The next unit movement, or null if no units left to move
   */
  private EnemyMoveAction getNextActionAttackAI1(AttackArgs args) {
    EnemyMoveAction nextAction = null;

    final HashMap<TilePoint, FieldedUnit> enemyUnits = battleState.enemyUnits;
    final HashMap<TilePoint, FieldedUnit> playerUnits = battleState.playerUnits;

    for (TilePoint enemyPos : enemyUnits.keySet()) {
      FieldedUnit enemyUnit = enemyUnits.get(enemyPos);
      if (enemyUnit.state != State.IDLE) {
        continue;
      }
      HashMap<TilePoint, FieldedUnit> blockedTiles = new HashMap<>(playerUnits);
      Integer distanceLimit = (args.onlyInUnitSight) ? enemyUnit.getStats().visionRange - 1 : null;
      PathSearch.AStarSearchResult distances = battleMap.getDistancesFromPoint(enemyPos, distanceLimit, blockedTiles);

      int bestDistance = Integer.MAX_VALUE;
      TilePoint bestTarget = null;
      TilePoint bestMove = null;
      TilePoint[] adjacentPoints = new TilePoint[]{
          new TilePoint(1, 0),
          new TilePoint(0, 1),
          new TilePoint(-1, 0),
          new TilePoint(0, -1)
      };

      boolean noMove = false;
      for (TilePoint playerUnitLoc : playerUnits.keySet()) {
        if (noMove) {
          break;
        }
        for (TilePoint mod : adjacentPoints) {
          TilePoint movePoint = playerUnitLoc.add(mod);
          if (enemyPos.equals(movePoint)) {
            bestTarget = playerUnitLoc;
            bestMove = movePoint;
            noMove = true;
            break;
          }
          if (battleState.enemyUnits.containsKey(movePoint)) {
            continue;
          }
          int distance = battleMap.getDistance(distances, movePoint);
          if (distance < bestDistance) {
            bestDistance = distance;
            bestTarget = playerUnitLoc;
            bestMove = movePoint;
          }
        }
      }
      if (bestMove == null) {
        continue;
      }
      if (bestMove.absDiff(enemyPos) > enemyUnit.getStats().movement) {
        bestTarget = null;
      }

      List<TilePoint> path;
      if (noMove) {
        path = Collections.singletonList(enemyPos);
      } else {
        path = battleMap.getShortestPath(enemyPos, bestMove, blockedTiles);
      }
      List<TilePoint> attacks = (bestTarget != null) ? Collections.singletonList(bestTarget) : Collections.emptyList();
      if (path.size() > enemyUnit.getStats().movement) {
        //TODO-P3 allow for more complex path length calculation
        path = path.subList(0, enemyUnit.getStats().movement);
        while (path.size() > 1 && enemyUnits.containsKey(listGetTail(path))) {
          path = path.subList(0, path.size() - 1);
        }
      }
      nextAction = new EnemyMoveAction(path, attacks);
      break;
    }
    return nextAction;
  }

  /**
   * Basic implementation of AI for units that only move
   *
   * @param args parameters for specifying movement
   * @return The next unit movement, or null if no units left to move
   */
  private EnemyMoveAction getNextActionMoveAI1(MoveArgs args) {
    EnemyMoveAction nextAction = null;

    final HashMap<TilePoint, FieldedUnit> enemyUnits = battleState.enemyUnits;
    final HashMap<TilePoint, FieldedUnit> playerUnits = battleState.playerUnits;

    for (TilePoint enemyPos : enemyUnits.keySet()) {
      FieldedUnit enemyUnit = enemyUnits.get(enemyPos);
      if (enemyUnit.state != State.IDLE) {
        continue;
      }
      HashMap<TilePoint, FieldedUnit> blockedTiles = new HashMap<>(playerUnits);
      List<TilePoint> path = null;
      while (path == null) {
        path = battleMap.getShortestPath(enemyPos, args.target, blockedTiles);
        if (path == null || path.size() < 2) {
          path = null;
          break;
        }
        for (TilePoint point : path.subList(1, path.size() - 1)) {
          if (enemyUnits.containsKey(point)) {
            path = null;
            blockedTiles.put(point, enemyUnits.get(point));
          }
        }
      }
      if (path == null || path.size() <= 1) {
        path = new ArrayList<>();
        path.add(enemyPos);
      } else {
        int moveIdx = Math.min(enemyUnit.getStats().movement, path.size() - 1);
        path = path.subList(0, moveIdx + 1);
      }
      nextAction = new EnemyMoveAction(path);
      break;
    }
    return nextAction;
  }

  /**
   * Based on the current board state, and the AI selection for the level, return the next AI move
   *
   * @return the next move, or null if no more moves to make
   */
  public EnemyAction getNextAction() {
    final UnitBehavior behavior = levelData.enemyBehavior;
    EnemyAction nextAction = null;

    switch (behavior.behaviorType) {
      case MOVE:
        MoveArgs moveArgs = JsonLoader.loadFromJsonString(behavior.args, MoveArgs.class);
        nextAction = getNextActionMoveAI1(moveArgs);
        break;
      case DEFEND:
        throw new RuntimeException("Not yet implemented");
      case ATTACK:
        AttackArgs attackArgs = JsonLoader.loadFromJsonString(behavior.args, AttackArgs.class);
        nextAction = getNextActionAttackAI1(attackArgs);
        break;
    }
    return nextAction;
  }

  /**
   * Class to describes an AI that focuses on moving to a certain point.
   */
  private static class MoveArgs {

    /**
     * Target point to move to
     */
    final TilePoint target;

    private MoveArgs(TilePoint target) {
      this.target = target;
    }
  }

  /**
   * Class to describes an AI that focuses on attacking.
   */
  private static class AttackArgs {

    /**
     * Only attack units in vision range of a given unit
     */
    final boolean onlyInUnitSight;

    private AttackArgs(boolean onlyInUnitSight) {
      this.onlyInUnitSight = onlyInUnitSight;
    }
  }

  /**
   * Base class for describing enemy unit actions
   */
  public abstract static class EnemyAction {
  }

  /**
   * Class that describes an enemy unit movement
   */
  public static class EnemyMoveAction extends EnemyAction {

    public final List<TilePoint> path;

    public final List<TilePoint> attacks;

    EnemyMoveAction(List<TilePoint> path) {
      this.path = Collections.unmodifiableList(path);
      this.attacks = Collections.unmodifiableList(Collections.emptyList());
    }

    EnemyMoveAction(List<TilePoint> path, List<TilePoint> attacks) {
      this.path = Collections.unmodifiableList(path);
      this.attacks = attacks;
    }
  }
}
