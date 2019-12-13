package com.axlan.fogofwar.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//TODO-P2 Rerun this on resize
//TODO-P2 Load font descriptions from settings

public class FontManager {

  public final Map<String, BitmapFont> fontTable;
  /**
   * Base resolution for scaling fonts
   */
  private final double BASE_WIDTH = 2880;

  FontManager() {

    FreeTypeFontGenerator.setMaxTextureSize(4096);
    HashMap<String, BitmapFont> tmpFontTable = new HashMap<>();

    tmpFontTable.put("BlackOpsOne-Regular-large",
        createFont("BlackOpsOne-Regular.ttf", 400));
    tmpFontTable.put("Ubuntu-Regular-medium",
        createFont("Ubuntu-Regular.ttf", 50));

    fontTable = Collections.unmodifiableMap(tmpFontTable);

  }

  private BitmapFont createFont(String fileName, double baseSize) {
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + fileName));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    int fontSize = (int) Math.round(baseSize * ((double) Gdx.graphics.getWidth()) / BASE_WIDTH);
    System.out.println(fontSize);
    System.out.println(Gdx.graphics.getWidth());
    parameter.size = fontSize;
    BitmapFont font = generator.generateFont(parameter);
    generator.dispose();
    return font;
  }


}
