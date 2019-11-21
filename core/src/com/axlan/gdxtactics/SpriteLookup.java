package com.axlan.gdxtactics;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Class for loading from TextureAtlas based on a sprites name and pose
 */
@SuppressWarnings("WeakerAccess")
public class SpriteLookup {

  private final TextureAtlas textureAtlas;

  /**
   * @param textureAtlas texture atlas with entries labeled as <type>_<pose>. The indexes of each
   *     entry are the frames of the animation.
   */
  public SpriteLookup(TextureAtlas textureAtlas) {
    this.textureAtlas = textureAtlas;
  }

  /**
   * @param sprite name of sprite
   * @param pose pose of sprite
   * @return Corresponding AtlasRegion
   */
  public Array<AtlasRegion> getRegions(String sprite, Poses pose) {
    String name = String.format("%s_%s", sprite, pose.toString().toLowerCase());
    return textureAtlas.findRegions(name);
  }

  /**
   * @param sprite name of sprite
   * @param pose pose of sprite
   * @param frameDuration seconds to show each frame of the animation
   * @param reverse should the frames repeat in reverse {1, 2, 3} -> {1, 2, 3, 2, 1}
   * @return Corresponding AnimatedSprite
   */
  @SuppressWarnings("SameParameterValue")
  public AnimatedSprite<AtlasRegion> getAnimation(
      String sprite, Poses pose, float frameDuration, boolean reverse) {
    Array<AtlasRegion> regions = getRegions(sprite, pose);
    if (reverse && regions.size > 1) {
      for (int i = regions.size - 2; i >= 0; i--) {
        regions.add(regions.get(i));
      }
    }
    return new AnimatedSprite<>(frameDuration, regions);
  }

  /**
   * @param sprite name of sprite
   * @param pose pose of sprite
   * @return Corresponding TextureRegionDrawable
   */
  @SuppressWarnings("SameParameterValue")
  public TextureRegionDrawable getTextureRegionDrawable(String sprite, Poses pose) {
    Array<AtlasRegion> regions = getRegions(sprite, pose);
    return new TextureRegionDrawable(regions.first());
  }

  /** Poses in sprite set */
  public enum Poses {
    IDLE,
    LEFT,
    UP,
    DOWN
  }
}
