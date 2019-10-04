package com.axlan.gdxtactics;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Extension to Sprite that shows an animated sprite
 *
 * <p> Instances can be created by {@link SpriteLookup}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AnimatedSprite<T> extends Sprite {

  private final Animation<T> animation;
  private float elapsedTime = 0;

  /**
   * @param frameDuration Seconds to show each frame of animation
   * @param frames Frames that make up animation
   */
  public AnimatedSprite(float frameDuration, Array<T> frames) {
    super((TextureRegion) frames.get(0));
    animation = new Animation<>(frameDuration, frames);
  }

  /** Update the elapsed time that controls which frame of the animation to show
   *
   * @param delta amount of time to advance by in seconds
   */
  public void update(float delta) {
    elapsedTime += delta;
  }

  /**
   * Draw the current frame of the animation on the Batch. Apply the sprites transformations.
   */
  @Override
  public void draw(Batch batch) {
    T currentFrame = animation.getKeyFrame(elapsedTime, true);
    /* Loading a new region clears the flip attributes. Cache them and reapply after load */
    boolean flipX = isFlipX();
    boolean flipY = isFlipY();
    this.setRegion((TextureRegion) currentFrame);
    this.flip(flipX, flipY);
    super.draw(batch);
  }

  /** Update the elapsed time and draw the frame of the animation on the Batch.
   * Apply the sprites transformations.
   *
   * @param batch Batch to draw on
   * @param elapsedTime the total elapsed time used to determine the current frame
   */
  public void draw(Batch batch, float elapsedTime) {
    this.elapsedTime = elapsedTime;
    draw(batch);
  }

  /** Set the position by transforming a tile position into pixels using the sprites size */
  public void setTilePosition(TilePoint tilePoint) {
    this.setPosition(tilePoint.x * this.getWidth(), tilePoint.y * this.getHeight());
  }

}
