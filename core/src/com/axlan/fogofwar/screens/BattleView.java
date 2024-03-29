package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.logic.EnemyAi;
import com.axlan.fogofwar.logic.EnemyAi.EnemyAction;
import com.axlan.fogofwar.logic.EnemyAi.EnemyMoveAction;
import com.axlan.fogofwar.logic.UnitRangeCalc;
import com.axlan.fogofwar.models.*;
import com.axlan.fogofwar.models.FieldedUnit.State;
import com.axlan.fogofwar.models.LevelData.AlternativeWinConditions;
import com.axlan.gdxtactics.*;
import com.axlan.gdxtactics.SpriteLookup.Poses;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.axlan.gdxtactics.Utilities.getTransparentColor;
import static com.axlan.gdxtactics.Utilities.listGetTail;

// TODO-P1 Make touch controls more intuitive
// TODO-P2 Add overlay when unit is selected attack range
// TODO-P2 Add retreat/reinforcement mechanism
// TODO-P3 Add end turn button / Give option to end turn when no active units left.
// TODO-P3 Add support for more then one enemy or ally AI/Commander
// TODO-P3 Add touch screen support
// TODO-P3 Add battle animations
// TODO-P3 Disable saving during enemy turn / end screen

/**
 * A screen to play out the turn based battle. Player commands their troops against enemy AI.
 */
public class BattleView extends TiledScreen {

  /**
   * Class for accessing map data for pathing
   */
  private final BattleMap battleMap;

  private final LevelData levelData;
  /**
   * Used to draw potential paths and movement animations on the map
   */
  private final PathVisualizer pathVisualizer;
  /**
   * Window to display properties of selected objects on map
   */
  private final PropertyWindow propertyWindow;
  /**
   * Current state for state machine that controls UI
   */
  private BattleViewState state = BattleViewState.ENEMY_IDLE;
  /**
   * Key to {@link com.axlan.fogofwar.models.GameStateManager#gameState#battleState#playerUnits} for
   * where a unit was originally selected
   */
  private TilePoint startLocation = null;
  /**
   * Path from {@link #startLocation} to the mouse location
   */
  private List<TilePoint> selectedUnitPath = null;
  /**
   * Path from {@link #startLocation} to the last valid mouse position
   */
  private List<TilePoint> shownUnitPath = null;
  /**
   * List of tiles that selected unit can move to
   */
  private List<TilePoint> reachableTiles = null;
  /** Keeps track of time for selecting frames for animations */
  private float elapsedTime = 0;
  /**
   * AI for controlling enemy turn
   */
  private EnemyAi enemyAi;
  /**
   * Targets for an attack
   */
  private List<TilePoint> targetSelection = null;
  /**
   * Key to {@link com.axlan.fogofwar.models.GameStateManager#gameState#battleState#playerUnits} for
   * where the unit moves to
   */
  private TilePoint endLocation = null;

  /**
   * Tiles that are visible to player
   */
  private Set<TilePoint> visibleTiles;

  /**
   * Callback for when the screen completes
   */
  private final Runnable completionObserver;

  /**
   * Tiles to highlight to show potential enemy movement
   */
  private List<TilePoint> enemyMovementRange = null;
  /**
   * Tiles to highlight to show potential enemy attack
   */
  private List<TilePoint> enemyAttackRange = null;
  /**
   * Tile mouse is currently over. Used to invalidate cached processing
   */
  private TilePoint mouseOverTile = null;
  /**
   * Enemy move in progress
   */
  private EnemyMoveAction activeEnemyMove;

