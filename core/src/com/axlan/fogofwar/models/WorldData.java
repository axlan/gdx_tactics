package com.axlan.fogofwar.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data describing the world map
 */
@SuppressWarnings("WeakerAccess")
public class WorldData {
  /**
   * Identifier for map file
   */
  public final String mapName;
  /**
   * Data for cities on map
   */
  public final List<CityData> cities;

  /**
   * Get a city in the world city list by name
   *
   * @param name name of city to return
   * @return Optional CityData with matching name if found
   */
  public Optional<CityData> getCity(String name) {
    //TODO-P3 for places with this find pattern, override the equality check to use built in functions.
    return cities.stream().filter((a) -> a.name.equals(name)).findAny();
  }

  public WorldData(String mapName, List<CityData> cities) {
    this.mapName = mapName;
    this.cities = cities;
  }

  public WorldData(WorldData other) {
    this.mapName = other.mapName;
    this.cities = new ArrayList<>();
    for (CityData city : other.cities) {
      this.cities.add(new CityData(city));
    }
  }

  /**
   * Data for a city in the world map
   */
  public static class CityData {
    /** Identifier for city */
    public final String name;
    /** Number of spawn points for player */
    public final int maxFriendlyTroops;
    /** Number of spawn points for enemy */
    public final int maxEnemyTroops;
    /** Number of troops currently allocated to deploy to city */
    public int stationedFriendlyTroops;
    /** Number of enemy troops currently allocated to deploy to city */
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
