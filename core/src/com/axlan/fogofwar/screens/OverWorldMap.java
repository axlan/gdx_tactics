package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.Campaign;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.PathVisualizer;
import com.axlan.gdxtactics.TilePoint;
import com.axlan.gdxtactics.TiledScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.widget.VisLabel;

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
  private VisLabel cityLabel = null;

  public OverWorldMap(Campaign campaign) {
    super(
        "maps/" + campaign.worldMap.mapName + ".tmx",
        LoadedResources.getReadOnlySettings().tilesPerScreenWidth,
        LoadedResources.getReadOnlySettings().cameraSpeed,
        LoadedResources.getReadOnlySettings().edgeScrollSize);
    pathVisualizer = new PathVisualizer(getTilePixelSize(), LoadedResources.getSpriteLookup());
    loadPathsFromMap();
    loadCitiesFromMap();
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
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(Color.BLUE);
    for (ArrayList<TilePoint> path : shownPaths) {
      pathVisualizer.drawArrow(shapeRenderer, path, true);
    }
    shapeRenderer.end();
  }

  @Override
  protected void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  protected void updateScreen(float delta) {

  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    super.mouseMoved(screenX, screenY);
    Vector2 rawMouseLoc = new Vector2(Gdx.input.getX(), Gdx.input.getY());
    TilePoint curMouseTile = screenToTile(rawMouseLoc);
    if (cities.containsKey(curMouseTile)) {
      if (cityLabel == null) {
        cityLabel = new VisLabel(cities.get(curMouseTile).name);
        cityLabel.setColor(Color.BLACK);
        Vector2 cityScreenLoc = tileToScreen(curMouseTile);
        cityLabel.setPosition(cityScreenLoc.x, cityScreenLoc.y);
        centerLabel(cityLabel);
        this.stage.addActor(cityLabel);
      }
    } else {
      this.stage.getActors().removeValue(cityLabel, true);
      cityLabel = null;
    }

    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    TilePoint curMouseTile = screenToTile(new Vector2(screenX, screenY));
    shownPaths.clear();
    if (cities.containsKey(curMouseTile)) {
      for (ArrayList<TilePoint> path : paths) {
        if (path.contains(curMouseTile)) {
          ArrayList<TilePoint> newPath = new ArrayList<>(path);
          if (listGetTail(newPath).equals(curMouseTile)) {
            Collections.reverse(newPath);
          }
          shownPaths.add(newPath);
        }
      }
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
