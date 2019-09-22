package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.LevelData.UnitAllotment;
import com.axlan.gdxtactics.models.PlayerResources;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import java.util.HashMap;


public class DeployView extends TiledScreen {

  final VisLabel[] remainingLabels;
  private final AnimatedSprite<AtlasRegion> img;
  private GridPoint2 playerPos = new GridPoint2(0, 10);
  private ParticleEffect effect = new ParticleEffect();
  final HashMap<GridPoint2, String> placements = new HashMap<>();
  LevelData levelData;
  PlayerResources playerResources;
  Array<AtlasRegion> tankIdleRegions;
  private String selectedUnit;


  public DeployView(LevelData levelData, PlayerResources playerResources) {
    super("maps/" + levelData.mapName + ".tmx");
    this.levelData = levelData;
    this.playerResources = playerResources;
    TextureAtlas tankAtlas = new TextureAtlas("images/units/tank.atlas");

    tankIdleRegions = tankAtlas.findRegions("tank_idle");
    img = new AnimatedSprite<>(0.1f, tankIdleRegions);
    //img.setColor(0, 1, 0, 1);

    //messing around with particle effects
    effect.load(Gdx.files.internal("particles/dust.p"), Gdx.files.internal("particles"));
    effect.start();
    //Setting the position of the ParticleEffect
    effect.setPosition(100, 100);

    remainingLabels = new VisLabel[levelData.playerUnits.length];
    stage.addActor(createUnitSelectWindow());
    updateRemainingLabels();

  }

  private VisWindow createUnitSelectWindow() {

    VisWindow unitSelection = new VisWindow("Unit Selection");
    unitSelection.setPosition(0, 0);
    VisTable table = new VisTable();
    //table.setDebug(true);
    unitSelection.add(table).fill().expand();

    for (int i = 0; i < levelData.playerUnits.length; i++) {
      final UnitAllotment unit = levelData.playerUnits[i];
      Button button = new Button(new TextureRegionDrawable(tankIdleRegions.first()));
      button.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              if (remainingUnits(unit.type) > 0) {
                selectedUnit = unit.type;
              }
            }
          });
      table.add(button).size(32, 32).left();
      remainingLabels[i] = new VisLabel();
      table.add(remainingLabels[i]).left().expandX();
      table.row();
    }
    VisTextButton eraseButton = new VisTextButton("Eraser");
    eraseButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        selectedUnit = null;
      }
    });
    table.add(eraseButton);
    return unitSelection;
  }

  private int remainingUnits(String type) {
    for (UnitAllotment unit : levelData.playerUnits) {
      if (!unit.type.equals(type)) {
        continue;
      }
      int placed = 0;
      for (String placement : placements.values()) {
        if (placement.equals(unit.type)) {
          placed++;
        }
      }
      return unit.count - placed;
    }
    return 0;
  }

  private void updateRemainingLabels() {
    for (int i = 0; i < levelData.playerUnits.length; i++) {
      final UnitAllotment unit = levelData.playerUnits[i];
      int remaining = remainingUnits(unit.type);
      remainingLabels[i].setText(String.format("%s: %d/%d", unit.type, remaining, unit.count));
    }
  }


  @Override
  public void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

    Color spawnColor = Color.PURPLE;
    spawnColor.a = 0.75f;
    shapeRenderer.begin(ShapeType.Filled);
    shapeRenderer.setColor(spawnColor);
    for (GridPoint2 point : levelData.playerSpawnPoints) {
      Rectangle tileRect = getTileRect(point);
      shapeRenderer.rect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
    }
    shapeRenderer.end();

    batch.begin();
    img.update(delta);
    if (selectedUnit != null) {
      //Vector2 worldPos = tileToWorld(playerPos);
      Vector2 worldPos = screenToWorld(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
      img.setPosition(worldPos.x, worldPos.y);
      img.draw(batch);
    }
    for (GridPoint2 point : placements.keySet()) {
      Vector2 worldPos = tileToWorld(point);
      img.setPosition(worldPos.x, worldPos.y);
      img.draw(batch);
    }
    //Updating and Drawing the particle effect
    //Delta being the time to progress the particle effect by, usually you pass in Gdx.graphics.getDeltaTime();
    //effect.draw(batch, delta);
    // this.camera.position.x = pos_row;
    // this.camera.position.y = pos_col;
    batch.end();
  }

  @Override
  public void updateScreen(float delta) {
    moveCameraToLeft = (Gdx.input.isKeyPressed(Input.Keys.LEFT));
    moveCameraToRight = (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
    moveCameraToBottom = (Gdx.input.isKeyPressed(Input.Keys.DOWN));
    moveCameraToTop = (Gdx.input.isKeyPressed(Input.Keys.UP));
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    playerPos = screenToTile(new Vector2(screenX, screenY));

    for (GridPoint2 point : levelData.playerSpawnPoints) {
      if (playerPos.equals(point)) {
        placements.remove(point);
        if (selectedUnit != null) {
          placements.put(point, selectedUnit);
          if (remainingUnits(selectedUnit) <= 0) {
            selectedUnit = null;
          }
        }
      }
      updateRemainingLabels();
    }

    System.out.println(playerPos);
    return super.touchDown(screenX, screenY, pointer, button);
  }
}
