package de.dev.eth0.libgdx.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.dev.eth0.libgdx.demo.utils.OrthographicCameraController;

/**
 * Demo on how to implement a light system for tiledMaps
 */
public class TiledMapLightMap extends ApplicationAdapter {

  private OrthogonalTiledMapRenderer mapRenderer;
  private OrthographicCamera camera;
  private Viewport viewport;

  private Texture lightTexture;
  private Matrix4 worldProjectionMatrix;

  private FrameBuffer frameBuffer;

  public static final int WORLD_WIDTH = 50;
  public static final int WORLD_HEIGHT = 30;


  @Override
  public void create() {
    super.create();
    TiledMap tiledMap = new TmxMapLoader().load("forest.tmx");
    mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 32f);

    //Configure viewport / camera to show the whole world
    camera = new OrthographicCamera();
    camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
    viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    // center camera on world
    camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0f);

    OrthographicCameraController cameraController = new OrthographicCameraController(camera);
    Gdx.input.setInputProcessor(cameraController);

    // Matrix used to project onto the world
    worldProjectionMatrix = new Matrix4();
    worldProjectionMatrix.setToOrtho2D(0f,0f , WORLD_WIDTH, WORLD_HEIGHT);

    lightTexture=new Texture("light_forest.png");
  }

  @Override
  public void render() {
    camera.update();

    Batch batch = mapRenderer.getBatch();

    // First we create a frame-buffer which contains all lights
    frameBuffer.begin();

    // Clear framebuffer
    Gdx.gl.glClearColor(0,0,0,1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    // Project the Framebuffer size onto the World size
    batch.setProjectionMatrix(worldProjectionMatrix);

    // Render the lights
    batch.setBlendFunction(GL20.GL_ONE,GL20.GL_ZERO);
    batch.begin();
    batch.draw(lightTexture, 0,0,WORLD_WIDTH,WORLD_HEIGHT);
    batch.end();

    frameBuffer.end();

    // clear screen
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


    viewport.apply();
    // Now we render the map
    batch.setProjectionMatrix(camera.combined);
    mapRenderer.setView(camera);
    mapRenderer.render();

    // Add lights
    batch.setBlendFunction( GL20.GL_ZERO,GL20.GL_SRC_COLOR);
    batch.begin();
    // framebuffer is upside down, therefore we need to modify the y and height
    batch.draw(frameBuffer.getColorBufferTexture(),0,WORLD_HEIGHT, WORLD_WIDTH,-1* WORLD_HEIGHT);
    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
    if(frameBuffer !=null && (frameBuffer.getWidth()!=width || frameBuffer.getHeight()!=height )) {
      frameBuffer.dispose();
      frameBuffer=null;
    }

    if(frameBuffer==null){
      try {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
      }catch (GdxRuntimeException e){
        frameBuffer=new FrameBuffer(Pixmap.Format.RGB565,width,height,false);
      }
    }
  }

  public static void main (String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width = 2400;
    config.height = 800;
    new LwjglApplication(new TiledMapLightMap(), config);
  }
}
