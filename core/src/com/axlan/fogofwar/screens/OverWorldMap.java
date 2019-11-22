package com.axlan.fogofwar.screens;

import com.axlan.fogofwar.models.Campaign;
import com.axlan.fogofwar.models.LoadedResources;
import com.axlan.gdxtactics.TilePoint;
import com.axlan.gdxtactics.TiledScreen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Polyline;

import java.util.ArrayList;
import java.util.HashMap;

public class OverWorldMap extends TiledScreen {

  private final ArrayList<ArrayList<TilePoint>> paths = new ArrayList<>();
  private final HashMap<TilePoint, City> cities = new HashMap<>();
  public OverWorldMap(Campaign campaign) {
    super(
        "maps/" + campaign.worldMap.mapName + ".tmx",
        LoadedResources.getReadOnlySettings().tilesPerScreenWidth,
        LoadedResources.getReadOnlySettings().cameraSpeed,
        LoadedResources.getReadOnlySettings().edgeScrollSize);
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
  public boolean mouseMoved(int screenX, int screenY) {
    return super.mouseMoved(screenX, screenY);
  }


  @Override
  protected void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  protected void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  protected void updateScreen(float delta) {

  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
