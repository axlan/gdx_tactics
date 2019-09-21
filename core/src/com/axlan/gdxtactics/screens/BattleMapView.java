package com.axlan.gdxtactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTextButton;

/**
 * Created by David on 16/10/2016.
 */
public class BattleMapView extends TiledScreen {

    private final Sprite img;
    private GridPoint2 playerPos = new GridPoint2(0, 10);

    public BattleMapView() {
        super("maps/advanced1.tmx");
        TextureAtlas tankAtlas = new TextureAtlas("images/units/tank.atlas");
        //img = tankAtlas.createSprite("tank00").getTexture();
        img = tankAtlas.createSprite("tank00");
        img.scale(0.5f / getCameraZoom());

        VisTextButton test = new VisTextButton("Test");
        test.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    playerPos = new GridPoint2(0, 0);
                }
            });
        stage.addActor(test);
    }

    @Override
    public void renderScreen(float delta, SpriteBatch batch) {

        Vector2 screenpos = tileToScreen(playerPos);
        img.setPosition(screenpos.x + tileSize.x / 2, screenpos.y + tileSize.y / 2);
        batch.begin();
        img.draw(batch);
        batch.end();
        // this.camera.position.x = pos_row;
        // this.camera.position.y = pos_col;
    }

    @Override
    public void updateScreen(float delta) {
        moveCameraToLeft = (Gdx.input.isKeyPressed(Input.Keys.LEFT));
        moveCameraToRight = (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        moveCameraToBottom = (Gdx.input.isKeyPressed(Input.Keys.DOWN));
        moveCameraToTop = (Gdx.input.isKeyPressed(Input.Keys.UP));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        playerPos = screenToTile(new Vector2(screenX, screenY));
        System.out.println(playerPos);
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
