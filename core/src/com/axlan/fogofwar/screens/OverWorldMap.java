package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.LoadedResources;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.axlan.gdxtactics.Utilities.centerLabel;
import static com.axlan.gdxtactics.Utilities.listGetTail;

public class OverWorldMap extends TiledScreen {

  private final ArrayList<ArrayList<TilePoint>> paths = new ArrayList<>();
  private final HashMap<TilePoint, City> cities = new HashMap<>();
  private final ArrayList<ArrayList<TilePoint>> shownPaths = new ArrayList<>();
  private final PathVisualizer pathVisualizer;
  @SuppressWarnings("FieldCanBeLocal")
  private final Runnable completionObserver;
  private final CityWindow cityWindow;

  private boolean init = true;

  public OverWorldMap(Runnable completionObserver, MenuBar gameMenuBar) {
    super(
        "maps/" + LoadedResources.getGameStateManager().gameState.campaign.getOverWorldData().mapName + ".tmx",
        LoadedResources.getReadOnlySettings().tilesPerScreenWidth,
        LoadedResources.getReadOnlySettings().cameraSpeed,
        LoadedResources.getReadOnlySettings().edgeScrollSize);
    this.completionObserver = completionObserver;
    pathVisualizer = new PathVisualizer(getTilePixelSize(), LoadedResources.getSpriteLookup());
    final VisTable root = new VisTable();
    root.setFillParent(true);
    stage.addActor(root);
    root.add(gameMenuBar.getTable()).expandX().fillX().row();
    root.add().expand().fill();
    loadPathsFromMap();
    loadCitiesFromMap();
    //TODO-P2 lay this out so it doesn't cover the map
    cityWindow = new CityWindow();
    cityWindow.setPosition(0, 0);
    stage.addActor(cityWindow);
  }

  private void loadCitiesFromMap() {
    MapLayer pathLayer = map.getLayers().get("cities");
    MapObjects pathObjects = pathLayer.getObjects();
    for (MapObject pathObj : pathObjects) {
      MapProperties properties = pathObj.getProperties();
      City.Controller controller = City.Controller.valueOf((String) properties.get("controller"));
      String name = pathObj.getName();
      TilePoint location = new TilePoint((float) properties.get("x"), (float) properties.get("y"));
      location = location.divBy(getTilePixelSize());
      cities.put(location, new City(name, location, controller));
    }
  }

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
      for (TilePoint location : cities.keySet()) {
        VisLabel cityLabel = new VisLabel(cities.get(location).name);
        // TODO-P2 clean up font. consider using
        // https://github.com/libgdx/libgdx/wiki/Distance-field-fonts
        Label.LabelStyle style = new Label.LabelStyle(cityLabel.getStyle());
        style.font = new BitmapFont(Gdx.files.internal("fonts/ariel_outlined.fnt"));
        cityLabel.setStyle(style);
        cityLabel.setFontScale(0.5f);
        cityLabel.setAlignment(Align.center);
        Vector2 cityScreenLoc = tileToScreen(location);
        cityLabel.setPosition(cityScreenLoc.x, cityScreenLoc.y);
        centerLabel(cityLabel);
        this.stage.addActor(cityLabel);
      }
      init = false;
    }
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    super.mouseMoved(screenX, screenY);
//    Vector2 rawMouseLoc = new Vector2(Gdx.input.getX(), Gdx.input.getY());
//    TilePoint curMouseTile = screenToTile(rawMouseLoc);
//
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    TilePoint curMouseTile = screenToTile(new Vector2(screenX, screenY));
    shownPaths.clear();
    if (cities.containsKey(curMouseTile)) {
      ArrayList<String> neighbors = new ArrayList<>();
      for (ArrayList<TilePoint> path : paths) {
        if (path.contains(curMouseTile)) {
          ArrayList<TilePoint> newPath = new ArrayList<>(path);
          if (listGetTail(newPath).equals(curMouseTile)) {
            Collections.reverse(newPath);
          }
          neighbors.add(cities.get(listGetTail(newPath)).name);
          shownPaths.add(newPath);
        }
      }
      cityWindow.showCityProperties(cities.get(curMouseTile).name, neighbors);
    }
    return super.touchUp(screenX, screenY, pointer, button);
  }

  private static class City {
    final String name;
    final TilePoint location;
    Controller controller;

    City(String name, TilePoint location, Controller controller) {
      this.name = name;
      this.location = location;
      this.controller = controller;
    }

    enum Controller {
      PLAYER,
      ENEMY,
      NONE
    }
  }
}
