package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.screens.SpriteLookup.Poses;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;

public class PathVisualizer {

  private TilePoint tileSize;
  private int lineWith;
  private float elapsedTime;
  private int curTileCount;
  private int curTileIdx;
  private int stepsPerTile;
  private float frameDuration;
  private String spriteName;
  private ArrayList<TilePoint> animationPath;

  public PathVisualizer(TilePoint tileSize) {
    this.tileSize = tileSize;
    lineWith = tileSize.x / 3;
  }

  private TilePoint center(TilePoint point) {
    return point.add(tileSize.divBy(2));
  }

  public void startAnimation(String spriteName, ArrayList<TilePoint> points, int stepsPerTile,
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

    TilePoint curPoint = this.animationPath.get(curTileIdx);
    TilePoint nextPoint = this.animationPath.get(curTileIdx + 1);
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


  public void drawArrow(ShapeRenderer shapeRenderer, ArrayList<TilePoint> points) {
    if (points.size() < 2) {
      return;
    }
    shapeRenderer.setColor(Color.BLUE);
    TilePoint lastPoint = center(points.get(0).mult(tileSize));
    Boolean goingX = null;
    for (TilePoint nextPoint : points.subList(1, points.size())) {
      nextPoint = center(nextPoint.mult(tileSize));
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
