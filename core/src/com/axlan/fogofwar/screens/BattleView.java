package com.axlan.fogofwar.screens;

import static com.axlan.gdxtactics.Utilities.listGet2d;
import static com.axlan.gdxtactics.Utilities.listGetTail;

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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTextButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO-P1 Add level data on who goes first
//TODO-P1 Add concept of turns and mechanism to end player turns
//TODO-P1 Show terrain and unit info under cursor
//TODO-P1 Add enemy turn
//TODO-P1 Add scenario goal along with victory / failure conditions
//TODO-P2 Separate UI and logic about the state of the battle
//TODO-P2 Add fog of war mechanic
//TODO-P2 Add overlay when unit is selected to show move and attack range (redo path calculation)
//TODO-P3 Add intel view
//TODO-P3 Add end turn button / Give option to end turn when no active units left.
//TODO-P3 Add support for more then one enemy or ally AI/Commander
//TODO-P3 Add touch screen support
//TODO-P3 Add battle animations

/**
 * A screen to play out the turn based battle. Player commands their troops against enemy AI.
 */
public class BattleView extends TiledScreen {

  /**
   * Current state for state machine that controls UI
   */
  private BattleViewState state = BattleViewState.IDLE;

  /**
   * Mapping of points on the map, to the players units on that tile.
   */
  private final HashMap<TilePoint, FieldedUnit> playerUnits = new HashMap<>();
  /**
   * Mapping of points on the map, to the enemy units on that tile.
   */
  private final HashMap<TilePoint, FieldedUnit> enemyUnits = new HashMap<>();
  /**
   * Key to {@link #playerUnits} for where a unit was originally selected
   */
  private TilePoint startLocation = null;
  /**
   * Key to {@link #playerUnits} for where the unit moves to
   */
  private TilePoint endLocation = null;
  /**
   * Path from {@link #startLocation} to the mouse location
   */
  private List<TilePoint> selectedUnitPath = null;
  /**
   * Path from {@link #startLocation} to the last valid mouse position
   */
  private List<TilePoint> shownUnitPath = null;
  /** Keeps track of time for selecting frames for animations */
  private float elapsedTime = 0;
  /** Used to draw potential paths and movement animations on the map */
  private final PathVisualizer pathVisualizer;
  /**
   * Unmodifiable 2D list of the properties of each tile in the map
   */
  private final List<List<TileProperties>> tileProperties;

  /**
   * Check if all player units are done and start enemy turn if they have.
   */
  private void checkTurnDone() {
    boolean done = true;
    for (FieldedUnit unit : playerUnits.values()) {
      done &= unit.state == State.DONE;
    }
    if (done) {
      state = BattleViewState.ENEMY_TURN;
    } else {
      state = BattleViewState.IDLE;
    }
  }
  /**
   * Targets for an attack
   */
  private List<TilePoint> targetSelection = null;


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

    TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get(0);
    ArrayList<List<TileProperties>> tmpTileProperties = new ArrayList<>();
    for (int r = 0; r < getMapTileSize().x; r++) {
      ArrayList<TileProperties> tmpColumn = new ArrayList<>();
      for (int c = 0; c < getMapTileSize().y; c++) {
        tmpColumn.add(new TileProperties(tileLayer.getCell(r, c).getTile().getProperties()));
      }
      tmpTileProperties.add(Collections.unmodifiableList(tmpColumn));
    }
    tileProperties = Collections.unmodifiableList(tmpTileProperties);



  }

  /**
   * Create a dialogue window to select the action the unit should take
   *
   * @param unitPos the position of the acting unit
   */
  private void createActionDialogue(final TilePoint unitPos) {
    final FieldedUnit unit = playerUnits.get(unitPos);
    final VisDialog actionDialogue = new VisDialog("Choose Action");
    final ArrayList<TilePoint> targets = new ArrayList<>();
    for (TilePoint enemyPos : enemyUnits.keySet()) {
      int distance = unitPos.absDiff(enemyPos);
      if (distance >= unit.stats.minAttackRange && distance <= unit.stats.minAttackRange) {
        targets.add(enemyPos);
      }
    }
    if (!targets.isEmpty()) {
      VisTextButton attackButton = new VisTextButton("Attack");
      attackButton.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              targetSelection = targets;
              state = BattleViewState.CHOOSE_ATTACK;
            }
          });
      actionDialogue.button(attackButton);
      actionDialogue.getButtonsTable().row();
    }
    VisTextButton waitButton = new VisTextButton("Wait");
    waitButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        unit.state = State.DONE;
        checkTurnDone();
      }
    });
    actionDialogue.button(waitButton);
    actionDialogue.getButtonsTable().row();
    VisTextButton cancelButton = new VisTextButton("Cancel");
    cancelButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        unit.state = State.IDLE;
        movePlayerUnit(endLocation, startLocation);
        state = BattleViewState.IDLE;
      }
    });
    actionDialogue.button(cancelButton);
    Vector2 screenPos = tileToScreen(unitPos);
    //TODO-P2 Make dialogue less likely to block relevant map space
    actionDialogue.setPosition(screenPos.x, screenPos.y);
    stage.addActor(actionDialogue);
    state = BattleViewState.CHOOSE_ACTION;
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
    if (state == BattleViewState.MOVING) {
      if (pathVisualizer.drawAnimatedSpritePath(delta, batch)) {
        FieldedUnit unit = playerUnits.get(endLocation);
        unit.state = State.SELECTED;
        createActionDialogue(endLocation);
      }
    }
    batch.end();
    shapeRenderer.begin(ShapeType.Filled);
    if (state == BattleViewState.CHOOSE_MOVE && shownUnitPath != null) {
      shapeRenderer.setColor(Color.BLUE);
      pathVisualizer.drawArrow(shapeRenderer, shownUnitPath);
    }
    if (state == BattleViewState.CHOOSE_ATTACK) {
      // Get back to the correct alpha blend mode
      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,
          GL20.GL_ONE_MINUS_SRC_ALPHA);
      Color attackColor = Color.RED;
      attackColor.a = 0.5f;
      shapeRenderer.setColor(attackColor);
      for (TilePoint point : targetSelection) {
        Rectangle tileRect = getTileWorldRect(point);
        shapeRenderer.rect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
      }
    }
    shapeRenderer.end();
  }

  @SuppressWarnings("DuplicateBranchesInSwitch")
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    TilePoint clickedTile = screenToTile(new Vector2(screenX, screenY));
    switch (state) {
      case IDLE:
        if (playerUnits.containsKey(clickedTile)
            && playerUnits.get(clickedTile).state == State.IDLE) {
          startLocation = clickedTile;
          playerUnits.get(clickedTile).state = State.SELECTED;
          state = BattleViewState.CHOOSE_MOVE;
        }
        break;
      case CHOOSE_MOVE:
        endLocation = clickedTile;
        if (clickedTile.equals(startLocation)) {
          createActionDialogue(clickedTile);
        } else {
          if (shownUnitPath != null && !playerUnits.containsKey(clickedTile) && listGetTail(
              shownUnitPath).equals(clickedTile)) {
            FieldedUnit unit = playerUnits.get(startLocation);
            unit.state = State.MOVING;
            movePlayerUnit(startLocation, endLocation);
            state = BattleViewState.MOVING;
            pathVisualizer.startAnimation(
                unit.stats.type,
                shownUnitPath,
                LoadedResources.getSettings().sprites.movementDurationPerTile,
                LoadedResources.getSettings().sprites.frameDuration);
          } else {
            playerUnits.get(startLocation).state = State.IDLE;
            state = BattleViewState.IDLE;
          }
        }
        shownUnitPath = null;
        selectedUnitPath = null;
        break;
      case MOVING:
        break;
      case CHOOSE_ACTION:
        break;
      case CHOOSE_ATTACK:
        if (targetSelection.contains(clickedTile)) {
          FieldedUnit unit = playerUnits.get(endLocation);
          FieldedUnit enemy = enemyUnits.get(clickedTile);
          unit.fight(enemy);
          unit.state = State.DONE;
          if (unit.currentHealth <= 0) {
            playerUnits.remove(endLocation);
          }
          if (enemy.currentHealth <= 0) {
            enemyUnits.remove(clickedTile);
          }
          state = BattleViewState.IDLE;
        } else {
          createActionDialogue(endLocation);
        }
        break;
      case ENEMY_TURN:
        break;
    }
    return super.touchDown(screenX, screenY, pointer, button);
  }

  @Override
  protected boolean isTilePassable(TilePoint point, Object context) {
    if (point.x < 0 || point.x >= getMapTileSize().x || point.y < 0
        || point.y >= getMapTileSize().y) {
      return false;
    }
    @SuppressWarnings("unchecked") List<TilePoint> blockedTiles = (List<TilePoint>) context;
    return listGet2d(tileProperties, point.x, point.y).passable && !blockedTiles.contains(point);
  }

  // TODO-P3 allow for tiles that require more movement for certain units

  /**
   * Add up the movement distance along a path
   *
   * @param points the list of adjacent points in a path
   * @param unit   the unit that will be following the path
   * @return the total movement required for the unit to follow the path
   */
  @SuppressWarnings("unused")
  private int getDistance(List<TilePoint> points, FieldedUnit unit) {
    return points.size();
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    super.mouseMoved(screenX, screenY);
    if (state == BattleViewState.CHOOSE_MOVE) {
      TilePoint curMouseTile = screenToTile(new Vector2(screenX, screenY));
      // Only recalculate path when mouse has moved onto new tile.
      if (selectedUnitPath != null && curMouseTile.equals(listGetTail(selectedUnitPath))) {
        return false;
      }
      ArrayList<TilePoint> enemyPosList = new ArrayList<>(enemyUnits.keySet());
      if (curMouseTile.equals(startLocation)) {
        selectedUnitPath = null;
        shownUnitPath = null;
      } else if (isTilePassable(curMouseTile, enemyPosList)) {
        selectedUnitPath = getShortestPath(startLocation, curMouseTile, enemyPosList);
        FieldedUnit unit = playerUnits.get(startLocation);
        if (!selectedUnitPath.isEmpty()
            && getDistance(selectedUnitPath, unit) <= unit.stats.movement) {
          shownUnitPath = selectedUnitPath;
        }
      }
    }
    return false;
  }

  @Override
  public void updateScreen(float delta) {
    elapsedTime += delta;

  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  public HashMap<TilePoint, FieldedUnit> getEnemyUnits() {
    return enemyUnits;
  }

  public HashMap<TilePoint, FieldedUnit> getPlayerUnits() {
    return playerUnits;
  }

  enum BattleViewState {
    IDLE,
    CHOOSE_MOVE,
    MOVING,
    CHOOSE_ACTION,
    CHOOSE_ATTACK,
    ENEMY_TURN
  }

  static class TileProperties {

    final static String passableKey = "passable";

    final boolean passable;

    TileProperties(MapProperties mapProperties) {
      this.passable = mapProperties.get(passableKey, false, Boolean.class);
    }
  }

}
