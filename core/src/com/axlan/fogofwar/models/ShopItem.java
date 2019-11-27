package com.axlan.fogofwar.models;


import java.util.Collections;
import java.util.List;

/**
 * Description of an item appearing in the shop
 */
public class ShopItem {


  /**
   * Name of item to be displayed
   */
  public final String name;
  /**
   * Cost of item in store
   */
  public final int cost;
  /**
   * Description of item to be displayed
   */
  public final String description;
  /**
   * Pieces of intel that buying the item will reveal
   */
  public final List<Intel> effects;

  public ShopItem(String name, int cost, String description, List<Intel> effects) {
    this.name = name;
    this.cost = cost;
    this.description = description;
    this.effects = Collections.unmodifiableList(effects);
  }

  /**
   * Should the units in a formation be spotted in order, or at random
   */
  public enum SpotType {
    RANDOM,
    ORDERED
  }

  /**
   * Class describing how an item will reveal information about the enemy.
   *
   * <p>This sets what the player will see during the {@link com.axlan.fogofwar.screens.DeployView}
   * scene
   */
  public static class Intel {

    // TODO-P2 Add reported and actual accuracy values, mis-identification, false positive, etc.
    // TODO-P2 Allow items to persist between levels.
    /**
     * How many units in the formation should be reveled
     */
    public final int numberOfUnits;
    /**
     * How should the reveal be ordered
     */
    public final SpotType spotType;

    public Intel(int numberOfUnits, SpotType spotType) {
      this.numberOfUnits = numberOfUnits;
      this.spotType = spotType;
    }
  }
}