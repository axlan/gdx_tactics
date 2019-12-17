package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.StageBasedScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.Map;
import java.util.function.Consumer;

import static com.axlan.gdxtactics.Utilities.enumToButtons;

/**
 * Title screen to start/load game and set options
 */
public class TitleScreen extends StageBasedScreen {

  private final Consumer<TitleSelection> observer;

  public TitleScreen(Consumer<TitleSelection> observer) {
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
    VisLabel title = new VisLabel("Fog of War", "title");
    root.add(title).colspan(3);
    root.row();

    Map<TitleSelection, VisTextButton> buttons = enumToButtons(TitleSelection.values());
    for (final TitleSelection val : buttons.keySet()) {
      final VisTextButton button = buttons.get(val);
      root.add().expandX();
      root.add(button).fill().uniform().pad(3);
      root.add().expandX();
      if (val == TitleSelection.LOAD_GAME) {
        button.addListener(
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent event, Actor actor) {
                LoadedResources.getOptionsMenu().getLoadMenu().showMenu(stage, button.getX(), button.getY());
              }
            });
      } else {
        button.addListener(
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent event, Actor actor) {
                observer.accept(val);
              }
            });
      }
      root.row();
    }

    return root;
  }

  public enum TitleSelection {
    NEW_GAME,
    LOAD_GAME,
    SETTINGS,
    QUIT
  }

}
