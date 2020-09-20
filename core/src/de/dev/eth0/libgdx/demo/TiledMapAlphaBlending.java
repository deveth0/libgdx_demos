package de.dev.eth0.libgdx.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Demo which overlays a TiledMap with an Alpha Layer for light effects
 */
public class TiledMapAlphaBlending extends ApplicationAdapter {

  private OrthogonalTiledMapRenderer mapRenderer;
  private OrthographicCamera camera;
  private Viewport viewport;
  private AlphaBlendingMap alphaBlendingMap;

  @Override
  public void create() {
    super.create();
    TiledMap tiledMap = new TmxMapLoader().load("collision.tmx");
    mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / 16f);

    //Configure viewport / camera to show the whole world
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 4, 4);
    viewport = new FitViewport(4, 4, camera);
    camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0f);
    camera.zoom = 2f;
    camera.update();

    OrthographicCameraController cameraController = new OrthographicCameraController(camera);
    Gdx.input.setInputProcessor(cameraController);

    alphaBlendingMap = new AlphaBlendingMap(viewport);
  }

  @Override
  public void render() {
    viewport.apply();
    // clear the screen
    Gdx.gl.glClearColor(0f, 0f, 0f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    mapRenderer.setView(camera);
    mapRenderer.render();

    alphaBlendingMap.draw();
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    viewport.update(width, height);
  }

  /**
   * This should render a layer onto the map with some light
   */
  static class AlphaBlendingMap extends Stage {

    private final Color LIGHT_COLOR = new Color(0f, 0f, 1f, .2f);
    private FrameBuffer frameBuffer;

    public AlphaBlendingMap(Viewport viewport) {
      super(viewport);


      // add two lights
      addActor(getLightImage(1, 1, 2, 2));
      addActor(getLightImage(1, 1, 1, 3));

      //TODO: IS THIS CORRECT? I assume the Framebuffer should have the size of world
      frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 4, 4, false);
    }

    @Override
    public void draw() {
      Batch batch = getBatch();
      Camera camera = getViewport().getCamera();
      camera.update();

      frameBuffer.begin();
      batch.setProjectionMatrix(camera.combined);
      // clear buffer with a nice dusk lightning color
      Gdx.gl.glClearColor(LIGHT_COLOR.r, LIGHT_COLOR.g, LIGHT_COLOR.b, LIGHT_COLOR.a);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

      Matrix4 m = new Matrix4();
      m.setToOrtho2D(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
      batch.setProjectionMatrix(m);
      // draw the actual lights
      batch.begin();
      getRoot().draw(batch, 1);
      batch.end();
      frameBuffer.end();

      batch.setProjectionMatrix(camera.combined);
      getViewport().apply();
      batch.begin();
      batch.draw(frameBuffer.getColorBufferTexture(), 0,frameBuffer.getWidth(), frameBuffer.getWidth(), -1 * frameBuffer.getHeight());
      batch.end();
    }

    private Image getLightImage(int width, int height, int x, int y) {
      // generate a blurry circle for lightning, positioned on tile 2/2
      Pixmap pixmap = new Pixmap(11, 11, Pixmap.Format.RGBA8888);
      pixmap.setColor(1, 1, 1, 1);
      pixmap.fillCircle(5, 5, 5);
      Pixmap blured = BlurUtils.blur(pixmap, 2, 1, true);
      Image image = new Image(new Texture(blured));
      image.setSize(width, height);
      image.setPosition(x, y);
      return image;
    }
  }
}
