package com.axlan.gdxtactics;

import com.axlan.gdxtactics.LevelData.BriefPage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.VisUI;


class BriefingView {

  static VisTable MakeBriefingView(LevelData levelData){
    VisTable rootTable = new VisTable();
    rootTable.setFillParent(true);
    rootTable.setBackground(VisUI.getSkin().getDrawable("window"));
    VisLabel settingLabel = new VisLabel(levelData.briefSetting);
    LabelStyle labelStyle = settingLabel.getStyle();
    labelStyle.background = VisUI.getSkin().getDrawable("textfield");

    VisImage avatar = new VisImage(new Texture(Gdx.files.internal("badlogic.jpg")));
    VisLabel avatarLabel = new VisLabel(levelData.briefPages[0].speaker);
    avatarLabel.setStyle(labelStyle);


    rootTable.add(settingLabel).colspan(2);
    rootTable.row();
    rootTable.add(avatar);
    rootTable.add(avatarLabel);


    return rootTable;
  }
}
