package com.axlan.gdxtactics.desktop;

import com.axlan.fogofwar.Core;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

class DesktopLauncher {
	public static void main (String[] arg) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    ApplicationListener gameCore;
    if (arg.length > 0) {
      gameCore = new Core(arg[0]);
    } else {
      gameCore = new Core();
    }
    new Lwjgl3Application(gameCore, config);
	}
}
