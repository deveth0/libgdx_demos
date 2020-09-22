package de.dev.eth0.libgdx.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

/**
 * This demonstrates how to use an overlay to simulate lights.
 *
 * It uses a simple image containing three colored lights (white, orange, red)
 *
 * Based on https://stackoverflow.com/a/45598754/2625592
 */
public class LightMap extends ApplicationAdapter {

  private FrameBuffer frameBuffer;
  private SpriteBatch spriteBatch;
  private OrthographicCamera camera;

  private Texture lightTexture,backgroundTexture;

  @Override
  public void create() {

    camera=new OrthographicCamera();
    camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

    spriteBatch=new SpriteBatch();

    lightTexture=new Texture("light.png");

    backgroundTexture=new Texture("tileMap.png");

    frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 400, 400, false);

  }

  @Override
  public void render() {
    camera.update();
    // First we create a frame-buffer which contains all lights
    frameBuffer.begin();

    Gdx.gl.glClearColor(.2f,.2f,.2f,1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE);
    spriteBatch.begin();
    spriteBatch.draw(lightTexture, 0,0,400,400);
    spriteBatch.end();

    frameBuffer.end();

    // Now we render the background image
    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
    spriteBatch.begin();
    spriteBatch.draw(backgroundTexture,0,0);
    spriteBatch.end();

    // And then the lights
    spriteBatch.setProjectionMatrix(spriteBatch.getProjectionMatrix().idt());
    spriteBatch.setBlendFunction( GL20.GL_ZERO,GL20.GL_SRC_COLOR);
    spriteBatch.begin();
    spriteBatch.draw(frameBuffer.getColorBufferTexture(),-1,1,2,-2);
    spriteBatch.end();
  }

}
