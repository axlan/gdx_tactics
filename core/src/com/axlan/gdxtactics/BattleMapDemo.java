package com.axlan.gdxtactics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import com.axlan.gdxtactics.screens.BattleMapView;

import java.util.ArrayList;

public class BattleMapDemo extends Game {

  private ArrayList<Screen> screens = new ArrayList<>();

  @Override
  public void create() {
    this.addScreen(new BattleMapView());
  }

  @Override
  public void render () {
    super.render();
  }

  private void addScreen(Screen newScreen) {
    Screen current = this.getScreen();
    screens.add(current);
    super.setScreen(newScreen);
  }

  boolean closeScreen() {
    System.out.println(screens.size());
    if(!screens.isEmpty()) {
      Screen lastScreen = screens.get(screens.size()-1);
      setScreen(lastScreen);
      screens.remove(lastScreen);
      return true;
    }
    return false;
  }
}