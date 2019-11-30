package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.WorldData;
import com.axlan.fogofwar.screens.OverWorldMap.Movement;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.axlan.gdxtactics.Utilities.getIntRange;

class MovementsWindow extends VisWindow {

  private final VisTextButton moveBtn = new VisTextButton("Move");
  private final VisSelectBox<Integer> amountBox = new VisSelectBox<>();
  private final VisSelectBox<String> toBox = new VisSelectBox<>();
  private List<Movement> movements;
  private String selectedCity = null;
  private Runnable updateOthers;

  MovementsWindow(List<Movement> movements, Runnable updateOthers) {
    super("Troop Movements");
    this.updateOthers = updateOthers;
    this.movements = movements;
    this.setWidth(250);
    this.add(layoutWindow());
  }

  private VisTable layoutWindow() {
    final VisTable root = new VisTable();

    root.add(moveBtn);
    moveBtn.setDisabled(true);
    root.add(amountBox);
    root.add(new VisLabel(" to "));
    root.add(toBox).row();
    root.add(new VisLabel("Scheduled Movements")).colspan(4).row();

    final ArrayList<String> movementButtonsItems = new ArrayList<>();
    final VisList<String> movementsList = new VisList<>();
    root.add(movementsList).colspan(4).row();

    moveBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            int amount = amountBox.getSelected();
            String to = toBox.getSelected();
            int match = -1;
            for (int i = 0; i < movements.size(); i++) {
              if (movements.get(i).to.equals(to) && movements.get(i).from.equals(selectedCity)) {
                match = i;
                break;
              }
            }
            Movement movement;
            if (match != -1) {
              movement = new Movement(to, selectedCity, amount + movements.get(match).amount);
              movements.remove(match);
              movementButtonsItems.remove(match);
            } else {
              movement = new Movement(to, selectedCity, amount);
            }
            movements.add(movement);
            String text = String.format("Cancel: %d from %s to %s", movement.amount, selectedCity, to);
            movementButtonsItems.add(text);
            movementsList.setItems(movementButtonsItems.toArray(new String[0]));
            updateOthers.run();
          }
        });

    movementsList.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        int idx = movementsList.getSelectedIndex();
        movements.remove(idx);
        movementButtonsItems.remove(idx);
        movementsList.setItems(movementButtonsItems.toArray(new String[0]));
        updateOthers.run();
      }
    });

    return root;
  }

  void updateAddMovementButton(String name, List<String> adjacent) {
    this.selectedCity = name;
    WorldData data = LoadedResources.getGameStateManager().gameState.campaign.getOverWorldData();
    Optional<WorldData.CityData> cityDataOption = data.cities.stream().filter((a) -> a.name.equals(name)).findAny();
    if (!cityDataOption.isPresent()) {
      return;
    }
    WorldData.CityData cityData = cityDataOption.get();
    int remaining = cityData.stationedFriendlyTroops;
    for (Movement movement : movements) {
      if (movement.from.equals(name)) {
        remaining -= movement.amount;
      }
    }
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

}
