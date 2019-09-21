package com.axlan.gdxtactics;

import com.axlan.gdxtactics.screens.BattleMapView;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

public class BattleMapDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    this.setScreen(new BattleMapView());
  }

}
