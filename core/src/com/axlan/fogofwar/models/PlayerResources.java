package com.axlan.fogofwar.models;

import com.axlan.fogofwar.models.LevelData.ShopItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class storing the state of resources available to the player.
 */
public class PlayerResources {

  private final ArrayList<ShopItem> purchases;
  private int money;

  PlayerResources() {
    money = 20;
    purchases = new ArrayList<>();
  }

  /**
   * @return Money available to the player
   */
  public int getMoney() {
    return money;
  }

  /**
   * Add money to the players available total
   *
   * @param income amount of money to add to the player
   */
  public void addMoney(int income) {
    money += income;
  }

  /**
   * @return An unmodifiable List of ShopItem available to the player
   */
  public List<ShopItem> getPurchases() {
    return Collections.unmodifiableList(purchases);
  }

  /**
   * Deduct the cost of an item from the player and add the item to purchases
   *
   * @param item item to purchase
   * @throws AssertionError if the available money is less then the cost of the item.
   */
  public void makePurchase(ShopItem item) {
    assert (item.cost <= money);
    money -= item.cost;
    purchases.add(item);
  }

}
