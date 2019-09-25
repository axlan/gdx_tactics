package com.axlan.gdxtactics.desktop;

import com.axlan.gdxtactics.PathSearchDemo;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class PathSearchLauncher {

  public static void main(String[] arg) {
    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
    new LwjglApplication(new PathSearchDemo(), config);
  }
}