  public BattleView(Runnable completionObserver) {
    super(
        "maps/" + LoadedResources.getGameStateManager().gameState.campaign.getLevelData().mapName + ".tmx",
        LoadedResources.getReadOnlySettings().tilesPerScreenWidth,
        LoadedResources.getReadOnlySettings().cameraSpeed,
        LoadedResources.getReadOnlySettings().edgeScrollSize);
    this.completionObserver = completionObserver;
    levelData = LoadedResources.getGameStateManager().gameState.campaign.getLevelData();
    pathVisualizer = new PathVisualizer(getTilePixelSize(), LoadedResources.getSpriteLookup());
    battleMap = new BattleMap(map);

    propertyWindow =
        new PropertyWindow(LoadedResources.getGameStateManager().gameState.battleState, battleMap);
    propertyWindow.setPosition(0, 0);

    final VisTable root = new VisTable();
    root.setFillParent(true);
    stage.addActor(root);

    MenuBar gameMenuBar = new MenuBar();
    gameMenuBar.addMenu(LoadedResources.getOptionsMenu());
    root.add(gameMenuBar.getTable()).expandX().fillX().row();
    root.add().expand().fill();

    stage.addActor(propertyWindow);

    //TODO-P2 fix levelData.doesPlayerGoFirst, this breaks the load logic since turn isn't saved
    //changeTurn(levelData.doesPlayerGoFirst);
    changeTurn(true);
    visibleTiles = UnitRangeCalc.getVisibleTiles(LoadedResources.getGameStateManager().gameState.battleState.playerUnits);
  }

  /**
   * Switch to a fresh state for either the player or enemy turn
   *
   * @param isPlayerTurn if true, start player turn. Otherwise start enemy.
   */
  private void changeTurn(boolean isPlayerTurn) {
    Collection<FieldedUnit> unitList;
    if (isPlayerTurn) {
      state = BattleViewState.IDLE;
      unitList = LoadedResources.getGameStateManager().gameState.battleState.playerUnits.values();
    } else {
      state = BattleViewState.ENEMY_IDLE;
      unitList = LoadedResources.getGameStateManager().gameState.battleState.enemyUnits.values();
      enemyAi =
          new EnemyAi(
              levelData, LoadedResources.getGameStateManager().gameState.battleState, battleMap);
    }
    for (FieldedUnit unit : unitList) {
      unit.state = State.IDLE;
    }
  }

  /** Check if all player units are done and start enemy turn if they have. */
  private void checkTurnDone() {
    boolean done = true;
    for (FieldedUnit unit :
        LoadedResources.getGameStateManager().gameState.battleState.playerUnits.values()) {
      done &= unit.state == State.DONE;
    }
    if (done) {
      checkVictory();
      changeTurn(false);
    } else {
      state = BattleViewState.IDLE;
    }
    visibleTiles = UnitRangeCalc.getVisibleTiles(LoadedResources.getGameStateManager().gameState.battleState.playerUnits);
  }

  private void movePropertyWindow() {
    //TODO-P2 Make the portion of the screen that triggers moves a setting
    if (Gdx.input.getX() < Gdx.graphics.getWidth() / 3) {
      propertyWindow.setPosition(Gdx.graphics.getWidth(), 0);
    } else if (Gdx.input.getX() > Gdx.graphics.getWidth() * 2 / 3) {
      propertyWindow.setPosition(0, 0);
    }
  }

  /**
   * Check the victory state for sets of units and conditions
   *
   * @param units         the friendly units for the player to check
   * @param opponentUnits the enemy units for the player to check
   * @param conditions    the alternative victory conditions for the player to check
   * @return true if the player of interest meets a victory condition
   */
  private boolean checkVictory(
      HashMap<TilePoint, FieldedUnit> units,
      HashMap<TilePoint, FieldedUnit> opponentUnits,
      AlternativeWinConditions conditions) {
    boolean hasWon = false;
    if (opponentUnits.size() == 0) {
      return true;
    }
    if (conditions != null) {
      if (conditions.moveToPoint != null) {
        hasWon = units.containsKey(conditions.moveToPoint);
      }
      if (conditions.opponentAtPoint != null) {
        hasWon |= opponentUnits.containsKey(conditions.moveToPoint);
      }
    }
    return hasWon;
  }

