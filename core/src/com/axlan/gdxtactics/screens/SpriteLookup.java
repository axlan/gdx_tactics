package com.axlan.gdxtactics.screens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

public class SpriteLookup {

  static final HashMap<String, TextureAtlas> atlases = new HashMap<>();

  static void checkSheet(String sprite) {
    if (!atlases.containsKey(sprite)) {
      atlases.put(sprite, new TextureAtlas("images/units/tank.atlas"));
    }
  }

  static Array<AtlasRegion> getRegions(String sprite, Poses pose) {
    SpriteLookup.checkSheet(sprite);
    String name = String.format("%s_%s", sprite, pose.toString().toLowerCase());
    return atlases.get(sprite).findRegions(name);
  }

  static AnimatedSprite<AtlasRegion> getAnimation(String sprite, Poses pose, float frameDuration) {
    Array<AtlasRegion> regions = getRegions(sprite, pose);
    return new AnimatedSprite<>(frameDuration, regions);
  }

  static TextureRegionDrawable getTextureRegionDrawable(String sprite, Poses pose) {
    Array<AtlasRegion> regions = getRegions(sprite, pose);
    return new TextureRegionDrawable(regions.first());
  }

  enum Poses {
    IDLE,
    LEFT,
    UP,
    DOWN
  }

}
