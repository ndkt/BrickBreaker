package com.sabpisal.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sabpisal.BrickBreaker;

public class DesktopLauncher {
	public static int bbbwidth = 1366;
	public static int bbbheight = 768;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Berry Brick-Breaker Deluxe";
		config.width = bbbwidth;
		config.height = bbbheight;
		new LwjglApplication(new BrickBreaker(), config);
	}
}
