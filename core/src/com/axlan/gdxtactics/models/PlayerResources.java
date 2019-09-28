package com.axlan.gdxtactics.models;

import com.axlan.gdxtactics.models.LevelData.ShopItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerResources {

  private final ArrayList<ShopItem> purchases;
  private int money;
  public PlayerResources() {
    money = 20;
    purchases = new ArrayList<>();
  }

  public int getMoney() {
    return money;
  }

  public void addMoney(int income) {
    money += income;
  }

  public List<ShopItem> getPurchases() {
    return Collections.unmodifiableList(purchases);
  }

  public void makePurchase(ShopItem item) {
    assert (item.cost <= money);
    money -= item.cost;
    purchases.add(item);
  }

}
