package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.*;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.axlan.gdxtactics.TilePoint;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.*;

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

  // TODO-P2 Add all useful info to window

  /**
   * Update the window to show the properties of objects at the map coordinate
   *
   * @param tile The coordinate of the tile properties to display
   */
  void showTileProperties(TilePoint tile, boolean isVisible) {
    this.clear();

    FieldedUnit unit = null;
    if (battleState.playerUnits.containsKey(tile)) {
      unit = battleState.playerUnits.get(tile);
    } else if (isVisible && battleState.enemyUnits.containsKey(tile)) {
      unit = battleState.enemyUnits.get(tile);
    }

    VisTable tilePropertiesTable = new VisTable();

    if (unit != null) {
      VisTable unitPropertiesTable = new VisTable();
      VisSplitPane splitPane = new VisSplitPane(unitPropertiesTable, tilePropertiesTable, true);
      splitPane.setFillParent(true);
      unitPropertiesTable.add(new VisLabel("Unit Properties")).padTop(getTitleLabel().getHeight() * 3).left();
      unitPropertiesTable.row();
      // TODO-P3 Clean up drawable generation
      TextureRegionDrawable unitTexture =
          LoadedResources.getSpriteLookup()
              .getTextureRegionDrawable(unit.getStats().type, Poses.IDLE);
      int buttonSize = Gdx.graphics.getWidth() / LoadedResources.getReadOnlySettings().tilesPerScreenWidth;
      unitPropertiesTable.add(new VisImage(unitTexture)).size(buttonSize, buttonSize).row();
      unitPropertiesTable.add(new VisLabel("HP: " + unit.currentHealth + "/" + unit.getStats().maxHealth)).row();
      unitPropertiesTable.add(new VisLabel("Attack: " + unit.getStats().attack)).padBottom(getTitleLabel().getHeight() * 2);

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
    pack();
  }
}
