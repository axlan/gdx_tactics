package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.DeploymentSelection;
import com.axlan.fogofwar.models.FieldedUnit;
import com.axlan.fogofwar.models.FieldedUnit.State;
import com.axlan.fogofwar.models.GameStateManager;
import com.axlan.fogofwar.models.LevelData.Formation;
import com.axlan.fogofwar.models.LevelData.UnitStart;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.UnitStats;
import com.axlan.gdxtactics.AnimatedSprite;
import com.axlan.gdxtactics.PathVisualizer;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.axlan.gdxtactics.TilePoint;
import com.axlan.gdxtactics.TiledScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTextButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO-P1 Add concept of turns and mechanism to end player turns
//TODO-P1 Show terrain and unit info under cursor
//TODO-P1 Add attack action
//TODO-P1 Add enemy turn
//TODO-P1 Add scenario goal along with victory / failure conditions
//TODO-P2 Add fog of war mechanic
//TODO-P2 Add overlay when unit is selected to show move and attack range
//TODO-P3 Add intel view
//TODO-P3 Add support for more then one enemy or ally
//TODO-P3 Add touch screen support
//TODO-P3 Add battle animations

/**
 * A screen to play out the turn based battle. Player commands their troops against enemy AI.
 */
public class BattleView extends TiledScreen {

  /**
   * Mapping of points on the map, to the players units on that tile.
   */
  private final HashMap<TilePoint, FieldedUnit> playerUnits = new HashMap<>();
  /**
   * Mapping of points on the map, to the enemy units on that tile.
   */
  private final HashMap<TilePoint, FieldedUnit> enemyUnits = new HashMap<>();
  /**
   * Key to {@link #playerUnits} for the unit currently being issued a command
   */
  private TilePoint selected = null;
  /** Key to {@link #playerUnits} for the unit currently in a movement animation */
  private TilePoint moving = null;
  /** Keeps track of time for selecting frames for animations */
  private float elapsedTime = 0;
  /** Used to draw potential paths and movement animations on the map */
  private final PathVisualizer pathVisualizer;
  /**
   * Path from {@link #selected} to the mouse location
   */
  private List<TilePoint> selectedUnitPath = null;
  /**
   * Disable selecting units when some other action needs to complete
   */
  private boolean disableSelection = false;
  /**
   * Stores a moved unit's start position to allow move to be undone
   */
  private TilePoint moveStartPos = null;


  public BattleView() {
    super("maps/" + LoadedResources.getLevelData().mapName + ".tmx",
        LoadedResources.getSettings().tilesPerScreenWidth,
        LoadedResources.getSettings().cameraSpeed,
        LoadedResources.getSettings().edgeScrollSize);
    Map<String, UnitStats> unitStats = LoadedResources.getUnitStats();
    pathVisualizer = new PathVisualizer(getTilePixelSize(), LoadedResources.getSpriteLookup());
    DeploymentSelection deploymentSelection = GameStateManager.deploymentSelection;
    for (TilePoint point : deploymentSelection.getPlayerUnitPlacements().keySet()) {
      String unitType = deploymentSelection.getPlayerUnitPlacements().get(point);
      playerUnits.put(point, new FieldedUnit(unitStats.get(unitType)));
    }
    List<Formation> enemyFormations = LoadedResources.getLevelData().enemyFormations;
    for (int formationIdx = 0; formationIdx < enemyFormations.size(); formationIdx++) {
      Formation formation = enemyFormations.get(formationIdx);
      int spawnIdx = deploymentSelection.getEnemySpawnSelections().get(formationIdx);
      for (UnitStart unit : formation.units) {
        TilePoint startPos = formation.getUnitPos(spawnIdx, unit);
        enemyUnits.put(startPos, new FieldedUnit(unitStats.get(unit.unitType)));
      }
    }
  }

  /**
   * Move the player controlled unit in playerUnits to a new position key
   *
   * @param oldPos old unit position
   * @param newPos new unit position
   */
  private void movePlayerUnit(TilePoint oldPos, TilePoint newPos) {
    final FieldedUnit unit = playerUnits.get(oldPos);
    playerUnits.remove(oldPos);
    playerUnits.put(newPos, unit);
  }

