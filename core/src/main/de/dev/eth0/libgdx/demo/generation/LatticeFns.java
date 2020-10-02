package de.dev.eth0.libgdx.demo.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.badlogic.gdx.Gdx;

public class LatticeFns {

  // for speed and convenience we assume wall will always be 1 so
  // we can do a simple count to find the number of walls surrounding us.
  public static final boolean EMPTY = false;
  public static final boolean FILLED = true;

  private static final String TAG = LatticeFns.class.getSimpleName();

  public static final Point[] MOORE_HOOD = {
      new Point(-1, -1), new Point(0, -1), new Point(1, -1),
      new Point(1, 0), new Point(1, 1), new Point(0, 1),
      new Point(-1, 1), new Point(-1, 0)
  };

  public static final Point[] VON_NEUMANN_HOOD = {
      new Point(0, -1), new Point(-1, 0), new Point(1, 0), new Point(0, 1)
  };

  /**
   * This map provides quick lookup by Point to get the starting index
   * in the Moore-Neighborhood.
   */
  public static final Map<Point, Integer> MOORE_INDEX = new HashMap<>();

  static {
    for (int i = 0; i < MOORE_HOOD.length; ++i) {
      MOORE_INDEX.put(MOORE_HOOD[i], i);
    }
  }


  /**
   * We count the cell that is requested as well.
   *
   * @param map
   * @param y
   * @param x
   * @return
   */
  public static int getNeighborCount(boolean[][] map, int y, int x) {
    int count = 0;
    for (int i = y - 1; i <= y + 1; ++i) {
      for (int j = x - 1; j <= x + 1; ++j) {
        if (i < 0 || j < 0 || i >= map.length || j > map[i].length) {
          continue;
        }
        count += map[i][j] == FILLED ? 1 : 0;
      }
    }
    return count;
  }

  /**
   * We count the cell that is requested as well.
   *
   * @param map
   * @param y
   * @param x
   * @return
   */
  public static int getTwoStepNeighborCount(boolean[][] map, int y, int x) {
    int count = 0;
    for (int i = y - 2; i <= y + 2; ++i) {
      for (int j = x - 2; j <= x + 2; ++j) {
        if (Math.abs(i - y) == 2 && Math.abs(j - x) == 2) {
          continue;
        }
        if (i < 0 || j < 0 || i >= map.length || j >= map[i].length) {
          continue;
        }
        count += map[i][j] == FILLED ? 1 : 0;
      }
    }
    return count;
  }

  public static void addNeighbor(Point neighbor,
      boolean[][] map,
      boolean type,
      LinkedHashSet<Point> notVisited,
      LinkedList<Point> frontier) {

    if (!neighbor.valid(0, map[0].length, 0, map.length))
      return;

    if (map[neighbor.y][neighbor.x] == type
        && notVisited.contains(neighbor)) {
      notVisited.remove(neighbor);
      frontier.addLast(neighbor);
    }
  }

  /**
   * Return a list containing a representation of each room.  Rooms
   * are a collection of points, one for each cell in the room.
   *
   * @param map
   * @param type
   * @return
   */
  public static List<Set<Point>> getRooms(boolean[][] map, boolean type) {
    int height = map.length;
    int width = map[0].length;

    Point start = null;
    LinkedHashSet<Point> notVisited = new LinkedHashSet<>();
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        if (map[y][x] == type) {
          if (start == null)
            start = new Point(x, y);
          else
            notVisited.add(new Point(x, y));
        }
      }
    }
    Gdx.app.log(TAG, "Number of points: " + notVisited.size());
    List<Set<Point>> rooms = new ArrayList<>();
    LinkedList<Point> frontier = new LinkedList<>();
    frontier.add(start);
    while (!notVisited.isEmpty()) {
      Set<Point> room = new LinkedHashSet<>();
      while (!frontier.isEmpty()) {
        Point current = frontier.removeFirst();
        room.add(current);

        for (Point p : VON_NEUMANN_HOOD) {
          addNeighbor(Point.add(current, p), map, type,
              notVisited, frontier);
        }
      }
      rooms.add(room);

      if (notVisited.size() > 0) {
        Iterator<Point> iterator = notVisited.iterator();
        frontier.add(iterator.next());
        iterator.remove();
      }
    }

    if (!frontier.isEmpty()) {
      Gdx.app.log(TAG, "Frontier not empty! " + frontier.size());
      Set<Point> room = new LinkedHashSet<>();
      room.add(frontier.getFirst());
      rooms.add(room);
    }

    return rooms;
  }

}
