package com.axlan.gdxtactics.screens;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class AnimatedSprite<T> extends Sprite {

  private final Animation<T> animation;
  private float elapsedTime = 0;

  public AnimatedSprite(float frameDuration, Array<T> frames) {
    super((TextureRegion) frames.get(0));
    animation = new Animation<>(frameDuration, frames);
  }

  public void update(float delta) {
    elapsedTime += delta;
  }

  @Override
  public void draw(Batch batch) {
    T currentFrame = animation.getKeyFrame(elapsedTime, true);
    this.setRegion((TextureRegion) currentFrame);
    super.draw(batch);
  }

  public void draw(Batch batch, float elapsedTime) {
    this.elapsedTime = elapsedTime;
    draw(batch);
  }

  public void setTilePosition(GridPoint2 tilePoint) {
    this.setPosition(tilePoint.x * this.getWidth(), tilePoint.y * this.getHeight());
  }

}
