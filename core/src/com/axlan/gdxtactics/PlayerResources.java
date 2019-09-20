package com.axlan.gdxtactics;

import com.axlan.gdxtactics.LevelData.ShopItem;
import java.util.Vector;

class PlayerResources {
  int money;
  final Vector<ShopItem> purchases;
  PlayerResources() {
    money = 20;
    purchases = new Vector<>();
  }
}
