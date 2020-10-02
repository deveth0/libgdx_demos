package de.dev.eth0.libgdx.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.dev.eth0.libgdx.demo.generation.CaveGenerator;
import de.dev.eth0.libgdx.demo.utils.OrthographicCameraController;

/**
 * Demo showcase for a procedurally generated map
 */
public class ProcedurallyGeneratedMapDemo extends ApplicationAdapter {

  private OrthogonalTiledMapRenderer mapRenderer;
  private OrthographicCamera camera;
  private Viewport viewport;

  private Stage debugStage;

  public static final int WORLD_WIDTH = 50;
  public static final int WORLD_HEIGHT = 50;
  public static final int TILE_SIZE = 32;

  @Override
  public void create() {
    super.create();

    //Configure viewport / camera to show the whole world
    camera = new OrthographicCamera();
    camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
    viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    // center camera on world
    camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0f);
    debugStage = new Stage(viewport);
    debugStage.setDebugAll(true);

    OrthographicCameraController cameraController = new OrthographicCameraController(camera);
    Gdx.input.setInputProcessor(cameraController);
    // generate map
    TiledMap tiledMap = generateMap();
    mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1f / TILE_SIZE);
  }

  private TiledMap generateMap() {
    AssetManager assetManager = new AssetManager();
    assetManager.load("marchingSquares.atlas", TextureAtlas.class);
    assetManager.finishLoading();

    TextureAtlas atlas = assetManager.get("marchingSquares.atlas", TextureAtlas.class);

    CaveGenerator caveGenerator = CaveGenerator.Builder.create()
        .withSize(WORLD_WIDTH, WORLD_HEIGHT)
        .withRandomSeed(System.currentTimeMillis())
        .addPhase(5, 2, 4)
        .addPhase(5, -1, 5)
        .withTileSet(atlas)
        .withTileSize(TILE_SIZE)
        .build();
    TiledMap ret = caveGenerator.generate();

    boolean[][] map = caveGenerator.getMap();
    for (int y = 0; y < map.length; y++) {
      for (int x = 0; x < map.length; x++) {
        // the generated map is upside down, so we need to shift the y position. We also shift the overlay so the corners match the tiles
        debugStage.addActor(new BlockActor(x - 0.5f, (map.length - 1.5f - y), map[y][x]));
      }
    }
    return ret;
  }

  @Override
  public void render() {
    camera.update();

    Batch batch = mapRenderer.getBatch();

    // clear screen
    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    viewport.apply();
    batch.setProjectionMatrix(camera.combined);
    mapRenderer.setView(camera);
    mapRenderer.render();

    debugStage.draw();

  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height);
  }

  /**
   * Actor used to render an overlay on the map which displays the filled and empty corners
   */
  private static class BlockActor extends Actor {

    private final boolean filled;

    public BlockActor(float x, float y, boolean filled) {
      this.filled = filled;
      this.setBounds(x, y, 1f, 1f);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
      shapes.end();

      Color color = new Color(filled ? 1 : 0, filled ? 0 : 1, 0, 0.2f);
      Gdx.gl.glEnable(GL20.GL_BLEND);
      Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
      shapes.setColor(color);
      shapes.begin(ShapeRenderer.ShapeType.Filled);
      shapes.rect(getX(), getY(), getWidth(), getHeight());
      shapes.end();
      Gdx.gl.glDisable(GL20.GL_BLEND);

      shapes.setColor(Color.BLACK);
      shapes.begin(ShapeRenderer.ShapeType.Line);
      shapes.rect(getX(), getY(), getWidth(), getHeight());
      shapes.end();

    }
  }

  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    config.width = 2400;
    config.height = 800;
    new LwjglApplication(new ProcedurallyGeneratedMapDemo(), config);
  }


}

