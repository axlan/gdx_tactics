package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.models.DeploymentSelection;
import com.axlan.gdxtactics.models.LevelData;
import com.axlan.gdxtactics.models.PlayerResources;
import com.axlan.gdxtactics.models.UnitStats;
import com.axlan.gdxtactics.screens.FieldedUnit.State;
import com.axlan.gdxtactics.screens.SpriteLookup.Poses;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import java.util.HashMap;

public class BattleView extends TiledScreen {

  HashMap<String, UnitStats> unitStats;
  HashMap<GridPoint2, FieldedUnit> playerUnits = new HashMap<>();
  GridPoint2 selected = null;
  private float elapsedTime = 0;

  public BattleView(LevelData levelData, HashMap<String, UnitStats> unitStats,
      PlayerResources playerResources, DeploymentSelection deploymentSelection) {
    super("maps/" + levelData.mapName + ".tmx");
    this.unitStats = unitStats;

    for (GridPoint2 point : deploymentSelection.playerUnitPlacements.keySet()) {
      String unitType = deploymentSelection.playerUnitPlacements.get(point);
      playerUnits.put(point, new FieldedUnit(unitStats.get(unitType)));
    }

  }

  @Override
  public void renderScreen(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {
    batch.begin();
    for (GridPoint2 point : playerUnits.keySet()) {
      FieldedUnit unit = playerUnits.get(point);
      AnimatedSprite<AtlasRegion> sprite;
      if (unit.state == State.MOVING) {
        sprite = SpriteLookup.getAnimation(unit.stats.getType(),
            Poses.LEFT, 0.1f);
      } else {
        sprite = SpriteLookup.getAnimation(unit.stats.getType(),
            Poses.IDLE, 0.1f);
      }
      Vector2 worldPos = tileToWorld(point);
      sprite.setPosition(worldPos.x, worldPos.y);
      if (unit.state == State.DONE) {
        sprite.setColor(Color.GRAY);
      }
      sprite.draw(batch, elapsedTime);
    }
    batch.end();
    if (selected != null) {
      GridPoint2 playerPos = screenToTile(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
      if (!playerPos.equals(selected)) {

      }
    }
  }

  @Override
  public void renderAboveUI(float delta, SpriteBatch batch, ShapeRenderer shapeRenderer) {

  }

  @Override
  public void updateScreen(float delta) {
    elapsedTime += delta;
  }


  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    GridPoint2 playerPos = screenToTile(new Vector2(screenX, screenY));

    if (selected != null) {
      playerUnits.get(selected).state = State.IDLE;
      selected = null;
    }
    if (playerUnits.containsKey(playerPos)) {
      selected = playerPos;
      playerUnits.get(playerPos).state = State.MOVING;
    }

    return super.touchDown(screenX, screenY, pointer, button);
  }
}
