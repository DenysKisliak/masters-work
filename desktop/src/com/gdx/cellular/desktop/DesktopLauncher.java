package com.gdx.cellular.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gdx.cellular.CellularAutomaton;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();



		config.width = 1280;
		config.height = 800;
		new LwjglApplication(new CellularAutomaton(), config);
	}
}