  /**
   * Create a dialogue window to select the action the unit should take
   *
   * @param unitPos the position of the acting unit
   */
  private void createActionDialogue(final TilePoint unitPos) {
    final FieldedUnit unit = playerUnits.get(unitPos);
    VisDialog actionDialogue = new VisDialog("Choose Action");
    actionDialogue.button("Attack");
    actionDialogue.getButtonsTable().row();
    VisTextButton waitButton = new VisTextButton("Wait");
    waitButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        unit.state = State.DONE;
        disableSelection = false;
      }
    });
    actionDialogue.button(waitButton);
    actionDialogue.getButtonsTable().row();
    VisTextButton cancelButton = new VisTextButton("Cancel");
    cancelButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        unit.state = State.IDLE;
        movePlayerUnit(unitPos, moveStartPos);
        disableSelection = false;
      }
    });
    actionDialogue.button(cancelButton);
    Vector2 screenPos = tileToScreen(unitPos);
    //TODO-P2 Make dialogue less likely to block relevant map space
    actionDialogue.setPosition(screenPos.x, screenPos.y);
    stage.addActor(actionDialogue);
  }

  @Override
  public void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
    batch.begin();
    for (TilePoint point : playerUnits.keySet()) {
      FieldedUnit unit = playerUnits.get(point);
      AnimatedSprite<AtlasRegion> sprite = null;
      if (unit.state == State.SELECTED) {
        sprite = LoadedResources.getAnimation(unit.stats.type,
            Poses.LEFT);
      } else if (unit.state == State.IDLE || unit.state == State.DONE) {
        sprite = LoadedResources.getAnimation(unit.stats.type,
            Poses.IDLE);
      }
      if (sprite != null) {
        TilePoint worldPos = tileToWorld(point);
        sprite.setPosition(worldPos.x, worldPos.y);
        if (unit.state == State.DONE) {
          sprite.setColor(Color.GRAY);
        }
        sprite.draw(batch, elapsedTime);
      }
    }
    for (TilePoint point : enemyUnits.keySet()) {
      FieldedUnit unit = enemyUnits.get(point);
      AnimatedSprite<AtlasRegion> sprite = null;
      if (unit.state == State.IDLE || unit.state == State.DONE) {
        sprite = LoadedResources.getAnimation(unit.stats.type,
            Poses.IDLE);
      }
      if (sprite != null) {
        TilePoint worldPos = tileToWorld(point);
        sprite.setPosition(worldPos.x, worldPos.y);
        if (unit.state == State.DONE) {
          sprite.setColor(Color.GRAY);
        }
        sprite.setColor(Color.BLUE);
        sprite.draw(batch, elapsedTime);
      }
    }
    if (moving != null) {
      if (pathVisualizer.drawAnimatedSpritePath(delta, batch)) {
        FieldedUnit unit = playerUnits.get(moving);
        unit.state = State.DONE;
        createActionDialogue(moving);
        moving = null;
      }
    }
    batch.end();
    shapeRenderer.begin(ShapeType.Filled);
    if (selectedUnitPath != null) {
      shapeRenderer.setColor(Color.BLUE);
      pathVisualizer.drawArrow(shapeRenderer, selectedUnitPath);
    }
    shapeRenderer.end();
  }

  /**
   * Check if a tile in the map can be passed through. Tiles in the TMX map need a "passable"
   * property or it will throw a  ClassCastException
   *
   * @param point        the 2D index of the tile of interest
   * @param blockedTiles list of additional tiles the unit can't move through
   * @return whether the tile can be passed through
   */
  private boolean isTilePassable(TilePoint point, List<TilePoint> blockedTiles) {
    return isTilePassable(point) && !blockedTiles.contains(point);
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    TilePoint playerPos = screenToTile(new Vector2(screenX, screenY));
    if (!disableSelection) {
      if (selected != null) {
        if (playerPos.equals(selected)) {
          disableSelection = true;
          createActionDialogue(selected);
        } else if (selectedUnitPath != null) {
          ArrayList<TilePoint> enemyPosList = new ArrayList<>(enemyUnits.keySet());
          if (!playerUnits.containsKey(playerPos) && isTilePassable(playerPos, enemyPosList)) {
            FieldedUnit unit = playerUnits.get(selected);
            unit.state = State.MOVING;
            movePlayerUnit(selected, playerPos);
            moving = playerPos;
            disableSelection = true;
            pathVisualizer.startAnimation(
                unit.stats.type,
                selectedUnitPath,
                LoadedResources.getSettings().sprites.movementDurationPerTile,
                LoadedResources.getSettings().sprites.frameDuration);
          } else {
            playerUnits.get(selected).state = State.IDLE;
          }
        }
        selected = null;
        selectedUnitPath = null;
      }
      if (playerUnits.containsKey(playerPos) && playerUnits.get(playerPos).state == State.IDLE) {
        selected = playerPos;
        moveStartPos = playerPos;
        playerUnits.get(playerPos).state = State.SELECTED;
      }
    }
    return super.touchDown(screenX, screenY, pointer, button);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    super.mouseMoved(screenX, screenY);
    if (selected != null) {
      TilePoint playerPos = screenToTile(new Vector2(screenX, screenY));
      // Only recalculate path when mouse has moved onto new tile.
      if (selectedUnitPath != null && playerPos.equals(
          selectedUnitPath.get(selectedUnitPath.size() - 1))) {
        return false;
      }
      ArrayList<TilePoint> enemyPosList = new ArrayList<>(enemyUnits.keySet());
      if (!playerPos.equals(selected) && isTilePassable(playerPos, enemyPosList)) {
        selectedUnitPath = getShortestPath(selected, playerPos, enemyPosList);
      }
    }
    return false;
  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  public void updateScreen(float delta) {
    elapsedTime += delta;
  }

}
