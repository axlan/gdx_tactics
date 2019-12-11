package com.axlan.gdxtactics;

import java.util.HashSet;
import java.util.Set;

public class RangeCalc {

  public static Set<TilePoint> getTilesInRange(TilePoint loc, int max) {
    HashSet<TilePoint> points = new HashSet<>();
    points.add(loc);
    HashSet<TilePoint> pointsNext = new HashSet<>(points);
    for (int i = 0; i < max; i++) {
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

  public static Set<TilePoint> getTilesInRange(TilePoint loc, int min, int max) {
    Set<TilePoint> minRange = getTilesInRange(loc, min);
    Set<TilePoint> maxRange = getTilesInRange(loc, max);
    maxRange.removeAll(minRange);
    return maxRange;
  }
}
