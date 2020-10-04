package de.dev.eth0.libgdx.demo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.dev.eth0.libgdx.demo.TiledMapLightMap;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 2400;
		config.height = 800;
		new LwjglApplication(new TiledMapLightMap(), config);
	}
}
