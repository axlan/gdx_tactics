package com.axlan.fogofwar.screens;

/**
 * Interface for a callback that makes a selection
 */
public interface TitleSelectionObserver {

  void onDone(TitleSelection selection);

  enum TitleSelection {
    NEW_GAME,
    LOAD_GAME,
    SETTINGS,
    QUIT
  }
}
