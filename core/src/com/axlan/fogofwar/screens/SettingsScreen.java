package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.StageBasedScreen;
import com.axlan.gdxtactics.TilePoint;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;

public class SettingsScreen extends StageBasedScreen {

  /**
   * Supported resolutions
   */
  private static final TilePoint[] RESOLUTIONS =
      new TilePoint[]{
          new TilePoint(640, 480),
          new TilePoint(800, 600),
          new TilePoint(960, 720),
          new TilePoint(1024, 768),
          new TilePoint(1280, 960),
          new TilePoint(1400, 1050),
          new TilePoint(1440, 1080),
          new TilePoint(1600, 1200),
          new TilePoint(1856, 1392),
          new TilePoint(1920, 1440),
          new TilePoint(2048, 1536)
      };

  private Runnable observer;

  public SettingsScreen(Runnable observer) {
    this.observer = observer;
    this.stage.addActor(this.makeSettingsScreen());
  }

  private VisTable makeSettingsScreen() {
    VisTable root = new VisTable();
    root.setFillParent(true);

    root.add(new VisLabel("Resolution"));
    final VisSelectBox<TilePoint> resolutionSelect = new VisSelectBox<>();
    resolutionSelect.setItems(RESOLUTIONS);
    resolutionSelect.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            LoadedResources.getEditableSettings().screenSize = resolutionSelect.getSelected();
            LoadedResources.getEditableSettings().apply();
          }
        });
    resolutionSelect.setSelected(LoadedResources.getEditableSettings().screenSize);
    root.add(resolutionSelect);
    root.row();

    root.add(new VisLabel("Full Screen"));
    final VisCheckBox fullScreenBox = new VisCheckBox("");
    fullScreenBox.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            LoadedResources.getEditableSettings().fullScreen = fullScreenBox.isChecked();
            resolutionSelect.setDisabled(fullScreenBox.isChecked());
            LoadedResources.getEditableSettings().apply();
          }
        });
    fullScreenBox.setChecked(LoadedResources.getEditableSettings().fullScreen);
    root.add(fullScreenBox);
    root.row();

    VisTextButton doneButton = new VisTextButton("Done");
    doneButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            LoadedResources.writeEditableSettings();
            observer.run();
          }
        });
    root.add(doneButton);

    return root;
  }
}
