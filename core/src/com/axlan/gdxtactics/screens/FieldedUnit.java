package com.axlan.gdxtactics.screens;

import com.axlan.gdxtactics.models.UnitStats;

public class FieldedUnit {

  final UnitStats stats;
  State state;
  int currentHealth;

  public FieldedUnit(UnitStats stats) {
    this.stats = stats;
    this.currentHealth = stats.getMaxHealth();
    state = State.IDLE;
  }

  enum State {
    IDLE,
    MOVING,
    DONE
  }


}
