package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.*;
import com.axlan.fogofwar.models.LevelData.*;
import com.axlan.gdxtactics.AnimatedSprite;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.axlan.gdxtactics.TilePoint;
import com.axlan.gdxtactics.TiledScreen;
import com.axlan.gdxtactics.Utilities;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisCheckBox.VisCheckBoxStyle;

import java.util.*;

import static com.axlan.gdxtactics.Utilities.getTransparentColor;

/**
 * A screen that lets the player decide where to position their troops. They can view their intel to
 * inform their decision.
 */
public class DeployView extends TiledScreen {

  /**
   * List of colors to differentiate intel sources
   */
  private static final java.util.List<Color> COLOR_LIST =
      Collections.unmodifiableList(
          Arrays.asList(
              Color.BLUE,
              Color.GREEN,
              Color.RED,
              Color.CYAN,
              Color.MAGENTA,
              Color.YELLOW,
              Color.BLACK,
              Color.WHITE
          ));
  /**
   * The set of enemy spawn points randomly selected
   */
  private final Integer[] enemySpawnSelections;
  /**
   * Labels describing how many units of each type are still available to deploy
   */
  private final VisLabel[] remainingLabels;
  //TODO-P3 allow for determinism
  private final Random rand = new Random();
  /** Check boxes to select intel to display */
  private final VisCheckBox[] intelCheckBoxes;
  private final LevelData levelData;
  private final PlayerResources playerResources;
  /**
   * Mapping of map points to the unit type deployed there
   */
  private final HashMap<TilePoint, String> placements = new HashMap<>();
  /** Animations for intel that can be shown on the map */
  private final Array<ArrayList<AnimatedSprite<AtlasRegion>>> sightings;
  /**
   * Button to finalize deployment
   */
  private final VisTextButton doneButton = new VisTextButton("Deploy Troops");
  private final CompletionObserver observer;
  /** The type of unit currently selected to deploy. null for removing previous deployments.*/
  private String selectedUnit;
  /** Keeps track of time for selecting frames for animations */
  private float elapsedTime = 0;

  /**
   *
   * @param observer  observer to call when briefing is finished
   */
  public DeployView(CompletionObserver observer) {
    super("maps/" + LoadedResources.getLevelData().mapName + ".tmx",
            LoadedResources.getReadOnlySettings().tilesPerScreenWidth,
            LoadedResources.getReadOnlySettings().cameraSpeed,
            LoadedResources.getReadOnlySettings().edgeScrollSize);
    this.levelData = LoadedResources.getLevelData();
    this.observer = observer;
    this.playerResources = GameStateManager.gameState.playerResources;
    enemySpawnSelections = new Integer[levelData.enemyFormations.size()];
    for (int i = 0; i < levelData.enemyFormations.size(); i++) {
      Formation formation = levelData.enemyFormations.get(i);
      int selection = rand.nextInt(formation.spawnPoints.size());
      enemySpawnSelections[i] = selection;
    }

    remainingLabels = new VisLabel[levelData.playerUnits.size()];
    stage.addActor(createUnitSelectWindow());
    updateRemainingLabels();

    sightings = new Array<>(playerResources.getPurchases().size());
    intelCheckBoxes = new VisCheckBox[playerResources.getPurchases().size()];
    stage.addActor(createIntelSelectWindow());

    //TODO-P2 Add property window to deploy view

  }

  //TODO-P2 fix window so resize works

