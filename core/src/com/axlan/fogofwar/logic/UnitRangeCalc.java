package com.axlan.fogofwar.logic;

import com.axlan.fogofwar.models.FieldedUnit;
import com.axlan.gdxtactics.TilePoint;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.axlan.gdxtactics.RangeCalc.getTilesInRange;

public class UnitRangeCalc {

  public static Set<TilePoint> getVisibleTiles(TilePoint loc, FieldedUnit unit) {
    return getTilesInRange(loc, unit.getStats().visionRange);
  }

  public static Set<TilePoint> getVisibleTiles(Map<TilePoint, FieldedUnit> units) {
    HashSet<TilePoint> points = new HashSet<>();
    for (TilePoint unitPoint : units.keySet()) {
      points.addAll(getVisibleTiles(unitPoint, units.get(unitPoint)));
    }
    return points;
  }

  public static Set<TilePoint> getAttackTiles(TilePoint loc, FieldedUnit unit) {
    return getTilesInRange(loc, unit.getStats().minAttackRange - 1, unit.getStats().maxAttackRange);
  }

  public static Set<TilePoint> getAttackTiles(List<TilePoint> movePoints, FieldedUnit unit) {
    HashSet<TilePoint> points = new HashSet<>();
    for (TilePoint unitPoint : movePoints) {
      points.addAll(getAttackTiles(unitPoint, unit));
    }
    points.removeAll(movePoints);
    return points;
  }

}