  /**
   * Check if either player meets their victory conditions. Finalize the results and end the screen if done.
   */
  private void checkVictory() {
    GameState gameState = LoadedResources.getGameStateManager().gameState;
    String endingText = null;
    Color endingColor = null;
    if (checkVictory(
        gameState.battleState.enemyUnits,
        gameState.battleState.playerUnits,
        levelData.enemyWinConditions)) {
      gameState.controlledCities.put(gameState.contestedCity, City.Controller.ENEMY);
      gameState.battleState.playerUnits.clear();
      endingText = "DEFEAT";
      endingColor = Color.RED;
    } else if (checkVictory(
        gameState.battleState.playerUnits,
        gameState.battleState.enemyUnits,
        levelData.playerWinConditions)) {
      gameState.controlledCities.put(gameState.contestedCity, City.Controller.PLAYER);
      gameState.battleState.enemyUnits.clear();
      endingText = "VICTORY";
      endingColor = Color.GREEN;
    }
    if (endingText != null) {
      Optional<WorldData.CityData> optionalCityData = gameState.campaign.getOverWorldData().getCity(gameState.contestedCity);
      if (!optionalCityData.isPresent()) {
        throw new RuntimeException("Invalid city name");
      }
      WorldData.CityData cityData = optionalCityData.get();

      //TODO-P2 allow units to have a point value (some units are worth more points then others)
      cityData.stationedEnemyTroops = gameState.battleState.enemyUnits.size();
      cityData.stationedFriendlyTroops = gameState.battleState.playerUnits.size();

      Label.LabelStyle titleStyle = new Label.LabelStyle(VisUI.getSkin().getFont("BlackOpsOne-Regular"), endingColor);
      VisLabel victoryLabel = new VisLabel(endingText, titleStyle);
      //noinspection IntegerDivisionInFloatingPointContext
      victoryLabel.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
      Utilities.centerLabel(victoryLabel);
      this.stage.addActor(victoryLabel);
      this.stage.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
          super.clicked(event, x, y);
          completionObserver.run();
        }
      });

    }

  }

  /**
   * Create a dialogue window to select the action the unit should take
   *
   * @param unitPos the position of the acting unit
   */
  private void createActionDialogue(final TilePoint unitPos) {
    final FieldedUnit unit =
        LoadedResources.getGameStateManager().gameState.battleState.playerUnits.get(unitPos);
    final VisDialog actionDialogue = new VisDialog("Choose Action");
    final ArrayList<TilePoint> targets = new ArrayList<>();
    for (TilePoint enemyPos :
        LoadedResources.getGameStateManager().gameState.battleState.enemyUnits.keySet()) {
      int distance = unitPos.absDiff(enemyPos);
      if (distance >= unit.getStats().minAttackRange
          && distance <= unit.getStats().minAttackRange &&
          visibleTiles.contains(enemyPos)) {
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
    waitButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            unit.state = State.DONE;
            checkTurnDone();
          }
        });
    actionDialogue.button(waitButton);
    actionDialogue.getButtonsTable().row();
    VisTextButton cancelButton = new VisTextButton("Cancel");
    cancelButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            unit.state = State.IDLE;
            movePlayerUnit(endLocation, startLocation);
            state = BattleViewState.IDLE;
          }
        });
    actionDialogue.button(cancelButton);
    Vector2 screenPos = tileToScreen(unitPos);
    // TODO-P2 Make dialogue less likely to block relevant map space
    actionDialogue.setPosition(screenPos.x, screenPos.y);
    actionDialogue.pack();
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
    final FieldedUnit unit =
        LoadedResources.getGameStateManager().gameState.battleState.playerUnits.get(oldPos);
    LoadedResources.getGameStateManager().gameState.battleState.playerUnits.remove(oldPos);
    LoadedResources.getGameStateManager().gameState.battleState.playerUnits.put(newPos, unit);
  }

  /**
   * Move the enemy controlled unit in enemyUnits to a new position key
   *
   * @param oldPos old unit position
   * @param newPos new unit position
   */
  private void moveEnemyUnit(TilePoint oldPos, TilePoint newPos) {
    final FieldedUnit unit =
        LoadedResources.getGameStateManager().gameState.battleState.enemyUnits.get(oldPos);
    LoadedResources.getGameStateManager().gameState.battleState.enemyUnits.remove(oldPos);
    LoadedResources.getGameStateManager().gameState.battleState.enemyUnits.put(newPos, unit);
  }

  @Override
  public void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
    batch.begin();
    for (TilePoint point :
        LoadedResources.getGameStateManager().gameState.battleState.playerUnits.keySet()) {
      FieldedUnit unit =
          LoadedResources.getGameStateManager().gameState.battleState.playerUnits.get(point);
      AnimatedSprite<AtlasRegion> sprite = null;
      if (unit.state == State.SELECTED) {
        sprite = LoadedResources.getAnimation(unit.getStats().type, Poses.LEFT);
      } else if (unit.state == State.IDLE || unit.state == State.DONE) {
        sprite = LoadedResources.getAnimation(unit.getStats().type, Poses.IDLE);
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
    for (TilePoint point :
        LoadedResources.getGameStateManager().gameState.battleState.enemyUnits.keySet()) {
      if (visibleTiles.contains(point)) {
        FieldedUnit unit =
            LoadedResources.getGameStateManager().gameState.battleState.enemyUnits.get(point);
        AnimatedSprite<AtlasRegion> sprite = null;
        if (unit.state == State.IDLE || unit.state == State.DONE) {
          sprite = LoadedResources.getAnimation(unit.getStats().type, Poses.IDLE);
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
    }
    if (state == BattleViewState.MOVING) {
      if (pathVisualizer.drawAnimatedSpritePath(delta, batch)) {
        FieldedUnit unit =
            LoadedResources.getGameStateManager()
                .gameState
                .battleState
                .playerUnits
                .get(endLocation);
        unit.state = State.SELECTED;
        createActionDialogue(endLocation);
      }
    } else if (state == BattleViewState.ENEMY_MOVING) {
      if (pathVisualizer.drawAnimatedSpritePath(delta, batch, visibleTiles)) {
        FieldedUnit unit =
            LoadedResources.getGameStateManager().gameState.battleState.enemyUnits.get(endLocation);
        unit.state = State.DONE;
        state = BattleViewState.ENEMY_IDLE;
      }
    }
    batch.end();
    shapeRenderer.begin(ShapeType.Filled);
    // Get back to the correct alpha blend mode
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    TilePoint mouseTile = screenToTile(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    if (enemyAttackRange != null) {
      Color attackColor = getTransparentColor(Color.RED, 0.5f);
      shapeRenderer.setColor(attackColor);
      for (TilePoint point : enemyAttackRange) {
        Rectangle tileRect = getTileWorldRect(point);
        shapeRenderer.rect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
      }
    }
    if (enemyMovementRange != null) {
      Color moveColor = getTransparentColor(Color.BLUE, 0.5f);
      shapeRenderer.setColor(moveColor);
      for (TilePoint point : enemyMovementRange) {
        Rectangle tileRect = getTileWorldRect(point);
        shapeRenderer.rect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
      }
    }
    if (state == BattleViewState.CHOOSE_MOVE) {
      Color moveColor = getTransparentColor(Color.BLUE, 0.5f);
      shapeRenderer.setColor(moveColor);
      for (TilePoint tile : reachableTiles) {
        Rectangle tileRect = getTileWorldRect(tile);
        shapeRenderer.rect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
      }
      if (shownUnitPath != null) {
        shapeRenderer.setColor(Color.BLUE);
        pathVisualizer.drawArrow(shapeRenderer, shownUnitPath);
      }
    }
    if (state == BattleViewState.CHOOSE_ATTACK) {
      Color attackColor = getTransparentColor(Color.RED, 0.5f);
      shapeRenderer.setColor(attackColor);
      for (TilePoint point : targetSelection) {
        Rectangle tileRect = getTileWorldRect(point);
        shapeRenderer.rect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
      }
    }
    shapeRenderer.end();
  }

  @Override
  protected void renderAboveForeground(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
    Color fogColor = getTransparentColor(Color.BLACK, 0.5f);
    shapeRenderer.begin(ShapeType.Filled);
    // Get back to the correct alpha blend mode
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    shapeRenderer.setColor(fogColor);
    List<TilePoint> hiddenTiles = getVisibleTiles().stream().filter((t) -> !visibleTiles.contains(t)).collect(Collectors.toList());
    for (TilePoint point : hiddenTiles) {
      Rectangle tileRect = getTileWorldRect(point);
      shapeRenderer.rect(tileRect.x, tileRect.y, tileRect.width, tileRect.height);
    }
    shapeRenderer.end();
  }


  @SuppressWarnings("DuplicateBranchesInSwitch")
  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    mouseMoved(screenX, screenY);
    TilePoint clickedTile = screenToTile(new Vector2(screenX, screenY));
    switch (state) {
      case IDLE:
        if (LoadedResources.getGameStateManager()
            .gameState
            .battleState
            .playerUnits
            .containsKey(clickedTile)) {
          FieldedUnit unit =
              LoadedResources.getGameStateManager()
                  .gameState
                  .battleState
                  .playerUnits
                  .get(clickedTile);
          if (unit.state == State.IDLE) {
            startLocation = clickedTile;
            unit.state = State.SELECTED;
            state = BattleViewState.CHOOSE_MOVE;
            reachableTiles =
                battleMap.getPointsWithinRange(
                    clickedTile,
                    unit.getStats().movement,
                    LoadedResources.getGameStateManager().gameState.battleState.enemyUnits);
          }
        }
        break;
      case CHOOSE_MOVE:
        endLocation = clickedTile;
        if (clickedTile.equals(startLocation)) {
          createActionDialogue(clickedTile);
        } else {
          if (shownUnitPath != null
              && !LoadedResources.getGameStateManager()
              .gameState
              .battleState
              .playerUnits
              .containsKey(clickedTile)
              && listGetTail(shownUnitPath).equals(clickedTile)) {
            FieldedUnit unit =
                LoadedResources.getGameStateManager()
                    .gameState
                    .battleState
                    .playerUnits
                    .get(startLocation);
            unit.state = State.MOVING;
            movePlayerUnit(startLocation, endLocation);
            state = BattleViewState.MOVING;
            pathVisualizer.startAnimation(
                unit.getStats().type,
                shownUnitPath,
                LoadedResources.getReadOnlySettings().sprites.movementDurationPerTile,
                LoadedResources.getReadOnlySettings().sprites.frameDuration);
          } else {
            LoadedResources.getGameStateManager()
                .gameState
                .battleState
                .playerUnits
                .get(startLocation)
                .state =
                State.IDLE;
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
          FieldedUnit unit =
              LoadedResources.getGameStateManager()
                  .gameState
                  .battleState
                  .playerUnits
                  .get(endLocation);
          FieldedUnit enemy =
              LoadedResources.getGameStateManager()
                  .gameState
                  .battleState
                  .enemyUnits
                  .get(clickedTile);
          unit.fight(enemy);
          unit.state = State.DONE;
          if (unit.currentHealth <= 0) {
            LoadedResources.getGameStateManager()
                .gameState
                .battleState
                .playerUnits
                .remove(endLocation);
          }
          if (enemy.currentHealth <= 0) {
            LoadedResources.getGameStateManager()
                .gameState
                .battleState
                .enemyUnits
                .remove(clickedTile);
          }
          checkTurnDone();
        } else {
          createActionDialogue(endLocation);
        }
        break;
      case ENEMY_IDLE:
        break;
      case ENEMY_MOVING:
        break;
    }
    return super.touchDown(screenX, screenY, pointer, button);
  }

  // TODO-P3 allow for tiles that require more movement for certain units

  /**
   * Add up the movement distance along a path
   *
   * @param points the list of adjacent points in a path
   * @param unit the unit that will be following the path
   * @return the total movement required for the unit to follow the path
   */
  @SuppressWarnings("unused")
  private int getDistance(List<TilePoint> points, FieldedUnit unit) {
    return points.size() - 1;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    super.touchDragged(screenX, screenY, pointer);
    return mouseMoved(screenX, screenY);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    super.mouseMoved(screenX, screenY);
    TilePoint curMouseTile = screenToTile(new Vector2(screenX, screenY));
    if (curMouseTile.equals(mouseOverTile)) {
      return false;
    }
    mouseOverTile = curMouseTile;

    if (state == BattleViewState.CHOOSE_MOVE) {
      if (curMouseTile.equals(startLocation)) {
        selectedUnitPath = null;
        shownUnitPath = null;
      } else if (battleMap.isTilePassable(
          curMouseTile, LoadedResources.getGameStateManager().gameState.battleState.enemyUnits)) {
        selectedUnitPath =
            battleMap.getShortestPath(
                startLocation,
                curMouseTile,
                LoadedResources.getGameStateManager().gameState.battleState.enemyUnits);
        FieldedUnit unit =
            LoadedResources.getGameStateManager()
                .gameState
                .battleState
                .playerUnits
                .get(startLocation);
        if (!selectedUnitPath.isEmpty()
            && getDistance(selectedUnitPath, unit) <= unit.getStats().movement) {
          shownUnitPath = selectedUnitPath;
        }
      }
    }
    propertyWindow.showTileProperties(curMouseTile, visibleTiles.contains(curMouseTile));
    movePropertyWindow();
    if (LoadedResources.getGameStateManager()
        .gameState
        .battleState
        .enemyUnits
        .containsKey(curMouseTile) &&
        visibleTiles.contains(curMouseTile)) {
      FieldedUnit unit = LoadedResources.getGameStateManager()
          .gameState
          .battleState
          .enemyUnits
          .get(curMouseTile);
      enemyMovementRange =
          battleMap.getPointsWithinRange(
              curMouseTile,
              unit.getStats().movement,
              LoadedResources.getGameStateManager().gameState.battleState.playerUnits);
      enemyAttackRange = new ArrayList<>(UnitRangeCalc.getAttackTiles(enemyMovementRange, unit));
      enemyMovementRange.remove(curMouseTile);
      enemyAttackRange.remove(curMouseTile);
    } else {
      enemyMovementRange = null;
      enemyAttackRange = null;
    }
    return false;
  }

  @Override
  public void updateScreen(float delta) {
    elapsedTime += delta;
    if (state == BattleViewState.ENEMY_IDLE) {
      if (activeEnemyMove != null) {
        for (TilePoint attack : activeEnemyMove.attacks) {
          endLocation = listGetTail(activeEnemyMove.path);
          FieldedUnit enemyUnit = LoadedResources.getGameStateManager()
              .gameState
              .battleState
              .enemyUnits.get(endLocation);
          FieldedUnit playerUnit = LoadedResources.getGameStateManager()
              .gameState
              .battleState
              .playerUnits.get(attack);
          //TODO-P2 consolidate this redundant code along with other fight
          enemyUnit.fight(playerUnit);
          if (enemyUnit.currentHealth <= 0) {
            LoadedResources.getGameStateManager()
                .gameState
                .battleState
                .enemyUnits
                .remove(endLocation);
          }
          if (playerUnit.currentHealth <= 0) {
            LoadedResources.getGameStateManager()
                .gameState
                .battleState
                .playerUnits
                .remove(attack);
            visibleTiles = UnitRangeCalc.getVisibleTiles(LoadedResources.getGameStateManager().gameState.battleState.playerUnits);
          }
        }
      }
      EnemyAction action = enemyAi.getNextAction();
      if (action instanceof EnemyMoveAction) {
        activeEnemyMove = (EnemyMoveAction) action;
        startLocation = activeEnemyMove.path.get(0);
        FieldedUnit unit =
            LoadedResources.getGameStateManager()
                .gameState
                .battleState
                .enemyUnits
                .get(startLocation);
        if (activeEnemyMove.path.size() > 1) {
          state = BattleViewState.ENEMY_MOVING;
          endLocation = listGetTail(activeEnemyMove.path);
          unit.state = State.MOVING;
          moveEnemyUnit(startLocation, endLocation);
          pathVisualizer.startAnimation(
              unit.getStats().type,
              activeEnemyMove.path,
              LoadedResources.getReadOnlySettings().sprites.movementDurationPerTile,
              LoadedResources.getReadOnlySettings().sprites.frameDuration);
        } else {
          unit.state = State.DONE;
        }
      } else {
        activeEnemyMove = null;
        changeTurn(true);
      }
    }
    if (state == BattleViewState.IDLE) {
      checkVictory();
    }
  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {}

  enum BattleViewState {
    IDLE,
    CHOOSE_MOVE,
    MOVING,
    CHOOSE_ACTION,
    CHOOSE_ATTACK,
    ENEMY_IDLE,
    ENEMY_MOVING
  }
}
