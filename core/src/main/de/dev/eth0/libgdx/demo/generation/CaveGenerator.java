package de.dev.eth0.libgdx.demo.generation;

import static de.dev.eth0.libgdx.demo.generation.LatticeFns.EMPTY;
import static de.dev.eth0.libgdx.demo.generation.LatticeFns.FILLED;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

/**
 * This is a modified version of Wesley Kerr's Code:
 * <p>
 * <a href="https://github.com/wesleykerr/level-generator">Github: Wesley Kerr - Level Generator</a>
 * </p>
 * Instead of generating the cells content, we generate values for each corner as described here:
 * <p>
 * <a href="https://web.archive.org/web/20150104062342/http://blog.project-retrograde.com/2013/05/marching-squares/">Squares Made for Marching</a>
 * </p>
 */
public class CaveGenerator {

  private static final String TAG = CaveGenerator.class.getSimpleName();

  private long seed;
  private Random random;

  private boolean[][] map;
  private boolean[][] bufferMap;

  private int width;
  private int height;

  private final List<Phase> phases;
  // Size per Tile
  private int tileSize = 16;
  // Texture Atlas used for the tiles
  private TextureAtlas tileSet;

  private CaveGenerator() {
    this.seed = 7;
    this.phases = new ArrayList<>();
  }

  public void initialize() {
    random = new Random(seed);

    map = new boolean[height][width];
    bufferMap = new boolean[height][width];
    for (int i = 0; i < height; ++i) {
      for (int j = 0; j < width; ++j) {
        bufferMap[i][j] = FILLED;

        if (i == 0 || j == 0 || i == map.length - 1
            || j == map[i].length - 1) {
          map[i][j] = FILLED;
          continue;
        }

        if (random.nextDouble() < 0.4) {
          map[i][j] = FILLED;
        }
      }
    }
    Gdx.app.debug(TAG, "Initial");
    Gdx.app.debug(TAG, toString(map));
  }

  public void step(int minCount, int maxCount) {
    // if we haven't called initialize yet
    // go ahead and do it it ourselves.
    if (bufferMap == null) {
      initialize();
    }
    boolean[][] tmpMap;
    for (int i = 1; i < height - 1; ++i) {
      for (int j = 1; j < width - 1; ++j) {
        int count1 = LatticeFns.getNeighborCount(map, i, j);
        int count2 = LatticeFns.getTwoStepNeighborCount(map, i, j);
        if (count1 >= minCount || count2 <= maxCount) {
          bufferMap[i][j] = FILLED;
        }
        else {
          bufferMap[i][j] = EMPTY;
        }
      }
    }
    tmpMap = map;
    map = bufferMap;
    bufferMap = tmpMap;
  }

  public void iterate() {
    for (Phase p : phases) {
      for (int i = 0; i < p.getRounds(); ++i) {
        step(p.getMin(), p.getMax());
        Gdx.app.debug(TAG, "Round: " + i);
        Gdx.app.debug(TAG, "\n" + toString(map));
      }
    }
  }

  void fixRooms() {
    List<Set<Point>> rooms = LatticeFns.getRooms(map, EMPTY);
    rooms.sort((set1, set2) -> Integer.compare(set2.size(), set1.size()));
    for (int i = 1; i < rooms.size(); ++i) {
      fixRoom(rooms.get(i));
    }
  }

  public boolean[][] getMap() {
    return map;
  }

  public TiledMap generate() {
    initialize();
    iterate();

    Gdx.app.debug(TAG, "\n" + toString(map));
    bufferMap = null;
    fixRooms();

    return createTiledMap();
  }

  private TiledMap createTiledMap() {
    TiledMap ret = new TiledMap();

    MapLayers layers = ret.getLayers();

    TiledMapTileLayer tileLayer = new TiledMapTileLayer(width - 1, height - 1, tileSize, tileSize);

    MarchingSquaresSelector tileSelector = new MarchingSquaresSelector(map, tileSet);

    for (int y = 0; y < tileLayer.getWidth(); y++) {
      for (int x = 0; x < tileLayer.getHeight(); x++) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

        TextureRegion texture = tileSelector.getTextureRegion(x, y);
        cell.setTile(new StaticTiledMapTile(texture));
        // TiledMap's origin is the bottom-left corner but our map was generated using top-left as origin.
        tileLayer.setCell(x, tileLayer.getHeight() - 1 - y, cell);
      }
    }

    layers.add(tileLayer);

    return ret;
  }

  /**
   * Choose a random point from the room and walk towards the center of the
   * map until we encounter an empty cell that is not part of this room.
   * <p>
   * TODO(wkerr): There are still cases where we march across the map and
   * fail to connect the room to anything else.  The fix I put in stops this
   * algorithm from overwriting a wall, but doesn't address the underlying
   * cause of this problem.
   *
   * @param room - all of the points in this room.
   */
  void fixRoom(Set<Point> room) {
    Point point = room.iterator().next();

    Point delta = new Point(
        (int)Math.signum((width / 2) - point.x),
        (int)Math.signum((height / 2) - point.y));

    while (point.valid(0, width, 0, height)) {
      move(point, delta);

      if (!point.valid(1, width - 1, 1, height - 1))
        break;

      if (map[point.y][point.x] == EMPTY && !room.contains(point)) {
        return;
      }
      if (map[point.y][point.x] == FILLED) {
        map[point.y][point.x] = EMPTY;
      }

    }

    Gdx.app.error(TAG, "Encountered a boundary before finding an open space!");
    Gdx.app.error(TAG, ".. last location: " + point.x + ", " + point.y);
  }

  /**
   * This will move the point along the delta direction provided.  This
   * ensures that we actually move before returning to prevent returning to
   * the same location.
   *
   * @param point
   */
  void move(Point point, Point delta) {
    int x = point.x;
    int y = point.y;
    while (x == point.x && y == point.y) {
      if (random.nextDouble() < 0.5)
        x += delta.x;
      else
        y += delta.y;
    }
    point.setLocation(x, y);
  }

  @Override
  public String toString() {
    return toString(map);
  }

  public static String toString(boolean[][] map) {
    StringBuilder buf = new StringBuilder();
    for (boolean[] booleans : map) {
      for (boolean aBoolean : booleans) {
        if (aBoolean == EMPTY)
          buf.append(".");
        else
          buf.append("#");
      }
      buf.append("\n");
    }
    return buf.toString();
  }

  public static class Phase {

    private final int min;
    private final int max;
    private final int rounds;

    public Phase(int min, int max, int rounds) {
      this.min = min;
      this.max = max;
      this.rounds = rounds;
    }

    public int getMin() {
      return min;
    }

    public int getMax() {
      return max;
    }

    public int getRounds() {
      return rounds;
    }
  }

  public static class Builder {

    private CaveGenerator cave;

    private Builder() {
      cave = new CaveGenerator();
    }

    public Builder withSize(int width, int height) {
      cave.width = width + 1;
      cave.height = height + 1;
      return this;
    }

    public Builder withTileSize(int tileSize) {
      cave.tileSize = tileSize;
      return this;
    }

    public Builder withTileSet(TextureAtlas tileSet) {
      cave.tileSet = tileSet;
      return this;
    }

    public Builder withRandomSeed(long seed) {
      cave.seed = seed;
      return this;
    }

    public Builder addPhase(int min, int max, int rounds) {
      cave.phases.add(new Phase(min, max, rounds));
      return this;
    }

    public CaveGenerator build() {
      CaveGenerator tmp = cave;
      cave = null;
      return tmp;
    }

    public static Builder create() {
      return new Builder();
    }
  }

}
