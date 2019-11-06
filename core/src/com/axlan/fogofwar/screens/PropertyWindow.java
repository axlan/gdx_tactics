package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.BattleMap;
import com.axlan.fogofwar.models.BattleState;
import com.axlan.fogofwar.models.FieldedUnit;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.TileProperties;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.axlan.gdxtactics.TilePoint;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

class PropertyWindow extends VisWindow {

  /**
   * BattleView that keeps track of unit states and map information
   */
  private final BattleState battleState;
  /**
   * Class for accessing map data for pathing
   */
  private final BattleMap battleMap;


  PropertyWindow(BattleState battleState, BattleMap battleMap) {
    super("Properties");
    this.battleState = battleState;
    this.battleMap = battleMap;
  }

  //TODO-P2 Add all useful info to window

  /**
   * Update the window to show the properties of objects at the map coordinate
   * @param tile The coordinate of the tile properties to display
   */
  void showTileProperties(TilePoint tile) {
    this.clear();

    FieldedUnit unit = null;
    if (battleState.playerUnits.containsKey(tile)) {
      unit = battleState.playerUnits.get(tile);
    } else if (battleState.enemyUnits.containsKey(tile)) {
      unit = battleState.enemyUnits.get(tile);
    }

    VisTable tilePropertiesTable = new VisTable();

    if (unit != null) {
      VisTable unitPropertiesTable = new VisTable();
      VisSplitPane splitPane = new VisSplitPane(unitPropertiesTable, tilePropertiesTable, true);
      unitPropertiesTable.add(new VisLabel("Unit Properties")).left();
      unitPropertiesTable.row();
      //TODO-P3 Clean up drawable generation
      TextureRegionDrawable unitTexture = LoadedResources.getSpriteLookup()
          .getTextureRegionDrawable(unit.stats.type, Poses.IDLE);
      unitPropertiesTable.add(new VisImage(unitTexture)).size(32, 32);
      this.add(splitPane);
    } else {
      this.add(tilePropertiesTable);
    }
    TileProperties tileProperties = battleMap.getTileProperty(tile);
    if (tileProperties != null) {
      tilePropertiesTable.add(new VisLabel("Tile Properties")).left();
      tilePropertiesTable.row();
      tilePropertiesTable.add(new VisLabel("Passable: " + tileProperties.passable));
    }
  }

}
