package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.WorldData;
import com.axlan.fogofwar.screens.OverWorldMap.Movement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.List;
import java.util.Optional;

/**
 * Window to show information about the selected city
 */
class CityWindow extends VisWindow {

  /**
   * Label to show info about friendly troops
   */
  private final VisLabel currentFriendlyLabel = new VisLabel();
  /** Label to show info about enemy troops */
  private final VisLabel currentEnemyLabel = new VisLabel();
  /**
   * Label to indicate whether a battle will occur
   */
  private final VisLabel contestedLabel = new VisLabel();

  CityWindow() {
    super("City Properties");
    this.setWidth(200);
    this.add(layoutWindow());
  }

  private VisTable layoutWindow() {
    final VisTable root = new VisTable();

    root.add(new VisLabel("Friendly: "));
    root.add(currentFriendlyLabel).row();
    root.add(new VisLabel("Enemy: "));
    root.add(currentEnemyLabel).row();


    root.add(contestedLabel).colspan(2).row();

    return root;
  }

  /**
   * Set the style of {@link #contestedLabel} to a color
   *
   * @param c color for {@link #contestedLabel} text
   */
  private void setContestedColor(Color c) {
    Label.LabelStyle labelStyle = new Label.LabelStyle(contestedLabel.getStyle());
    labelStyle.fontColor = c;
    contestedLabel.setStyle(labelStyle);
  }

  /**
   * Update the window for the selected city
   * @param name name of city to select
   * @param movements currently selected troop movements
   */
  void showCityProperties(String name, List<Movement> movements) {
    getTitleLabel().setText("City Properties: " + name);
    WorldData data = LoadedResources.getGameStateManager().gameState.campaign.getOverWorldData();
    Optional<WorldData.CityData> cityDataOption = data.cities.stream().filter((a) -> a.name.equals(name)).findAny();
    if (!cityDataOption.isPresent()) {
      return;
    }
    WorldData.CityData cityData = cityDataOption.get();
    int added = 0;
    int removed = 0;
    for (Movement movement : movements) {
      if (movement.to.equals(name)) {
        added += movement.amount;
      }
      if (movement.from.equals(name)) {
        removed += movement.amount;
      }
    }
    int nextRound = cityData.stationedFriendlyTroops + added - removed;
    currentFriendlyLabel.setText(
        String.format("%d (-%d) (+%d) / %d", cityData.stationedFriendlyTroops, removed, added, cityData.maxFriendlyTroops));
    currentEnemyLabel.setText(
        String.format("%d / %d", cityData.stationedEnemyTroops, cityData.maxEnemyTroops));
    //TODO-P2 use knowledge of enemy troops to suggest possible, or definite conflict
    if (nextRound > 0 && cityData.stationedEnemyTroops > 0) {
      setContestedColor(Color.RED);
      contestedLabel.setText("Control of city contested");
    } else {
      setContestedColor(Color.GREEN);
      contestedLabel.setText("Control of city uncontested");
    }
  }
}
