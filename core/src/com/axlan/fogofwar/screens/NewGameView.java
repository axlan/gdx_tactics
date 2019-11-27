package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.campaigns.CampaignBase;
import com.axlan.fogofwar.campaigns.CampaignSet;
import com.axlan.fogofwar.models.GameState;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.StageBasedScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * Menu for selecting campaign for a new game
 */
public class NewGameView extends StageBasedScreen {
  private Runnable observer;

  public NewGameView(Runnable observer) {
    this.observer = observer;
    this.stage.addActor(this.makeNewGameScreen());
  }

  private VisTable makeNewGameScreen() {
    VisTable root = new VisTable();
    root.setFillParent(true);

    root.add(new VisLabel("Select Campaign: "));
    final VisSelectBox<String> resolutionSelect = new VisSelectBox<>();
    resolutionSelect.setItems(CampaignSet.CAMPAIGN_NAMES.toArray(new String[0]));
    root.add(resolutionSelect);
    root.row();
    root.row();

    VisTextButton submitButton = new VisTextButton("Submit");
    submitButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            CampaignBase campaign = CampaignSet.newCampaignByName(resolutionSelect.getSelected());
            LoadedResources.getGameStateManager().gameState = new GameState(campaign);
            observer.run();
          }
        });
    root.add(submitButton);

    VisTextButton cancelButton = new VisTextButton("Cancel");
    cancelButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            LoadedResources.getGameStateManager().gameState = null;
            observer.run();
          }
        });
    root.add(cancelButton);


    return root;
  }
}
