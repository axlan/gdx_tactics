package com.axlan.gdxtactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Base class for storing the state of the current game session. State should be complete for saving
 * and reloading.
 */
public abstract class GameStateManagerBase<T> {
  static final String EMPTY_LABEL = "Empty";
  private static final int NUM_SLOTS = 10;
  private static final String SAVE_PREF_NAME = "save_slots";
  private static final String SAVE_SLOT_NAME = "slot_";
  private static final String SAVE_SLOT_TIME_NAME = "slot_time_";
  protected final GsonBuilder gsonBuilder;

  /**
   * Active GameState
   */
  public T gameState = null;

  /**
   * Cache of saved game states. Null if unused
   */
  private T[] slots = null;

  /**
   * Timestamps for each save slot. Null if unused
   */
  private long[] slotsTimes = null;

  public GameStateManagerBase() {
    gsonBuilder = new GsonBuilder();
  }


  /**
   * Generate a new array of instances of game state T with no params
   *
   * @param length length of array
   * @return new array instance of class T of size length
   */
  @SuppressWarnings("SameParameterValue")
  protected abstract T[] newGameStateArray(int length);

  /**
   * Generate a new instance of game state T with copy constructor
   *
   * @return clone of T orig
   */
  protected abstract T newGameState(T orig);

  /**
   * Calls {@link #fetchSavesFromPrefs(Class)}
   */
  protected abstract void fetchSavesFromPrefs();

  /** Loads cache from persistent preferences */
  protected void fetchSavesFromPrefs(Class<T> type) {
    slots = newGameStateArray(NUM_SLOTS);
    slotsTimes = new long[NUM_SLOTS];
    Preferences prefs = Gdx.app.getPreferences(SAVE_PREF_NAME);
    Gson gson = gsonBuilder.create();
    for (int i = 0; i < NUM_SLOTS; i++) {
      String slotString = prefs.getString(SAVE_SLOT_NAME + i, "");
      if (!slotString.isEmpty()) {
        //        slotString.replaceAll("\\n", "\n");
        //        System.out.println(slotString);
        try {
          slots[i] = gson.fromJson(slotString, type);
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
   *
   * @return array of string labels for save slots
   */
  String[] getSlotLabels() {
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

  /**
   * Save active game data to save slot and write to persistent Preferences
   *
   * @param slot index of slot to use. Must be less then {@link #NUM_SLOTS}
   */
  void save(int slot) {
    assert slot < NUM_SLOTS;
    slots[slot] = newGameState(gameState);
    slotsTimes[slot] = System.currentTimeMillis();
    Preferences prefs = Gdx.app.getPreferences(SAVE_PREF_NAME);
    // enableComplexMapKeySerialization needed to properly serialize TilePoint key in
    // playerUnitPlacements
    Gson gson = gsonBuilder.create();
    prefs.putString(SAVE_SLOT_NAME + slot, gson.toJson(gameState));
    prefs.putLong(SAVE_SLOT_TIME_NAME + slot, slotsTimes[slot]);
    prefs.flush();
  }

  /**
   * Replace active game data with data in save slot
   *
   * @param slot index of slot to use. Must be less then {@link #NUM_SLOTS}
   */
  public void load(int slot) {
    assert slot < NUM_SLOTS;
    if (slots[slot] != null) {
      gameState = newGameState(slots[slot]);
    }
  }
}
