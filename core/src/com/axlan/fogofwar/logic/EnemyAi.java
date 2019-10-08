package com.axlan.fogofwar.logic;

import com.axlan.fogofwar.models.FieldedUnit;
import com.axlan.fogofwar.models.FieldedUnit.State;
import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.LevelData.UnitBehavior;
import com.axlan.fogofwar.screens.BattleView;
import com.axlan.gdxtactics.JsonLoader;
import com.axlan.gdxtactics.TilePoint;
import java.util.HashMap;

public class EnemyAi {

  private final LevelData levelData;
  private final BattleView battleView;

  public EnemyAi(LevelData levelData, BattleView battleView) {
    this.levelData = levelData;
    this.battleView = battleView;
  }

  public EnemyAction getNextAction() {
    final UnitBehavior behavior = levelData.enemyBehavior;
    final HashMap<TilePoint, FieldedUnit> enemyUnits = battleView.getEnemyUnits();
    final HashMap<TilePoint, FieldedUnit> playerUnits = battleView.getPlayerUnits();

    switch (behavior.behaviorType) {
      case MOVE:
        MoveArgs args = JsonLoader.loadFromJsonString(behavior.args, MoveArgs.class);
        for (TilePoint enemyPos : enemyUnits.keySet()) {
          FieldedUnit enemyUnit = enemyUnits.get(enemyPos);
          if (enemyUnit.state != State.IDLE) {
            continue;
          }
          HashMap<TilePoint, FieldedUnit> blockedTiles = new HashMap<>(playerUnits);
//          do{
//            battleView.getShortestPath(enemyPos, args.target, playerUnits);
//          } while();

        }

        break;
      case DEFEND:
      case ATTACK:
        throw new RuntimeException("Not yet implemented");
    }
    return null;
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

    public TilePoint start;
    public TilePoint end;
  }
}
