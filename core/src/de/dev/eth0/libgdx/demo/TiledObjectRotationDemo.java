package de.dev.eth0.libgdx.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class TiledObjectRotationDemo extends ApplicationAdapter {

  @Override
  public void create() {
    TiledMap tiledMap = new TmxMapLoader().load("collision.tmx");
    TiledMapTile tile = tiledMap.getTileSets().getTileSet(0).iterator().next();
    MapObject mapObject = tile.getObjects().get(0);


    RectangleMapObject rectangleMapObject = (RectangleMapObject)mapObject;
    System.out.println("The Object is 8x16 px and rotated by 90°, so it covers the left 50% of the tile");
    System.out.println("MapObject Rotation: " + rectangleMapObject.getRectangle());
    // The following lines demonstrate, that the rotation is NOT applied to the rectangle.
    System.out.println("(8/8) should be NOT be inside the rotated rectangle (== false): " + rectangleMapObject.getRectangle().contains(8, 8));
    System.out.println("(1/1) should be inside the rotated rectangle (==true): " + rectangleMapObject.getRectangle().contains(1, 1));

    System.out.println("Expected structure (zoom 2x):");
    System.out.println("O O O O T T T T");
    System.out.println("O O O O T T T T");
    System.out.println("O O O O T T T T");
    System.out.println("O O O O T T T T");
    System.out.println("O O O O T T T T");
    System.out.println("O O O O T T T T");
    System.out.println("O O O O T T T T");

    System.out.println("");
    System.out.println("");
    System.out.println("Actual structure (zoom 2x):");
    for (int x = 0; x < 16; x+=2) {
      for (int y = 0; y < 16; y+=2) {
        boolean contained = rectangleMapObject.getRectangle().contains(x, y);
        System.out.print(contained ? "O " : "T ");
      }
      System.out.println();
    }

  }

  @Override
  public void render() {
    Gdx.gl.glClearColor(1, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

  }

  @Override
  public void dispose() {
  }
}
