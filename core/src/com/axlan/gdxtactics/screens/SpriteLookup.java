package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.models.LoadedResources;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Class for loading from TextureAtlas based on a sprites name and pose
 *
 * @see LoadedResources
 */
@SuppressWarnings("WeakerAccess")
public class SpriteLookup {

  /**
   * @param sprite name of sprite
   * @param pose   pose of sprite
   * @return Corresponding AtlasRegion
   */
  static Array<AtlasRegion> getRegions(String sprite, Poses pose) {
    String name = String.format("%s_%s", sprite, pose.toString().toLowerCase());
    return LoadedResources.getTextureAtlas().findRegions(name);
  }

  /**
   *
   * @param sprite name of sprite
   * @param pose pose of sprite
   * @param frameDuration seconds to show each frame of the animation
   * @param reverse should the frames repeat in reverse {1, 2, 3} -> {1, 2, 3, 2, 1}
   * @return Corresponding AnimatedSprite
   */
  @SuppressWarnings("SameParameterValue")
  static AnimatedSprite<AtlasRegion> getAnimation(String sprite, Poses pose, float frameDuration,
      boolean reverse) {
    Array<AtlasRegion> regions = getRegions(sprite, pose);
    if (reverse && regions.size > 1) {
      for (int i = regions.size - 2; i >= 0; i--) {
        regions.add(regions.get(i));
      }
    }
    return new AnimatedSprite<>(frameDuration, regions);
  }

  /** Load an AnimatedSprite by name and pose using default setting for frameDuration and reverse
   *
   * @param sprite name of sprite
   * @param pose pose of sprite
   * @return Corresponding AnimatedSprite
   */
  static AnimatedSprite<AtlasRegion> getAnimation(
      String sprite, Poses pose) {
    return SpriteLookup
        .getAnimation(sprite, pose, LoadedResources.getSettings().sprites.frameDuration, true);
  }

  /**
   * @param sprite name of sprite
   * @param pose   pose of sprite
   * @return Corresponding TextureRegionDrawable
   */
  @SuppressWarnings("SameParameterValue")
  static TextureRegionDrawable getTextureRegionDrawable(String sprite, Poses pose) {
    Array<AtlasRegion> regions = getRegions(sprite, pose);
    return new TextureRegionDrawable(regions.first());
  }

  /** Poses in sprite set */
  enum Poses {
    IDLE,
    LEFT,
    UP,
    DOWN
  }

}
