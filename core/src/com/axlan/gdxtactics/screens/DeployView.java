package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.models.DeploymentSelection;
import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.LevelData.Formation;
import com.axlan.gdxtactics.models.LevelData.Intel;
import com.axlan.gdxtactics.models.LevelData.ShopItem;
import com.axlan.gdxtactics.models.LevelData.SpotType;
import com.axlan.gdxtactics.models.LevelData.UnitAllotment;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.screens.SpriteLookup.Poses;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisCheckBox.VisCheckBoxStyle;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class DeployView extends TiledScreen {

  private static final Color[] INTEL_COLORS = new Color[]{
      Color.BLUE,
      Color.GREEN,
      Color.RED,
      Color.CYAN,
      Color.MAGENTA,
      Color.YELLOW,
      Color.BLACK,
      Color.WHITE
  };
  private final int[] enemySpawnSelections;
  private final VisLabel[] remainingLabels;
  private final VisCheckBox[] intelCheckBoxes;
  //TODO allow for determinism
  private final Random rand = new Random();
  private final HashMap<GridPoint2, String> placements = new HashMap<>();
  private final LevelData levelData;
  private final PlayerResources playerResources;
  private final Array<ArrayList<AnimatedSprite<AtlasRegion>>> sightings;
  private String selectedUnit;
  private float elapsedTime = 0;
  private final VisTextButton doneButton = new VisTextButton("Deploy Troops");
  private final CompletionObserver observer;

  public DeployView(CompletionObserver observer, LevelData levelData,
      PlayerResources playerResources) {
    super("maps/" + levelData.mapName + ".tmx");
    this.levelData = levelData;
    this.observer = observer;
    this.playerResources = playerResources;
    enemySpawnSelections = new int[levelData.enemyFormations.length];
    for (int i = 0; i < levelData.enemyFormations.length; i++) {
      Formation formation = levelData.enemyFormations[i];
      int selection = rand.nextInt(formation.spawnPoints.length);
      enemySpawnSelections[i] = selection;
    }

    remainingLabels = new VisLabel[levelData.playerUnits.length];
    stage.addActor(createUnitSelectWindow());
    updateRemainingLabels();

    sightings = new Array<>(playerResources.purchases.size());
    intelCheckBoxes = new VisCheckBox[playerResources.purchases.size()];
    stage.addActor(createIntelSelectWindow());
  }

  private Array<Integer> getNElements(int n, int length) {
    assert (length >= n);
    Array<Integer> ret = new Array<>();
    for (int i = 0; i < n; i++) {
      int val;
      do {
        val = rand.nextInt(length);
      } while (ret.contains(val, false));
      ret.add(val);
    }
    return ret;
  }

  private Array<Integer> getIntRange(int start, int end, int inc) {
    Integer[] ret = new Integer[(end - start) / inc];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = start + inc * i;
    }
    return new Array<>(ret);
  }

  private VisWindow createUnitSelectWindow() {

    VisWindow unitSelection = new VisWindow("Unit Selection");
    unitSelection.setPosition(0, 0);
    VisTable table = new VisTable();
    //table.setDebug(true);
    unitSelection.add(table).fill().expand();

    for (int i = 0; i < levelData.playerUnits.length; i++) {
      final UnitAllotment unit = levelData.playerUnits[i];
      //TODO Make button react to hover, press
      Button button = new Button(SpriteLookup.getTextureRegionDrawable(unit.type, Poses.IDLE));
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
    table.row();
    doneButton.setDisabled(true);
    doneButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        observer.onDone();
      }
    });
    table.add(doneButton).colspan(2);

    return unitSelection;
  }


  private VisWindow createIntelSelectWindow() {
    VisWindow intelSelection = new VisWindow("Intel Selection");
    VisTable table = new VisTable();
    intelSelection.add(table);
    intelSelection.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    for (int i = 0; i < playerResources.purchases.size(); i++) {
      ShopItem resource = playerResources.purchases.get(i);
      ArrayList<AnimatedSprite<AtlasRegion>> spotted = new ArrayList<>();
      intelCheckBoxes[i] = new VisCheckBox(resource.name, true);
      VisCheckBoxStyle style = new VisCheckBoxStyle(intelCheckBoxes[i].getStyle());
      style.checkedFontColor = INTEL_COLORS[i];
      intelCheckBoxes[i].setStyle(style);
      table.add(intelCheckBoxes[i]);
      table.row();

      for (Intel intel : resource.effects) {
        Formation formation = levelData.enemyFormations[intel.formationSpottedIdx];
        int spawnSelection = enemySpawnSelections[intel.formationSpottedIdx];

        int numSpotted = Math.min(formation.units.length, intel.numberOfUnits);
        Array<Integer> unitIds;
        if (intel.spotType == SpotType.RANDOM) {
          unitIds = getNElements(numSpotted, formation.units.length);
        } else {
          unitIds = getIntRange(0, numSpotted, 1);
        }
        for (int unitId : unitIds) {
          //TODO set sprite based on unit type
          String spottedType = formation.units[unitId].unitType;
          GridPoint2 spottedTilePos = formation.getUnitPos(spawnSelection, unitId);
          AnimatedSprite<AtlasRegion> sprite = SpriteLookup
              .getAnimation(spottedType, Poses.IDLE, 0.1f);
          sprite.setColor(INTEL_COLORS[i]);
          sprite.setAlpha(0.5f);
          Vector2 worldPos = tileToWorld(spottedTilePos);
          sprite.setPosition(worldPos.x, worldPos.y);
          spotted.add(sprite);
        }
      }
      sightings.add(spotted);
    }
    return intelSelection;
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
    int totalRemaining = 0;
    for (int i = 0; i < levelData.playerUnits.length; i++) {
      final UnitAllotment unit = levelData.playerUnits[i];
      int remaining = remainingUnits(unit.type);
      totalRemaining += remaining;
      remainingLabels[i].setText(String.format("%s: %d/%d", unit.type, remaining, unit.count));
    }
    doneButton.setDisabled(totalRemaining > 0);
  }

  public DeploymentSelection getDeploymentSelections() {
    return new DeploymentSelection(enemySpawnSelections, placements);
  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
    batch.begin();
    if (selectedUnit != null) {
      Vector2 worldPos = screenToWorld(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
      AnimatedSprite<AtlasRegion> img = SpriteLookup.getAnimation(selectedUnit, Poses.IDLE, 0.1f);
      img.setPosition(worldPos.x, worldPos.y);
      img.draw(batch, elapsedTime);
    }
    batch.end();
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

    for (GridPoint2 point : placements.keySet()) {
      Vector2 worldPos = tileToWorld(point);
      AnimatedSprite<AtlasRegion> img = SpriteLookup
          .getAnimation(placements.get(point), Poses.IDLE, 0.1f);
      img.setPosition(worldPos.x, worldPos.y);
      img.draw(batch, elapsedTime);
    }

    for (int i = 0; i < intelCheckBoxes.length; i++) {
      if (!intelCheckBoxes[i].isChecked()) {
        continue;
      }
      for (AnimatedSprite<AtlasRegion> sprite : sightings.get(i)) {
        sprite.update(delta);
        sprite.draw(batch);
      }
    }

    batch.end();
  }

  @Override
  public void updateScreen(float delta) {
    elapsedTime += delta;
    moveCameraToLeft = (Gdx.input.isKeyPressed(Input.Keys.LEFT));
    moveCameraToRight = (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
    moveCameraToBottom = (Gdx.input.isKeyPressed(Input.Keys.DOWN));
    moveCameraToTop = (Gdx.input.isKeyPressed(Input.Keys.UP));
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    GridPoint2 playerPos = screenToTile(new Vector2(screenX, screenY));

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

    return super.touchDown(screenX, screenY, pointer, button);
  }
}
