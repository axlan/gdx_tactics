package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.campaigns.CampaignBase;
import com.axlan.fogofwar.campaigns.TutorialCampaign;
import com.axlan.fogofwar.models.GameState;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.CompletionObserver;
import com.axlan.gdxtactics.StageBasedScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Title screen to start/load game and set options
 */
public class NewGameView extends StageBasedScreen {

  static private final ArrayList<CampaignBase> CAMPAIGNS = new ArrayList<>(Arrays.asList(new TutorialCampaign()));
  static private final String[] CAMPAIGN_NAMES = CAMPAIGNS.stream().map(c -> c.getName()).toArray(String[]::new);
  private CompletionObserver observer;

  public NewGameView(CompletionObserver observer) {
    this.observer = observer;
    this.stage.addActor(this.makeNewGameScreen());
  }

  private VisTable makeNewGameScreen() {
    VisTable root = new VisTable();
    root.setFillParent(true);

    root.add(new VisLabel("Select Campaign: "));
    final VisSelectBox<String> resolutionSelect = new VisSelectBox<>();
    resolutionSelect.setItems(CAMPAIGN_NAMES);
    root.add(resolutionSelect);
    root.row();
    root.row();

    VisTextButton submitButton = new VisTextButton("Submit");
    submitButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            CampaignBase campaign = CAMPAIGNS.get(resolutionSelect.getSelectedIndex());
            LoadedResources.getGameStateManager().gameState = new GameState(campaign);
            observer.onDone();
          }
        });
    root.add(submitButton);

    VisTextButton cancelButton = new VisTextButton("Cancel");
    cancelButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            LoadedResources.getGameStateManager().gameState = null;
            observer.onDone();
          }
        });
    root.add(cancelButton);


    return root;
  }
}
