package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.City;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.fogofwar.models.ShopItem;
import com.axlan.fogofwar.models.WorldData;
import com.axlan.gdxtactics.PathVisualizer;
import com.axlan.gdxtactics.TilePoint;
import com.axlan.gdxtactics.TiledScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.*;

import static com.axlan.gdxtactics.Utilities.centerLabel;
import static com.axlan.gdxtactics.Utilities.listGetTail;

/**
 * View to select where to move troops for the next round of battles.
 */
public class OverWorldMap extends TiledScreen {

  /**
   * Paths between cities loaded from map file
   */
  private final ArrayList<ArrayList<TilePoint>> paths = new ArrayList<>();
  /** Map of city tile locations to the city data loaded from the map file */
  private final HashMap<TilePoint, City> cities = new HashMap<>();
  /** Paths to draw from the selected city */
  private final ArrayList<ArrayList<TilePoint>> shownPaths = new ArrayList<>();
  /** Utility for drawing map paths */
  private final PathVisualizer pathVisualizer;
  /** Window to show information about the selected city */
  private final CityWindow cityWindow;
  /** Window for selecting troop movements */
  private final MovementsWindow movementsWindow;
  /** Troop movements that have been selected for next round */
  private final ArrayList<Movement> movements = new ArrayList<>();
  /** Labels to show city names along with current controller */
  private final List<VisLabel> cityLabels = new ArrayList<>();
  /** Last selected city */
  private TilePoint lastSelected = null;

  private boolean init = true;

