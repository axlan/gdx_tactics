package com.axlan.fogofwar;

import com.axlan.fogofwar.models.*;
import com.axlan.fogofwar.screens.BattleView;
import com.axlan.gdxtactics.TilePoint;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class to run a demo of the {@link BattleView}
 */
public class BattleDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();
    LevelData levelData = LoadedResources.getLevelData();
    PlayerResources playerResources = GameStateManager.gameState.playerResources;
    playerResources.addMoney((int) 1e10);
    playerResources.makePurchase(levelData.shopItems.get(0));
    playerResources.makePurchase(levelData.shopItems.get(1));
    HashMap<TilePoint, String> playerUnitPlacements = new HashMap<>();
    playerUnitPlacements.put(new TilePoint(3, 6), "tank");
    playerUnitPlacements.put(new TilePoint(4, 6), "tank");
    ArrayList<Integer> dummyEnemy = new ArrayList<>();
    dummyEnemy.add(0);
    List<LevelData.Formation> enemyFormations = levelData.enemyFormations;
    GameStateManager.gameState.battleState = new BattleState(dummyEnemy, playerUnitPlacements, enemyFormations);
    this.setScreen(new BattleView());
  }

}
