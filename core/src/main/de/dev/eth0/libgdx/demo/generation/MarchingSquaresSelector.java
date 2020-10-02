package de.dev.eth0.libgdx.demo.generation;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Utility to select to correct tile from a given tileset using the Marching Squares Algorithm
 * https://en.wikipedia.org/wiki/Marching_squares#Isoline
 */
public class MarchingSquaresSelector {

  private final boolean[][] map;
  private final TextureAtlas tileSet;


  public MarchingSquaresSelector(boolean[][] map, TextureAtlas tileSet) {
    this.map = map;
    this.tileSet = tileSet;
  }

  /**
   * Selects the correct texture-region for the requested cell.
   * Note: the coordinates are the resulting coordinates and not the coordinates of the corners.
   *
   * @param x - the x coordinate of the cell.
   * @param y - the y coordinate of the cell.
   */
  public TextureRegion getTextureRegion(int x, int y) {

    boolean[][] corners = getCorners(x, y);

    int idx = toInt(corners[1][0]) | toInt(corners[1][1]) << 1 | toInt(corners[0][1]) << 2 | toInt(corners[0][0]) << 3;

    return tileSet.findRegion(String.valueOf(idx));
  }


  private int toInt(boolean bool) {
    return bool ? 0 : 1;
  }

  /**
   * Gets the 4 corners of the requested block
   *
   * @param x - x coordinate of the block
   * @param y - y coordinate of the block
   * @return the corners of the block
   */
  private boolean[][] getCorners(int x, int y) {
    boolean[][] ret = new boolean[2][2];

    ret[0][0] = map[y][x];
    ret[0][1] = map[y][x + 1];
    ret[1][0] = map[y + 1][x];
    ret[1][1] = map[y + 1][x + 1];
    return ret;
  }
}
