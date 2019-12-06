package com.axlan.fogofwar.logic;

import com.axlan.fogofwar.models.FieldedUnit;
import com.axlan.gdxtactics.TilePoint;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VisionCalc {
  public static Set<TilePoint> getVisibleTiles(TilePoint loc, FieldedUnit unit) {
    HashSet<TilePoint> points = new HashSet<>();
    points.add(loc);
    HashSet<TilePoint> pointsNext = new HashSet<>(points);
    for (int i = 0; i < unit.getStats().visionRange; i++) {
      for (TilePoint point : points) {
        pointsNext.add(point.add(1, 0));
        pointsNext.add(point.add(-1, 0));
        pointsNext.add(point.add(0, 1));
        pointsNext.add(point.add(0, -1));
      }
      points.addAll(pointsNext);
    }
    return points;
  }

  public static Set<TilePoint> getVisibleTiles(Map<TilePoint, FieldedUnit> units) {
    HashSet<TilePoint> points = new HashSet<>();
    for (TilePoint unitPoint : units.keySet()) {
      points.addAll(getVisibleTiles(unitPoint, units.get(unitPoint)));
    }
    return points;
  }

}
