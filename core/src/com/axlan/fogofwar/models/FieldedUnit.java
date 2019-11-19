package com.axlan.fogofwar.models;

/**
 * A unit on the battlefield
 *
 * @see com.axlan.fogofwar.screens.BattleView
 */
public class FieldedUnit implements Cloneable {

  /**
   * Unit type identifier
   */
  public final String type;
  /**
   * The state of the unit in the current turn
   */
  public State state;
  /**
   * Current HP for the unit
   */
  public int currentHealth;

  /**
   * @param type Unit type identifier
   */
  FieldedUnit(String type) {
    this.type = type;
    this.currentHealth = getStats().maxHealth;
    state = State.IDLE;
  }

  FieldedUnit(FieldedUnit other) {
    this.type = other.type;
    this.currentHealth = other.currentHealth;
    state = other.state;
  }

  /**
   * Get the stats for this unit
   *
   * @return unit stats
   */
  public UnitStats getStats() {
    return LoadedResources.getUnitStats().get(type);
  }

  /**
   * Deal damage to specified unit
   * @param opponent unit to damage
   */
  public void fight(FieldedUnit opponent) {
    opponent.currentHealth -= getStats().attack;
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
