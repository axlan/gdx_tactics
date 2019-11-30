package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.WorldData;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.axlan.gdxtactics.Utilities.getIntRange;

class CityWindow extends VisWindow {

  private final VisLabel currentFriendlyLabel = new VisLabel();
  private final VisLabel currentEnemyLabel = new VisLabel();
  private final VisTextButton moveBtn = new VisTextButton("Move");
  private final VisSelectBox<Integer> amountBox = new VisSelectBox<>();
  private final VisSelectBox<String> toBox = new VisSelectBox<>();
  private String selectedCity = null;
  private List<String> selectedCityNeighbors = null;
  private ArrayList<Movement> movements = new ArrayList<>();

  CityWindow() {
    super("City Properties");
    this.setWidth(250);
    this.setHeight(400);
    this.add(layoutWindow());
  }

  private VisTable layoutWindow() {
    final VisTable root = new VisTable();

    root.add(new VisLabel("Friendly: "));
    root.add(currentFriendlyLabel).colspan(3).row();
    root.add(new VisLabel("Enemy: "));
    root.add(currentEnemyLabel).colspan(3).row();
    root.add(moveBtn);
    moveBtn.setDisabled(true);
    root.add(amountBox);
    root.add(new VisLabel(" to "));
    root.add(toBox).row();
    root.add(new VisLabel("Scheduled Movements")).colspan(4).row();

    final ArrayList<String> movementButtonsItems = new ArrayList<>();
    final VisList<String> movementButtons = new VisList<>();
    root.add(movementButtons).colspan(4).row();

    moveBtn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        int amount = amountBox.getSelected();
        String to = toBox.getSelected();
        final Movement movement = new Movement(to, selectedCity, amount);
        movements.add(movement);
        showCityProperties(selectedCity, selectedCityNeighbors);
        String text = String.format("Cancel: %d from %s to %s", amount, selectedCity, to);
        movementButtonsItems.add(text);
        movementButtons.setItems(movementButtonsItems.toArray(new String[0]));
      }
    });
//    movementButtons.addListener(new ChangeListener() {
//      @Override
//      public void changed(ChangeEvent event, Actor actor) {
//        System.out.println(movementButtons.getSelectedIndex());
////        movements.remove(movement);
////        movementButtonsItems.add(movementButton);
////        movementButtons.setItems(movementButtonsItems.toArray(new VisTextButton[0]));
////        showCityProperties(selectedCity, selectedCityNeighbors);
//      }
//    });

    return root;
  }

  void showCityProperties(String name, List<String> adjacent) {
    selectedCity = name;
    selectedCityNeighbors = new ArrayList<>(adjacent);
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
    int remaining = cityData.stationedFriendlyTroops - removed;
    currentFriendlyLabel.setText(
        String.format("%d (-%d) (+%d) / %d", cityData.stationedFriendlyTroops, removed, added, cityData.maxFriendlyTroops));
    currentEnemyLabel.setText(
        String.format("%d / %d", cityData.stationedEnemyTroops, cityData.maxEnemyTroops));
    if (remaining == 0) {
      moveBtn.setDisabled(true);
      amountBox.setItems();
      toBox.setItems();
      return;
    }
    moveBtn.setDisabled(false);
    amountBox.setItems(getIntRange(1, remaining + 1, 1));
    toBox.setItems(adjacent.toArray(new String[0]));
  }

  private static class Movement {
    final String to;
    final String from;
    final int amount;

    Movement(String to, String from, int amount) {
      this.to = to;
      this.from = from;
      this.amount = amount;
    }
  }
}
