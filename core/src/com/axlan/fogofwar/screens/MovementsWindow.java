package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.WorldData;
import com.axlan.fogofwar.screens.OverWorldMap.Movement;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.List;
import java.util.Optional;

import static com.axlan.gdxtactics.Utilities.getIntRange;

/**
 * Window for selecting troop movements
 */
class MovementsWindow extends VisTable {

  /**
   * Button to submit a troop movement
   */
  private final VisTextButton moveBtn = new VisTextButton("Move");
  /**
   * Box to select number of troops
   */
  private final VisSelectBox<Integer> amountBox = new VisSelectBox<>();
  /**
   * Box to select city to move troops to
   */
  private final VisSelectBox<String> toBox = new VisSelectBox<>();
  /**
   * List of movements to modify
   */
  private List<Movement> movements;
  /**
   * City selected to move troops from
   */
  private String selectedCity = null;
  /**
   * Callback to call after updating {@link #movements}
   */
  private Runnable updateOthers;

  MovementsWindow(List<Movement> movements, Runnable updateOthers) {
    setBackground(VisUI.getSkin().getDrawable("window"));
    this.updateOthers = updateOthers;
    this.movements = movements;
    moveBtn.addListener(new ChangeListener() {
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
        } else {
          movement = new Movement(to, selectedCity, amount);
        }
        movements.add(movement);
        String text = String.format("Cancel: %d from %s to %s", movement.amount, selectedCity, to);
        updateOthers.run();
      }
    });
  }

  /**
   * Regenerate window with options for the selected city
   * @param name name of city to move troops from
   * @param adjacent names of adjacent cities to move troops to
   */
  void updateAddMovementButton(String name, List<String> adjacent) {
    this.selectedCity = name;
    WorldData data = LoadedResources.getGameStateManager().gameState.campaign.getOverWorldData();
    Optional<WorldData.CityData> cityDataOption = data.cities.stream().filter((a) -> a.name.equals(name)).findAny();
    if (!cityDataOption.isPresent()) {
      pack();
      return;
    }
    WorldData.CityData cityData = cityDataOption.get();
    int remaining = cityData.stationedFriendlyTroops;
    for (Movement movement : movements) {
      if (movement.from.equals(name)) {
        remaining -= movement.amount;
      }
    }
    clear();

    if (remaining == 0) {
      add("No available troops").row();
    } else {
      add("Move from " + name).row();
      add(amountBox).row();
      add("troops to").row();
      add(toBox).row();
      amountBox.setItems(getIntRange(1, remaining + 1, 1));
      toBox.setItems(adjacent.toArray(new String[0]));
      add(moveBtn).row();
    }

    if (movements.isEmpty()) {
      add("No Movements Scheduled").expand().align(Align.bottom).row();
    } else {
      add("Scheduled Movements:").expand().align(Align.bottom).row();
    }

    for (Movement movement : movements) {

      add(String.format(
          "%d from %s to %s", movement.amount, movement.from, movement.to))
          .row();
      VisTextButton cancel = new VisTextButton("Cancel");
      add(cancel).row();
      cancel.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              movements.remove(movement);
              updateOthers.run();
            }
          });
    }
  }

}
