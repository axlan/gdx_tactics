package com.axlan.gdxtactics;

import com.axlan.gdxtactics.screens.BattleMapView;
import com.badlogic.gdx.Game;

public class BattleMapDemo extends Game {

  @Override
  public void create() {
    this.setScreen(new BattleMapView());
  }

}
