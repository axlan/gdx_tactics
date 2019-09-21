package com.axlan.gdxtactics.models;

import com.axlan.gdxtactics.models.LevelData.ShopItem;
import java.util.Vector;

public class PlayerResources {
  public int money;
  public final Vector<ShopItem> purchases;
  public PlayerResources() {
    money = 20;
    purchases = new Vector<>();
  }
}
