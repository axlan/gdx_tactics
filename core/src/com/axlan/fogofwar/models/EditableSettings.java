package com.axlan.fogofwar.models;

import com.axlan.gdxtactics.JsonLoader;
import com.axlan.gdxtactics.TilePoint;
import com.badlogic.gdx.Gdx;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

//TODO-P2 Switch to libGDX Preferences to allow saves in HTML5

/**
 * Class for loading and storing settings from JSON. The structure of the JSON should mimic the
 * structure of the class.
 *
 * <p> A static instance is managed by {@link LoadedResources}
 */
@SuppressWarnings({"WeakerAccess"})
public final class EditableSettings implements Cloneable {

    /**
     * Defaults to fallback on if modified settings not found
     */
    private static EditableSettings defaults = null;
    /** Make game fullscreen */
    public boolean fullScreen;
    /** Screen resolution to use */
    public TilePoint screenSize;

    private EditableSettings() {
    }

    /** Load the default settings */
    public static void setDefaults(String projectPath) {
        defaults = JsonLoader.loadFromJsonFileInternal(projectPath, EditableSettings.class);
    }

    /**
     * This method deserializes the JSON read from the specified path into a Settings object
     * Returns a clone of the default settings if projectPath cannot be loaded
     *
     * @param projectPath path in the assets directory to JSON file to parse
     * @return a new instance of LevelData populated from the JSON file
     * @throws JsonIOException     if there was a problem reading from the Reader
     * @throws JsonSyntaxException if json is not a valid representation for an object of type
     */
    @SuppressWarnings("SameParameterValue")
    static EditableSettings loadFromJson(String projectPath) {
        assert defaults != null;
        try {
            EditableSettings loaded = JsonLoader.loadFromJsonFileExternal(projectPath, EditableSettings.class);
            if (loaded != null) {
                return loaded;
            }
            System.out.println("Invalid settings file. Using defaults");
        } catch (Exception e) {
            System.out.println("Missing settings file. Using defaults");
        }
        try {
            return (EditableSettings) defaults.clone();
        } catch (CloneNotSupportedException ex) {
            // Shouldn't be reachable
            ex.printStackTrace();
            throw new RuntimeException("CloneNotSupportedException problem");
        }
    }

    /** Apply the settings to the currently running game */
    public void apply() {
        if (fullScreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(screenSize.x, screenSize.y);
        }
    }

}
