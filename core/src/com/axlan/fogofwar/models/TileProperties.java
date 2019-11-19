package com.axlan.fogofwar.models;

import com.badlogic.gdx.maps.MapProperties;

public class TileProperties {

    private static final String passableKey = "passable";

  public final boolean passable;

  TileProperties(MapProperties mapProperties) {
    this.passable = mapProperties.get(passableKey, false, Boolean.class);
  }
}
