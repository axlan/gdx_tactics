package com.axlan.gdxtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.VisUI;
import com.badlogic.gdx.utils.Align;

class BriefingView extends ClickListener {

  private LevelData levelData;
  private int curPage;
  private final VisLabel dialogue;
  private final VisLabel avatarLabel;
  final VisTable rootTable;
  private final VisImage avatar;
  private final VisLabel settingLabel;
  private final CompletionObserver observer;

  BriefingView(CompletionObserver observer) {
    this.observer = observer;
    this.dialogue = new VisLabel();
    this.avatarLabel = new VisLabel();
    this.avatar = new VisImage();
    this.settingLabel = new VisLabel();
    this.levelData = null;
    this.rootTable = this.makeBriefingView();
  }

  private boolean isDone() {
    return this.levelData == null || this.curPage >= this.levelData.briefPages.length - 1;
  }

  void setLevelData(LevelData levelData) {
    this.levelData = levelData;
    this.settingLabel.setText(levelData.briefSetting);
    this.updatePage(0);
  }

  @Override
  public void clicked(InputEvent event, float x, float y) {
    super.clicked(event, x, y);
    if (this.isDone()) {
      observer.onDone();
    } else {
      this.updatePage(this.curPage + 1);
    }
    event.stop();
  }

  private void updatePage(int newPage) {
    this.curPage = newPage;
    if (this.levelData != null && this.curPage < this.levelData.briefPages.length) {
      this.dialogue.setText(this.levelData.briefPages[this.curPage].dialogue);
      this.avatarLabel.setText(this.levelData.briefPages[this.curPage].speaker);
      this.avatar.setDrawable(new Texture(Gdx.files.internal("images/avatars/img_avatar.png")));
    }
  }

  private VisTable makeBriefingView() {
    VisTable rootTable = new VisTable();
    rootTable.setFillParent(true);
    // rootTable.setDebug(true);

    this.settingLabel.setAlignment(Align.center);

    LabelStyle labelStyle = settingLabel.getStyle();
    labelStyle.background = VisUI.getSkin().getDrawable("textfield");

    this.avatarLabel.setStyle(labelStyle);

    this.dialogue.setStyle(labelStyle);
    this.dialogue.setAlignment(Align.topLeft);

    VisTextButton nextButton = new VisTextButton("Next");
    nextButton.setColor(Color.BLUE);
    nextButton.addListener(this);

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
    rootTable.add(nextButton).fill();
    rootTable.add(padding).expandX().fill();

    return rootTable;
  }
}
