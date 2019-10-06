package com.axlan.fogofwar.models;

/**
 * A unit on the battlefield
 *
 * @see com.axlan.fogofwar.screens.BattleView
 */
public class FieldedUnit {

  /**
   * Stats for the unit
   */
  public final UnitStats stats;
  /**
   * The state of the unit in the current turn
   */
  public State state;
  /**
   * Current HP for the unit
   */
  public int currentHealth;

  /**
   * @param stats Stats for the unit
   */
  public FieldedUnit(UnitStats stats) {
    this.stats = stats;
    this.currentHealth = stats.maxHealth;
    state = State.IDLE;
  }

  public void fight(FieldedUnit opponent) {
    opponent.currentHealth -= stats.attack;
  }

  /**
   * The state of the unit in the current turn
   */
  public enum State {
    /** Unit hasn't been used */
    IDLE,
    /** Unit is currently selected */
    SELECTED,
    /** Unit is showing movement animation */
    MOVING,
    /** Unit is tapped for turn */
    DONE
  }


}
