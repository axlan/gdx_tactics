package com.axlan.gdxtactics.models;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;

@SuppressWarnings({"WeakerAccess", "unused"})
public class TilePoint {

  public final int x;
  public final int y;

  public TilePoint(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public TilePoint(TilePoint point) {
    this(point.x, point.y);
  }

  public TilePoint(GridPoint2 point) {
    this(point.x, point.y);
  }

  public TilePoint(Vector2 point) {
    this((int) point.x, (int) point.y);
  }

  public TilePoint add(int x, int y) {
    return new TilePoint(this.x + x, this.y + y);
  }

  public TilePoint add(TilePoint other) {
    return this.add(other.x, other.y);
  }

  public TilePoint sub(int x, int y) {
    return new TilePoint(this.x - x, this.y - y);
  }

  public TilePoint sub(TilePoint other) {
    return this.add(other.x, other.y);
  }

  public TilePoint mult(int x, int y) {
    return new TilePoint(this.x * x, this.y * y);
  }

  public TilePoint mult(int val) {
    return this.mult(val, val);
  }

  public TilePoint mult(TilePoint other) {
    return this.add(other.x, other.y);
  }

  public TilePoint divBy(int x, int y) {
    return new TilePoint(this.x / x, this.y / y);
  }

  public TilePoint divBy(int val) {
    return this.mult(val, val);
  }

  public TilePoint divBy(TilePoint other) {
    return this.add(other.x, other.y);
  }

  public int absDiff(int x, int y) {
    return Math.abs(this.x - x) + Math.abs(this.y - y);
  }

  public Vector2 toVector2() {
    return new Vector2(x, y);
  }

  public GridPoint2 toGridPoint2() {
    return new GridPoint2(x, y);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || o.getClass() != this.getClass()) {
      return false;
    }
    TilePoint g = (TilePoint) o;
    return this.x == g.x && this.y == g.y;
  }

  @Override
  public int hashCode() {
    final int prime = 53;
    int result = 1;
    result = prime * result + this.x;
    result = prime * result + this.y;
    return result;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }

}
