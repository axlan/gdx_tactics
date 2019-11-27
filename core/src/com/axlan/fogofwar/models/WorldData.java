package com.axlan.fogofwar.models;

import java.util.List;

public class WorldData {
  public final String mapName;
  public final List<CityData> cities;

  public WorldData(String mapName, List<CityData> cities) {
    this.mapName = mapName;
    this.cities = cities;
  }

  public WorldData(WorldData other) {
    this.mapName = other.mapName;
    this.cities = other.cities;
  }

  public static class CityData {
    public final String name;
    public final int maxFriendlyTroops;
    public final int maxEnemyTroops;
    public int stationedFriendlyTroops;
    public int stationedEnemyTroops;

    public CityData(String name, int stationedFriendlyTroops, int maxFriendlyTroops, int stationedEnemyTroops, int maxEnemyTroops) {
      this.name = name;
      this.stationedFriendlyTroops = stationedFriendlyTroops;
      this.maxFriendlyTroops = maxFriendlyTroops;
      this.stationedEnemyTroops = stationedEnemyTroops;
      this.maxEnemyTroops = maxEnemyTroops;
    }

    public CityData(CityData other) {
      this.name = other.name;
      this.stationedFriendlyTroops = other.stationedFriendlyTroops;
      this.maxFriendlyTroops = other.maxFriendlyTroops;
      this.stationedEnemyTroops = other.stationedEnemyTroops;
      this.maxEnemyTroops = other.maxEnemyTroops;
    }

  }
}
