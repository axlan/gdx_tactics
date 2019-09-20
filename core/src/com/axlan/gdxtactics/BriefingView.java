package com.axlan.gdxtactics;

import com.axlan.gdxtactics.LevelData.BriefPage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.VisUI;
import com.badlogic.gdx.utils.Align;


class BriefingView extends ClickListener {

  private final LevelData levelData;
  private int curPage;
  private final VisLabel dialogue;
  private final VisLabel avatarLabel;

  BriefingView(LevelData levelData){
    this.levelData = levelData;
    this.dialogue = new VisLabel();
    this.avatarLabel = new VisLabel();
  }

  @Override
  public void clicked(InputEvent event, float x, float y) {
    if (this.curPage < this.levelData.briefPages.length - 1) {
      this.curPage++;
      this.dialogue.setText(this.levelData.briefPages[this.curPage].dialogue);
      this.avatarLabel.setText(this.levelData.briefPages[this.curPage].speaker);
    }
    event.stop();
  }

  VisTable MakeBriefingView(){
    this.curPage = 0;
    VisTable rootTable = new VisTable();
    rootTable.setFillParent(true);
    //rootTable.setDebug(true);

    VisLabel settingLabel = new VisLabel(levelData.briefSetting);
    settingLabel.setAlignment(Align.center);

    LabelStyle labelStyle = settingLabel.getStyle();
    labelStyle.background = VisUI.getSkin().getDrawable("textfield");

    VisImage avatar = new VisImage(new Texture(Gdx.files.internal("images/avatars/img_avatar.png")));

    this.avatarLabel.setText(this.levelData.briefPages[0].speaker);
    this.avatarLabel.setStyle(labelStyle);


    this.dialogue.setText(this.levelData.briefPages[0].dialogue);
    this.dialogue.setStyle(labelStyle);
    this.dialogue.setAlignment(Align.topLeft);

    VisTextButton nextButton = new VisTextButton("Next");
    nextButton.setColor(Color.BLUE);
    nextButton.addListener(this);

    VisLabel padding = new VisLabel();
    padding.setStyle(labelStyle);


    rootTable.top();
    rootTable.add(settingLabel).expandX().fill().colspan(2);
    rootTable.row();
    rootTable.add(avatar).size(100,100);
    rootTable.add(this.avatarLabel).left().expandX();
    rootTable.row();
    rootTable.add(this.dialogue).expand().fill().colspan(2);
    rootTable.row();
    rootTable.add(nextButton).fill();
    rootTable.add(padding).expandX().fill();

    return rootTable;
  }
}
