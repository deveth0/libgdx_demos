package de.dev.eth0.libgdx.demo.utils;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Controller that allows to drag a camera around
 */
public class OrthographicCameraController extends InputAdapter {

  private final OrthographicCamera camera;
  private final Vector3 curr = new Vector3();
  private final Vector3 last = new Vector3(-1f, -1f, -1f);
  private final Vector3 delta = new Vector3();

  public OrthographicCameraController(OrthographicCamera camera) {
    this.camera = camera;
  }

  @Override
  public boolean scrolled(int amount){
    camera.zoom = camera.zoom + (amount * 0.1f);
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    last.set(-1f, -1f, -1f);
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    camera.unproject(curr.set(screenX, screenY, 0f));
    if (!(last.x == -1f && last.y == -1f && last.z == -1f)) {
      camera.unproject(delta.set(last.x, last.y, 0f));
      delta.sub(curr);
      camera.position.add(delta.x, delta.y, 0f);
    }
    last.set(screenX, screenY, 0);
    return false;
  }
}
