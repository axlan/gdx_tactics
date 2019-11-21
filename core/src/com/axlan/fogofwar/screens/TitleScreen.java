package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.screens.TitleSelectionObserver.TitleSelection;
import com.axlan.gdxtactics.StageBasedScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.Map;

import static com.axlan.gdxtactics.Utilities.enumToButtons;

/**
 * Title screen to start/load game and set options
 */
public class TitleScreen extends StageBasedScreen {

  private TitleSelectionObserver observer;

  public TitleScreen(TitleSelectionObserver observer) {
    this.observer = observer;
    this.stage.addActor(this.makeTitleScreen());
  }

  private VisTable makeTitleScreen() {
    VisTable root = new VisTable();
    root.setFillParent(true);
    // TODO-P3 add drawables to image atlas
    root.setBackground(
        new TextureRegionDrawable(
            new Texture(Gdx.files.internal("images/backgrounds/war_room.png"))));

    // TODO-P3 add better font
    // TODO-P3 add font to skin
    BitmapFont titleFont = new BitmapFont(Gdx.files.internal("fonts/clouds_big.fnt"));
    LabelStyle titleStyle = new LabelStyle(titleFont, Color.GRAY);
    VisLabel title = new VisLabel("Fog of War", titleStyle);
    root.add(title);
    root.row();

    Map<TitleSelection, VisTextButton> buttons = enumToButtons(TitleSelection.values());
    for (final TitleSelection val : buttons.keySet()) {
      VisTextButton button = buttons.get(val);
      button.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              observer.onDone(val);
            }
          });
      root.add(button);
      root.row();
    }

    return root;
  }
}
