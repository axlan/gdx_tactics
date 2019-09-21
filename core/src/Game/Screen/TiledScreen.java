package Game.Screen;

import com.axlan.gdxtactics.BattleMap;
import com.axlan.gdxtactics.BattleMapDemo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;


/**
 * Created by David on 17/11/2016.
 * Abscract com.axlan.gdxtactics.Game.Screen class for tiled screens. Render entities between layers
 */
public abstract class TiledScreen implements Screen, InputProcessor {

    protected SpriteBatch batch;
    protected TiledMap map;
    protected OrthogonalTiledMapRenderer renderer;
    protected OrthographicCamera camera;

    protected ArrayList<TiledMapTileLayer> layers = new ArrayList<TiledMapTileLayer>();

    protected Vector2 screenSize;
    protected Vector2 worldSize;
    protected Vector2 tileSize;

    protected float[] cameraBounds = new float[4];
    protected Vector3 cameraPosition;
    protected boolean moveCameraToLeft = false;
    protected boolean moveCameraToTop = false;
    protected boolean moveCameraToRight = false;
    protected boolean moveCameraToBottom = false;
    protected float cameraSpeed = 300;
    protected float cameraZoom = .25f;

    /* Methods */

    public TiledScreen(String levelTmxFilename) {
        this.batch = new SpriteBatch();
        map = new TmxMapLoader().load(levelTmxFilename);

        /* Layers from the map : 0 - under entities / 1+ - above entities */
        for (int i=0;i<map.getLayers().getCount();i++) {
            layers.add( (TiledMapTileLayer) map.getLayers().get(i) );
        }

        worldSize = new Vector2(layers.get(0).getWidth(), layers.get(0).getHeight() );
        screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        tileSize = new Vector2(layers.get(0).getTileWidth(), layers.get(0).getTileWidth() );

        worldSize = worldSize.scl(tileSize);

        //Boundaries for the camera : left, top, right, bottom
        float horizontalMargin = (screenSize.x-worldSize.x>0) ? (screenSize.x-worldSize.x)/2 : 0;
        float verticalMargin = (screenSize.y-worldSize.y>0) ? (screenSize.y-worldSize.y)/2 : 0;
        cameraBounds[0] = (screenSize.x/2 - horizontalMargin) * cameraZoom;
        cameraBounds[1] = worldSize.y - screenSize.y/2 * cameraZoom + verticalMargin;
        cameraBounds[2] = worldSize.x - screenSize.x/2 * cameraZoom + horizontalMargin;
        cameraBounds[3] = (screenSize.y/2 - verticalMargin) * cameraZoom;

        System.out.println(worldSize);
        System.out.println(horizontalMargin);
        System.out.println(verticalMargin);
        System.out.println(cameraBounds[0]);
        System.out.println(cameraBounds[1]);
        System.out.println(cameraBounds[2]);
        System.out.println(cameraBounds[3]);

        renderer = new OrthogonalTiledMapRenderer(map, 1);
        camera=new OrthographicCamera(screenSize.x, screenSize.y);//(1080,720);
        camera.setToOrtho(false);
        camera.zoom = cameraZoom;
        cameraPosition = camera.position;
        Gdx.input.setInputProcessor(this);
    }

    /* To-Override Methods */

    /** Meant to be overrided : render & update entities between layers */
    public abstract void renderScreen(float delta, SpriteBatch batch);
    public abstract void updateScreen(float delta);

    /* Utils methods */

    public Vector2 screenToWorld(Vector2 screenCoord) {
      Vector3 proj = camera.unproject(new Vector3(screenCoord, 0));
      return new Vector2(proj.x, proj.y);
    }
    public Vector2 worldToScreen(Vector2 worldCoord) {
      Vector3 proj = camera.project(new Vector3(worldCoord, 0));
      return new Vector2(proj.x, proj.y);
    }
    public GridPoint2 screenToTile(Vector2 screenCoord) {
      Vector2 worldCoord = screenToWorld(screenCoord);
      int tileX = (int)(worldCoord.x / tileSize.x);
      int tileY = (int)(worldCoord.y / tileSize.y);
      return new GridPoint2(tileX, tileY);
    }
    public Vector2 tileToScreen(GridPoint2 tileCoord) {
      Vector2 worldCoord = new Vector2(tileCoord.x, tileCoord.y).scl(tileSize);
      return worldToScreen(worldCoord);
    }


  public void renderCamera(float delta) {

        //Translation
        if(moveCameraToLeft) cameraPosition.x -= cameraSpeed*delta;
        if(moveCameraToTop) cameraPosition.y += cameraSpeed*delta;
        if(moveCameraToRight) cameraPosition.x += cameraSpeed*delta;
        if(moveCameraToBottom) cameraPosition.y -= cameraSpeed*delta;

        // Position compared to cameraBounds
        if (cameraPosition.x < cameraBounds[0]) cameraPosition.x = cameraBounds[0]; //left
        if (cameraPosition.y > cameraBounds[1]) cameraPosition.y = cameraBounds[1]; //top
        if (cameraPosition.x > cameraBounds[2]) cameraPosition.x = cameraBounds[2]; //right
        if (cameraPosition.y < cameraBounds[3]) cameraPosition.y = cameraBounds[3]; //bottom

        //Update camera position
        camera.position.set(cameraPosition);

        //Clear screan
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update camera & refresh view
        camera.update();
        renderer.setView(camera);
    }


    /* Getters & Setters */

    public TiledMap getMap() {
        return map;
    }

    /* Implemented com.axlan.gdxtactics.Game.Screen Methods */

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // Camera rendering
        renderCamera(delta);


        // Map rendering
        int[] backgroundLayers = { 0 };
        int[] foregroundLayers = new int[layers.size()-1];
        for (int i=1; i<layers.size();i++) { foregroundLayers[i-1] = i; }

        /* Update & Render entities between layers */
        renderer.render(backgroundLayers);
        updateScreen(delta);
        renderScreen(delta, this.batch);
        renderer.render(foregroundLayers);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }


    /* Implemented InputProcessor Methods */

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //Vector3 clickCoordinates = new Vector3(screenX,screenY,0);
        //cameraPosition = camera.unproject(clickCoordinates);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        moveCameraToLeft = (screenX > 0 && screenX < tileSize.x);
        moveCameraToTop = (screenY > 0  && screenY < tileSize.y);
        moveCameraToRight = (screenX > screenSize.x - tileSize.x && screenX < screenSize.x);
        moveCameraToBottom = (screenY > screenSize.y - tileSize.y && screenY < screenSize.y);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
