package com.axlan.fogofwar.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

//TODO-P2 make this more generic and move to gdxtactics

/**
 * Class for storing the state of the current game session. State should be complete for saving and
 * reloading.
 *
 * <p>State is static and shared throughout application
 */
public class GameStateManager {
  public static final String EMPTY_LABEL = "Empty";
  private static final int NUM_SLOTS = 10;
  private static final String SAVE_PREF_NAME = "save_slots";
  private static final String SAVE_SLOT_NAME = "slot_";
  private static final String SAVE_SLOT_TIME_NAME = "slot_time_";
  /**
   * Active GameState
   */
  public static GameState gameState = new GameState();
  /**
   * Cache of saved game states. Null if unused
   */
  private static GameState[] slots = null;
  /** Timestamps for each save slot. Null if unused */
  private static long[] slotsTimes = null;

  /**
   * Loads cache from persistent preferences
   */
  private static void fetchSavesFromPrefs() {
    if (slots != null) {
      return;
    }
    slots = new GameState[NUM_SLOTS];
    slotsTimes = new long[NUM_SLOTS];
    Preferences prefs = Gdx.app.getPreferences(SAVE_PREF_NAME);
    Gson gson = new GsonBuilder().create();
    for (int i = 0; i < NUM_SLOTS; i++) {
      String slotString = prefs.getString(SAVE_SLOT_NAME + i, "");
      if (!slotString.isEmpty()) {
//        slotString.replaceAll("\\n", "\n");
//        System.out.println(slotString);
        try {
          slots[i] = gson.fromJson(slotString, GameState.class);
          slotsTimes[i] = prefs.getLong(SAVE_SLOT_TIME_NAME + i);
        } catch (Exception e) {
          e.printStackTrace();
          prefs.remove(SAVE_SLOT_NAME + i);
          prefs.remove(SAVE_SLOT_TIME_NAME + i);
        }
      }
    }
  }

  /**
   * Gets string identifiers for each save slot. {@link #EMPTY_LABEL} if unused
   * @return array of string labels for save slots
   */
  public static String[] getSlotLabels() {
    fetchSavesFromPrefs();
    String[] slotNames = new String[NUM_SLOTS];
    Arrays.fill(slotNames, EMPTY_LABEL);
    DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
    for (int i = 0; i < NUM_SLOTS; i++) {
      if (slotsTimes[i] != 0) {
        Date date = new Date(slotsTimes[i]);
        slotNames[i] = df.format(date);
      }
    }
    return slotNames;
  }

  /** Save active game data to save slot and write to persistent Preferences
   *
   * @param slot index of slot to use. Must be less then {@link #NUM_SLOTS}
   */
  public static void save(int slot) {
    assert slot < NUM_SLOTS;
    fetchSavesFromPrefs();
    slots[slot] = new GameState(gameState);
    slotsTimes[slot] = System.currentTimeMillis();
    Preferences prefs = Gdx.app.getPreferences(SAVE_PREF_NAME);
    // enableComplexMapKeySerialization needed to properly serialize TilePoint key in playerUnitPlacements
    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    prefs.putString(SAVE_SLOT_NAME + slot, gson.toJson(gameState));
    prefs.putLong(SAVE_SLOT_TIME_NAME + slot, slotsTimes[slot]);
    prefs.flush();
  }

  /** Replace active game data with data in save slot
   *
   * @param slot index of slot to use. Must be less then {@link #NUM_SLOTS}
   */
  public static void load(int slot) {
    assert slot < NUM_SLOTS;
    fetchSavesFromPrefs();
    if (slots[slot] != null) {
      gameState = new GameState(slots[slot]);
    }
  }

}
