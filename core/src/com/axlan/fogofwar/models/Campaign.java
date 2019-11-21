package com.axlan.fogofwar.models;

public class Campaign {
  public final WorldMap worldMap;

  public Campaign(WorldMap worldMap) {
    this.worldMap = worldMap;
  }

  public static class WorldMap {
    public final String mapName;

    public WorldMap(String mapName) {
      this.mapName = mapName;
    }
  }
}
