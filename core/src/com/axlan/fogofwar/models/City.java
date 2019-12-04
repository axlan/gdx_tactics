package com.axlan.fogofwar.models;

import com.axlan.gdxtactics.TilePoint;

/**
 * Data describing a city on the worldmap
 */
public class City {
  /**
   * city identifier
   */
  public final String name;
  /**
   * Tile index of city on map
   */
  public final TilePoint location;
  /**
   * Player that controls the city
   */
  public final Controller controller;

  public City(String name, TilePoint location, Controller controller) {
    this.name = name;
    this.location = location;
    this.controller = controller;
  }

  public enum Controller {
    PLAYER,
    ENEMY,
    NONE
  }
}

