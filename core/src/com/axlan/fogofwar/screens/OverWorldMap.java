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
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Polyline;

import java.util.ArrayList;
import java.util.List;

public class OverWorldMap extends TiledScreen {

  private final List<List<TilePoint>> paths = new ArrayList<>();

  public OverWorldMap(Campaign campaign) {
    super(
        "maps/" + campaign.worldMap.mapName + ".tmx",
        LoadedResources.getReadOnlySettings().tilesPerScreenWidth,
        LoadedResources.getReadOnlySettings().cameraSpeed,
        LoadedResources.getReadOnlySettings().edgeScrollSize);
    MapLayer pathLayer = map.getLayers().get("paths");
    MapObjects pathObjects = pathLayer.getObjects();
    for (MapObject pathObj : pathObjects) {
      List<TilePoint> path = new ArrayList<>();
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
  protected void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  protected void updateScreen(float delta) {

  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return super.touchUp(screenX, screenY, pointer, button);

  }
}
