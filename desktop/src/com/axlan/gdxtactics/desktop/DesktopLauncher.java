package com.axlan.gdxtactics.desktop;

import com.axlan.fogofwar.Core;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    ApplicationListener gameCore;
    if (arg.length > 0) {
      gameCore = new Core(arg[0]);
    } else {
      gameCore = new Core();
    }
    new LwjglApplication(gameCore, config);
	}
}