  /**
   *
   * @return Create a window for selecting units to deploy
   */
  private VisWindow createUnitSelectWindow() {

    final VisWindow unitSelection = new VisWindow("Unit Selection");
    unitSelection.setPosition(0, 0);
    VisTable table = new VisTable();
    //table.setDebug(true);
    unitSelection.add(table).fill().expand();

    for (int i = 0; i < levelData.playerUnits.size(); i++) {
      final UnitAllotment unit = levelData.playerUnits.get(i);
      //TODO-P3 Make button react to hover, press
      //TODO-P3 Clean up drawable generation
      Button button = new Button(
          LoadedResources.getSpriteLookup().getTextureRegionDrawable(unit.type, Poses.IDLE));
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
        ArrayList<Integer> enemyList = new ArrayList<>();
        Collections.addAll(enemyList, enemySpawnSelections);
        List<Formation> enemyFormations = levelData.enemyFormations;
        GameStateManager.gameState.battleState = new BattleState(enemyList, placements, enemyFormations);
        observer.onDone();
      }
    });
    table.add(doneButton).colspan(2);

    return unitSelection;
  }


  /**
   *
   * @return Create a window for selecting which intel sources to show
   */
  private VisWindow createIntelSelectWindow() {
    VisWindow intelSelection = new VisWindow("Intel Selection");
    VisTable table = new VisTable();
    intelSelection.add(table);
    intelSelection.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    for (int i = 0; i < playerResources.getPurchases().size(); i++) {
      ShopItem resource = playerResources.getPurchases().get(i);
      ArrayList<AnimatedSprite<AtlasRegion>> spotted = new ArrayList<>();
      intelCheckBoxes[i] = new VisCheckBox(resource.name, true);
      VisCheckBoxStyle style = new VisCheckBoxStyle(intelCheckBoxes[i].getStyle());
      style.checkedFontColor = COLOR_LIST.get(i);
      intelCheckBoxes[i].setStyle(style);
      table.add(intelCheckBoxes[i]);
      table.row();

      for (Intel intel : resource.effects) {
        Formation formation = levelData.enemyFormations.get(intel.formationSpottedIdx);
        int spawnSelection = enemySpawnSelections[intel.formationSpottedIdx];

        int numSpotted = Math.min(formation.units.size(), intel.numberOfUnits);
        Array<Integer> unitIds;
        if (intel.spotType == SpotType.RANDOM) {
          unitIds = Utilities.getNElements(rand, numSpotted, formation.units.size());
        } else {
          unitIds = Utilities.getIntRange(0, numSpotted, 1);
        }
        for (int unitId : unitIds) {
          String spottedType = formation.units.get(unitId).unitType;
          TilePoint spottedTilePos = formation.getUnitPos(spawnSelection, unitId);
          AnimatedSprite<AtlasRegion> sprite = LoadedResources
              .getAnimation(spottedType, Poses.IDLE);
          sprite.setColor(COLOR_LIST.get(i));
          sprite.setAlpha(0.5f);
          TilePoint worldPos = tileToWorld(spottedTilePos);
          sprite.setPosition(worldPos.x, worldPos.y);
          spotted.add(sprite);
        }
      }
      sightings.add(spotted);
    }
    return intelSelection;
  }

  /**
   *
   * @param type The identifier for a type of unit
   * @return a count of how many units of the given type haven't been deployed
   */
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

  /**
   * Update the UnitSelectWindow to reflect the units currently deployed. Enable the done button if
   * all units are deployed.
   */
  private void updateRemainingLabels() {
    int totalRemaining = 0;
    for (int i = 0; i < levelData.playerUnits.size(); i++) {
      final UnitAllotment unit = levelData.playerUnits.get(i);
      int remaining = remainingUnits(unit.type);
      totalRemaining += remaining;
      remainingLabels[i].setText(String.format("%s: %d/%d", unit.type, remaining, unit.count));
    }
    doneButton.setDisabled(totalRemaining > 0);
  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
    batch.begin();
    if (selectedUnit != null) {
      TilePoint worldPos = screenToWorld(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
      AnimatedSprite<AtlasRegion> img = LoadedResources.getAnimation(selectedUnit, Poses.IDLE);
      img.setPosition(worldPos.x, worldPos.y);
      img.draw(batch, elapsedTime);
    }
    batch.end();
  }

  @Override
  public void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

    Color spawnColor = getTransparentColor(Color.PURPLE, 0.75f);
    shapeRenderer.begin(ShapeType.Filled);
    shapeRenderer.setColor(spawnColor);
    for (TilePoint point : levelData.playerSpawnPoints) {
      Rectangle tileRect = getTileWorldRect(point);
      shapeRenderer.rect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
    }
    shapeRenderer.end();

    batch.begin();

    for (TilePoint point : placements.keySet()) {
      TilePoint worldPos = tileToWorld(point);
      AnimatedSprite<AtlasRegion> img = LoadedResources
          .getAnimation(placements.get(point), Poses.IDLE);
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
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    TilePoint playerPos = screenToTile(new Vector2(screenX, screenY));

    for (TilePoint point : levelData.playerSpawnPoints) {
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
