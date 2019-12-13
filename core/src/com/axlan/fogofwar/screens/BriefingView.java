package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.BriefingData;
import com.axlan.fogofwar.models.GameState;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.StageBasedScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * A screen that gives information about the upcoming mission in the form of dialogue in a mission
 * briefing.
 */
public class BriefingView extends StageBasedScreen {

  private final BriefingData briefingData;
  private final VisLabel dialogue;
  private final VisLabel avatarLabel;
  private final VisImage avatar;
  private final VisLabel settingLabel;
  private final Runnable completionObserver;
  private int curPage;

  /**
   * @param observer observer to call when briefing is finished
   */
  public BriefingView(Runnable observer) {
    this.completionObserver = observer;
    this.dialogue = new VisLabel();
    this.dialogue.setWrap(true);
    this.avatarLabel = new VisLabel();
    this.avatar = new VisImage();
    this.settingLabel = new VisLabel();
    GameState gameState = LoadedResources.getGameStateManager().gameState;
    if (LoadedResources.getGameStateManager().gameState.scene == SceneLabel.PRE_BATTLE_BRIEF) {
      this.briefingData = gameState.campaign.getLevelBriefing();
    } else {
      this.briefingData = gameState.campaign.getMapBriefing();
    }

    this.settingLabel.setText(this.briefingData.briefSetting);
    this.stage.addActor(this.makeBriefingView());
    this.updatePage(0);
  }

  private boolean isDone() {
    return this.briefingData == null || this.curPage >= this.briefingData.briefPages.size() - 1;
  }

  /**
   * Update UI elements with information on new page. No update occurs if page number is invalid.
   *
   * @param newPage index of new page to show.
   */
  private void updatePage(int newPage) {
    this.curPage = newPage;
    if (this.briefingData != null && this.curPage < this.briefingData.briefPages.size()) {
      this.dialogue.setText(this.briefingData.briefPages.get(this.curPage).dialogue);
      this.avatarLabel.setText(this.briefingData.briefPages.get(this.curPage).speaker);
      // TODO-P2 load speaker font and avatar based on name from map
      // TODO-P3 add drawables to image atlas
      this.avatar.setDrawable(new Texture(Gdx.files.internal("images/avatars/img_avatar.png")));
    }
  }

  /**
   * Lay out UI elements
   *
   * @return Root table for UI
   */
  private VisTable makeBriefingView() {
    // TODO-P3 Reskin and pretty up
    VisTable rootTable = new VisTable();
    rootTable.setFillParent(true);
    // rootTable.setDebug(true);

    this.settingLabel.setAlignment(Align.center);

    LabelStyle labelStyle = new LabelStyle(settingLabel.getStyle());
    labelStyle.background = VisUI.getSkin().getDrawable("textfield");

    this.avatarLabel.setStyle(labelStyle);

    this.dialogue.setStyle(labelStyle);
    this.dialogue.setAlignment(Align.topLeft);

    VisTextButton nextButton = new VisTextButton("Next");
    nextButton.setColor(Color.BLUE);
    nextButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            if (isDone()) {
              completionObserver.run();
            } else {
              updatePage(curPage + 1);
            }
            event.stop();
          }
        });

    VisLabel padding = new VisLabel();
    padding.setStyle(labelStyle);

    rootTable.top();
    rootTable.add(this.settingLabel).expandX().fill().colspan(2);
    rootTable.row();
    rootTable.add(this.avatar).size(100, 100);
    rootTable.add(this.avatarLabel).left().expandX();
    rootTable.row();
    rootTable.add(this.dialogue).expand().fill().colspan(2);
    rootTable.row();
    rootTable.add(padding).expandX().fill();
    rootTable.add(nextButton).fill();


    return rootTable;
  }
}
