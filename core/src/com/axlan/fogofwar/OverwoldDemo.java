package com.axlan.fogofwar;

import com.axlan.fogofwar.models.Campaign;
import com.axlan.fogofwar.models.LevelData;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.screens.DeployView;
import com.axlan.fogofwar.screens.OverWorldMap;
import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

/**
 * Class to run a demo of the {@link DeployView}
 */
public class OverwoldDemo extends Game {

  @Override
  public void create() {
    VisUI.load();
    LoadedResources.initializeGlobal();
    LoadedResources.initializeLevel();
    LevelData levelData = LoadedResources.getLevelData();
    Campaign campaign = new Campaign(new Campaign.WorldMap("overworld"));
    this.setScreen(new OverWorldMap(campaign));
  }
}
