package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.screens.SpriteLookup.Poses;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import java.util.ArrayList;

public class PathVisualizer {

  GridPoint2 tileSize;
  int lineWith;
  float elapsedTime;
  int curTileCount;
  int curTileIdx;
  int stepsPerTile;
  float frameDuration;
  String spriteName;
  ArrayList<GridPoint2> animationPath;

  public PathVisualizer(GridPoint2 tileSize) {
    this.tileSize = tileSize;
    lineWith = tileSize.x / 3;
  }

  GridPoint2 scl(GridPoint2 point, int val) {
    return new GridPoint2(point.x * val, point.y * val);
  }

  GridPoint2 scl(GridPoint2 point1, GridPoint2 point2) {
    return new GridPoint2(point1.x * point2.x, point1.y * point2.y);
  }

  GridPoint2 center(GridPoint2 point) {
    return new GridPoint2(point.x + tileSize.x / 2, point.y + tileSize.y / 2);
  }

  public void startAnimation(String spriteName, ArrayList<GridPoint2> points, int stepsPerTile,
      float frameDuration) {
    if (points.size() < 2) {
      return;
    }
    elapsedTime = 0;
    curTileCount = 0;
    curTileIdx = 0;
    this.spriteName = spriteName;
    this.animationPath = points;
    this.stepsPerTile = stepsPerTile;
    this.frameDuration = frameDuration;
  }

  public boolean drawAnimatedSpritePath(float delta, SpriteBatch batch) {
    if (this.spriteName == null) {
      return true;
    }

    GridPoint2 curPoint = this.animationPath.get(curTileIdx);
    GridPoint2 nextPoint = this.animationPath.get(curTileIdx + 1);
    int x = curPoint.x * tileSize.x;
    int y = curPoint.y * tileSize.y;
    AnimatedSprite<AtlasRegion> sprite;

    if (curPoint.x > nextPoint.x) {
      sprite = SpriteLookup.getAnimation(this.spriteName, Poses.LEFT, this.frameDuration);
      sprite.setPosition(x - tileSize.x * this.curTileCount / this.stepsPerTile, y);
    } else if (curPoint.x < nextPoint.x) {
      sprite = SpriteLookup.getAnimation(this.spriteName, Poses.LEFT, this.frameDuration);
      sprite.flip(true, false);
      sprite.setPosition(x + tileSize.x * this.curTileCount / this.stepsPerTile, y);
    } else if (curPoint.y > nextPoint.y) {
      sprite = SpriteLookup.getAnimation(this.spriteName, Poses.DOWN, this.frameDuration);
      sprite.setPosition(x, y - tileSize.y * this.curTileCount / this.stepsPerTile);
    } else {
      sprite = SpriteLookup.getAnimation(this.spriteName, Poses.UP, this.frameDuration);
      sprite.setPosition(x, y + tileSize.y * this.curTileCount / this.stepsPerTile);
    }
    sprite.scale(2);
    sprite.draw(batch, elapsedTime);

    this.curTileCount++;
    if (curTileCount == stepsPerTile) {
      curTileCount = 0;
      curTileIdx++;
      if (curTileIdx == animationPath.size() - 1) {
        spriteName = null;
      }
    }

    elapsedTime += delta;
    return false;
  }


  public void drawArrow(ShapeRenderer shapeRenderer, ArrayList<GridPoint2> points) {
    if (points.size() < 2) {
      return;
    }
    shapeRenderer.setColor(Color.BLUE);
    GridPoint2 lastPoint = center(scl(points.get(0), tileSize));
    Boolean goingX = null;
    for (GridPoint2 nextPoint : points.subList(1, points.size())) {
      nextPoint = center(scl(nextPoint, tileSize));
      Boolean nextGoingX = lastPoint.x != nextPoint.x;
      if (goingX != null && nextGoingX != goingX) {
        shapeRenderer.circle(lastPoint.x, lastPoint.y, lineWith / 2);
      }
      shapeRenderer.rectLine(
          lastPoint.x, lastPoint.y, nextPoint.x, nextPoint.y, lineWith);
      goingX = nextGoingX;
      lastPoint = nextPoint;
    }
    if (goingX) {
      int sign = (points.get(points.size() - 1).x - points.get(points.size() - 2).x < 0) ? -1 : 1;
      shapeRenderer.triangle(lastPoint.x + sign * lineWith, lastPoint.y, lastPoint.x,
          lastPoint.y + tileSize.y / 2, lastPoint.x, lastPoint.y - tileSize.y / 2);
    } else {
      int sign = (points.get(points.size() - 1).y - points.get(points.size() - 2).y < 0) ? -1 : 1;
      shapeRenderer
          .triangle(lastPoint.x, lastPoint.y + sign * lineWith, lastPoint.x + tileSize.x / 2,
              lastPoint.y, lastPoint.x - tileSize.x / 2, lastPoint.y);
    }
  }
}
