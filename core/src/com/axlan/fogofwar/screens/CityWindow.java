package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.WorldData;
import com.axlan.fogofwar.screens.OverWorldMap.Movement;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.List;
import java.util.Optional;

class CityWindow extends VisWindow {

  private final VisLabel currentFriendlyLabel = new VisLabel();
  private final VisLabel currentEnemyLabel = new VisLabel();

  CityWindow() {
    super("City Properties");
    this.setWidth(200);
    this.add(layoutWindow());
  }

  private VisTable layoutWindow() {
    final VisTable root = new VisTable();

    root.add(new VisLabel("Friendly: "));
    root.add(currentFriendlyLabel).colspan(3).row();
    root.add(new VisLabel("Enemy: "));
    root.add(currentEnemyLabel).colspan(3).row();

    return root;
  }

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
    currentFriendlyLabel.setText(
        String.format("%d (-%d) (+%d) / %d", cityData.stationedFriendlyTroops, removed, added, cityData.maxFriendlyTroops));
    currentEnemyLabel.setText(
        String.format("%d / %d", cityData.stationedEnemyTroops, cityData.maxEnemyTroops));
  }
}
