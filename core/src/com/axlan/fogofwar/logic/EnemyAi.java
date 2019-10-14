package com.axlan.fogofwar.logic;

import com.axlan.fogofwar.models.FieldedUnit;
import com.axlan.fogofwar.models.FieldedUnit.State;
import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.LevelData.UnitBehavior;
import com.axlan.fogofwar.screens.BattleView;
import com.axlan.gdxtactics.JsonLoader;
import com.axlan.gdxtactics.TilePoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//TODO-P2 Add comments
//TODO-P2 Add additional AI types
//TODO-P3 Work on improving AIs
//TODO-P3 improve system for allowing multiple AIs for development
//TODO-P3 make enemy actions respect fog of war
//TODO-P3 make MOVE understand how to improve state even if blocked off
public class EnemyAi {

  private final LevelData levelData;
  private final BattleView battleView;

  public EnemyAi(LevelData levelData, BattleView battleView) {
    this.levelData = levelData;
    this.battleView = battleView;
  }

  private EnemyMoveAction getNextActionMoveAI1(MoveArgs args) {
    EnemyMoveAction nextAction = null;

    final HashMap<TilePoint, FieldedUnit> enemyUnits = battleView.getEnemyUnits();
    final HashMap<TilePoint, FieldedUnit> playerUnits = battleView.getPlayerUnits();

    for (TilePoint enemyPos : enemyUnits.keySet()) {
      FieldedUnit enemyUnit = enemyUnits.get(enemyPos);
      if (enemyUnit.state != State.IDLE) {
        continue;
      }
      HashMap<TilePoint, FieldedUnit> blockedTiles = new HashMap<>(playerUnits);
      List<TilePoint> path = null;
      while (path == null) {
        path = battleView.getShortestPath(enemyPos, args.target, blockedTiles);
        if (path == null) {
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
        int moveIdx = Math.min(enemyUnit.stats.movement, path.size() - 1);
        path = path.subList(0, moveIdx + 1);
      }
      nextAction = new EnemyMoveAction(path);
      break;
    }
    return nextAction;
  }

  public EnemyAction getNextAction() {
    final UnitBehavior behavior = levelData.enemyBehavior;
    EnemyAction nextAction = null;

    switch (behavior.behaviorType) {
      case MOVE:
        MoveArgs args = JsonLoader.loadFromJsonString(behavior.args, MoveArgs.class);
        nextAction = getNextActionMoveAI1(args);
        break;
      case DEFEND:
      case ATTACK:
        throw new RuntimeException("Not yet implemented");
    }
    return nextAction;
  }

  private class MoveArgs {

    final TilePoint target;

    private MoveArgs(TilePoint target) {
      this.target = target;
    }
  }

  public abstract class EnemyAction {

  }

  public class EnemyMoveAction extends EnemyAction {

    public final List<TilePoint> path;

    public EnemyMoveAction(List<TilePoint> path) {
      this.path = Collections.unmodifiableList(path);
    }
  }
}
