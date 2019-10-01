package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.models.TilePoint;
import com.axlan.gdxtactics.screens.SpriteLookup.Poses;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.List;

/**
 * Class for drawing a path on top of a 2D TileMap
 */
public class PathVisualizer {

  private final TilePoint tileSize;
  private final int lineWith;
  private float elapsedTime;
  private float tileDuration;
  private float frameDuration;
  private String spriteName;
  private List<TilePoint> animationPath;

  /**
   * @param tileSize The height and width of tiles on the map in pixels
   */
  public PathVisualizer(TilePoint tileSize) {
    this.tileSize = tileSize;
    lineWith = tileSize.x / 3;
  }

  /**
   * Convert a tile index to a pixel location of the center of the tile
   * @param point the 2D index of a tile on the map
   * @return The pixel location of the middle of the tile
   */
  private TilePoint tileToPixelCenter(TilePoint point) {
    return point.add(tileSize.divBy(2));
  }

  /**
   * Initialize the class to move an {@link AnimatedSprite} along path on subsequent calls to
   * {@link PathVisualizer#drawAnimatedSpritePath(float, SpriteBatch)}
   *
   * @param spriteName String identifier of animated sprite to draw along path
   * @param points List of tile indexes that make up the path to draw the sprite along. Each point
   *               must be adjacent to the last.
   * @param tileDuration How many seconds should it take for the sprite to cross each tile along the
   *                     path.
   * @param frameDuration How many seconds should be between each frame of the
   *                      {@link AnimatedSprite}
   */
  public void startAnimation(String spriteName, List<TilePoint> points, float tileDuration,
      float frameDuration) {
    if (points.size() < 2) {
      return;
    }
    TilePoint lastPoint = null;
    for (TilePoint point : points) {
      assert lastPoint == null || (point.absDiff(lastPoint) == 1);
      lastPoint = point;
    }
    elapsedTime = 0;
    this.spriteName = spriteName;
    this.animationPath = points;
    this.tileDuration = tileDuration;
    this.frameDuration = frameDuration;
  }

  /**
   * Draw the current frame of the {@link AnimatedSprite} along the path last initialized by
   * {@link PathVisualizer#startAnimation(String, List, float, float)}
   *
   * @param delta seconds since last draw update
   * @param batch SpriteBatch to draw onto
   * @return true if the animation has completed, false if it's ongoing
   */
  public boolean drawAnimatedSpritePath(float delta, SpriteBatch batch) {
    if (this.spriteName == null) {
      return true;
    }
    elapsedTime += delta;
    float tileProgress = this.elapsedTime / this.tileDuration;
    int curTileIdx = (int) tileProgress;
    float curTileProgress = tileProgress - curTileIdx;
    if (curTileIdx >= animationPath.size() - 1) {
      this.spriteName = null;
      return true;
    }
    TilePoint curPoint = this.animationPath.get(curTileIdx);
    TilePoint nextPoint = this.animationPath.get(curTileIdx + 1);
    TilePoint movement = nextPoint.sub(curPoint);
    TilePoint drawLocation = curPoint.mult(tileSize).add(
        movement.mult(tileSize).mult(curTileProgress));
    AnimatedSprite<AtlasRegion> sprite;
    if (movement.x < 0) {
      sprite = SpriteLookup.getAnimation(this.spriteName, Poses.LEFT, this.frameDuration, true);
    } else if (movement.x > 0) {
      sprite = SpriteLookup.getAnimation(this.spriteName, Poses.LEFT, this.frameDuration, true);
      sprite.flip(true, false);
    } else if (movement.y < 0) {
      sprite = SpriteLookup.getAnimation(this.spriteName, Poses.DOWN, this.frameDuration, true);
    } else {
      sprite = SpriteLookup.getAnimation(this.spriteName, Poses.UP, this.frameDuration, true);
    }
    sprite.setPosition(drawLocation.x, drawLocation.y);
    sprite.draw(batch, elapsedTime);

    return false;
  }

  /**
   * Draw an arrow along a path.
   *
   * @param shapeRenderer shapeRenderer to draw arrow onto. ShapeRenderer is expected to already
   *                      have called begin, and to be set with the desired color.
   * @param points List of tile indexes that make up the path to draw the sprite along. Each point *
   *     must be adjacent to the last.
   */
  public void drawArrow(ShapeRenderer shapeRenderer, List<TilePoint> points) {
    if (points.size() < 2) {
      return;
    }
    TilePoint lastPoint = tileToPixelCenter(points.get(0).mult(tileSize));
    boolean goingX = false;
    for (TilePoint nextPoint : points.subList(1, points.size())) {
      nextPoint = tileToPixelCenter(nextPoint.mult(tileSize));
      Boolean nextGoingX = lastPoint.x != nextPoint.x;
      if (nextGoingX != goingX) {
        shapeRenderer.circle(lastPoint.x, lastPoint.y, ((float) lineWith) / 2);
      }
      shapeRenderer.rectLine(
          lastPoint.x, lastPoint.y, nextPoint.x, nextPoint.y, lineWith);
      goingX = nextGoingX;
      lastPoint = nextPoint;
    }
    TilePoint halfTile = tileSize.divBy(2);
    if (goingX) {
      int sign = (points.get(points.size() - 1).x - points.get(points.size() - 2).x < 0) ? -1 : 1;
      shapeRenderer.triangle(lastPoint.x + sign * lineWith, lastPoint.y, lastPoint.x,
          lastPoint.y + halfTile.y, lastPoint.x, lastPoint.y - halfTile.y);
    } else {
      int sign = (points.get(points.size() - 1).y - points.get(points.size() - 2).y < 0) ? -1 : 1;
      shapeRenderer
          .triangle(lastPoint.x, lastPoint.y + sign * lineWith, lastPoint.x + halfTile.x,
              lastPoint.y, lastPoint.x - halfTile.x, lastPoint.y);
    }
  }
}
