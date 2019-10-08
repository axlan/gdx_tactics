package com.axlan.gdxtactics;

import com.badlogic.gdx.Gdx;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;

/**
 * Class for loading objects from JSON using GSON
 */
public class JsonLoader {

  /**
   * This method deserializes the JSON read from the specified path into a <T> object
   *
   * @param projectPath path in the assets directory to JSON file to parse
   * @param classOfT    class of object to load
   * @return a new instance of <T> populated from the JSON file
   * @throws JsonIOException     if there was a problem reading from the Reader
   * @throws JsonSyntaxException if json is not a valid representation for an object of type
   */
  public static <T> T loadFromJsonFile(String projectPath, Class<T> classOfT) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapterFactory(new ImmutableListTypeAdapterFactory());
    Reader reader = Gdx.files.internal(projectPath).reader();
    return gsonBuilder.create().fromJson(reader, classOfT);
  }

  /**
   * This method deserializes the JSON from a String into a <T> object
   *
   * @param jsonData String containing JSON to parse
   * @param classOfT class of object to load
   * @return a new instance of <T> populated from the JSON file
   * @throws JsonIOException     if there was a problem reading from the Reader
   * @throws JsonSyntaxException if json is not a valid representation for an object of type
   */
  public static <T> T loadFromJsonString(String jsonData, Class<T> classOfT) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapterFactory(new ImmutableListTypeAdapterFactory());
    return gsonBuilder.create().fromJson(jsonData, classOfT);
  }

}
