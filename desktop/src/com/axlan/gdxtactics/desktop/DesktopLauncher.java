package com.axlan.gdxtactics.desktop;

import com.axlan.fogofwar.*;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    ApplicationListener gameCore;
    String demoMode = (arg.length > 0) ? arg[0] : "none";
    switch (demoMode) {
      case "battle":
        gameCore = new BattleDemo();
        break;
      case "deploy":
        gameCore = new DeployDemo();
        break;
      case "path":
        gameCore = new PathSearchDemo();
        break;
      case "overworld":
        gameCore = new OverwoldDemo();
        break;
      default:
        gameCore = new Core();
        break;
    }
    new LwjglApplication(gameCore, config);
	}
}