  public OverWorldMap(Runnable completionObserver, MenuBar gameMenuBar) {
    super(
        "maps/" + LoadedResources.getGameStateManager().gameState.campaign.getOverWorldData().mapName + ".tmx",
        LoadedResources.getReadOnlySettings().tilesPerScreenWidth,
        LoadedResources.getReadOnlySettings().cameraSpeed,
        LoadedResources.getReadOnlySettings().edgeScrollSize);
    pathVisualizer = new PathVisualizer(getTilePixelSize(), LoadedResources.getSpriteLookup());
    final VisTable root = new VisTable();
    root.setFillParent(true);
    stage.addActor(root);
    root.add(gameMenuBar.getTable()).expandX().colspan(3).fillX().row();

    VisTextButton deployBtn = new VisTextButton("Deploy");
    deployBtn.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        WorldData data = LoadedResources.getGameStateManager().gameState.campaign.getOverWorldData();
        for (Movement movement : movements) {
          for (WorldData.CityData city : data.cities) {
            if (city.name.equals(movement.to)) {
              city.stationedFriendlyTroops += movement.amount;
            } else if (city.name.equals(movement.from)) {
              city.stationedFriendlyTroops -= movement.amount;
            }
          }
        }
        completionObserver.run();
      }
    });

    root.add().expand().colspan(2).fill();
    root.add(deployBtn).align(Align.topRight);
    root.row();
    loadPathsFromMap();
    loadCitiesFromMap();
    //TODO-P2 lay this out so it doesn't cover the map and doesn't need hardcoded width
    cityWindow = new CityWindow();
    root.add(cityWindow).align(Align.left);
    root.add();
    movementsWindow = new MovementsWindow(movements, () -> selectCity(lastSelected));
    movementsWindow.setPosition(Gdx.graphics.getWidth(), 0);
    root.add(movementsWindow).align(Align.right);
  }

  /** Load city information from map file */
  private void loadCitiesFromMap() {
    MapLayer pathLayer = map.getLayers().get("cities");
    MapObjects pathObjects = pathLayer.getObjects();
    for (MapObject pathObj : pathObjects) {
      MapProperties properties = pathObj.getProperties();
      String name = pathObj.getName();
      Map<String, City.Controller> controllers = LoadedResources.getGameStateManager().gameState.controlledCities;
      City.Controller controller;
      if (controllers.containsKey(name)) {
        controller = controllers.get(name);
      } else {
        controller = City.Controller.valueOf((String) properties.get("controller"));
        controllers.put(name, controller);
      }
      TilePoint location = new TilePoint((float) properties.get("x"), (float) properties.get("y"));
      location = location.divBy(getTilePixelSize());
      cities.put(location, new City(name, location, controller));
    }
  }

  /** Load path information from map file */
  private void loadPathsFromMap() {
    MapLayer pathLayer = map.getLayers().get("paths");
    MapObjects pathObjects = pathLayer.getObjects();
    for (MapObject pathObj : pathObjects) {
      ArrayList<TilePoint> path = new ArrayList<>();
      Polyline poly = ((PolylineMapObject) pathObj).getPolyline();
      float[] vertices = poly.getTransformedVertices();
      for (int i = 0; i < vertices.length; i += 2) {
        TilePoint point = new TilePoint(vertices[i], vertices[i + 1]);
        path.add(point.divBy(getTilePixelSize()));
      }
      paths.add(path);
    }
  }

  @Override
  protected void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  protected void renderAboveForeground(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(Color.BLUE);
    for (ArrayList<TilePoint> path : shownPaths) {
      pathVisualizer.drawArrow(shapeRenderer, path);
    }
    shapeRenderer.end();
  }

  @Override
  protected void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  protected void updateScreen(float delta) {
    //TODO-P3 Fix the hack where I need to set the label positions here since the camera needs to be initialized correctly. Possibly switch from labels to drawing glyphs
    if (init) {
      if (!cityLabels.isEmpty()) {
        for (VisLabel cityLabel : cityLabels) {
          this.stage.getActors().removeValue(cityLabel, true);
        }
        cityLabels.clear();
      }
      for (TilePoint location : cities.keySet()) {
        City cityData = cities.get(location);
        VisLabel cityLabel = new VisLabel(cityData.name);
        // TODO-P2 clean up font. consider using
        // https://github.com/libgdx/libgdx/wiki/Distance-field-fonts
        Label.LabelStyle style = new Label.LabelStyle(cityLabel.getStyle());
        style.font = new BitmapFont(Gdx.files.internal("fonts/ariel_outlined.fnt"));
        switch (cityData.controller) {
          case PLAYER:
            style.fontColor = Color.GREEN;
            break;
          case ENEMY:
            style.fontColor = Color.RED;
            break;
          case NONE:
            style.fontColor = Color.WHITE;
            break;
        }
        cityLabel.setStyle(style);
        cityLabel.setFontScale(0.5f);
        cityLabel.setAlignment(Align.center);
        Vector2 cityScreenLoc = tileToScreen(location);
        cityLabel.setPosition(cityScreenLoc.x, cityScreenLoc.y);
        centerLabel(cityLabel);
        this.stage.addActor(cityLabel);
        this.cityLabels.add(cityLabel);
      }
      init = false;
    }
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    //Recalculate city label positions
    init = true;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    super.mouseMoved(screenX, screenY);
//    Vector2 rawMouseLoc = new Vector2(Gdx.input.getX(), Gdx.input.getY());
//    TilePoint curMouseTile = screenToTile(rawMouseLoc);
//
    return false;
  }

  /**
   * Update the Movement and City window for the city at cityTile
   */
  private void selectCity(TilePoint cityTile) {
    if (cities.containsKey(cityTile)) {
      shownPaths.clear();
      this.lastSelected = cityTile;
      ArrayList<String> neighbors = new ArrayList<>();
      for (ArrayList<TilePoint> path : paths) {
        if (path.contains(cityTile)) {
          ArrayList<TilePoint> newPath = new ArrayList<>(path);
          if (listGetTail(newPath).equals(cityTile)) {
            Collections.reverse(newPath);
          }
          neighbors.add(cities.get(listGetTail(newPath)).name);
          shownPaths.add(newPath);
        }
      }
      String name = cities.get(cityTile).name;
      movementsWindow.updateAddMovementButton(name, neighbors);
      //TODO-P2 update the window after buying an item
      boolean showEnemy = false;
      for (ShopItem item : LoadedResources.getGameStateManager().gameState.playerResources.getPurchases()) {
        for (ShopItem.Intel intel : item.effects) {
          if (intel.revealCityData && (intel.cities == null || intel.cities.contains(name))) {
            showEnemy = true;
            break;
          }
        }
        if (showEnemy) {
          break;
        }
      }
      cityWindow.showCityProperties(name, showEnemy, movements);
    }
  }


  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    TilePoint curMouseTile = screenToTile(new Vector2(screenX, screenY));
    selectCity(curMouseTile);
    return super.touchUp(screenX, screenY, pointer, button);
  }

  /**
   * Class to keep track of troop movements for the next round of battles
   */
  static class Movement {
    /** Identifier of city to move troops to */
    final String to;
    /** Identifier of city to move troops from */
    final String from;
    /** number of troops to move */
    final int amount;

    Movement(String to, String from, int amount) {
      this.to = to;
      this.from = from;
      this.amount = amount;
    }
  }
}
