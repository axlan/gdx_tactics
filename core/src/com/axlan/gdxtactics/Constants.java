package com.axlan.gdxtactics;

/**
 * Collected constants
 *
 * <P>All members of this class are immutable.
 */
public final class Constants {

  //TODO Add concept of multiple levels
  /**
   * File with level description
   */
  public static final String SETTINGS_FILE = "data/settings.json";

  // PRIVATE //

  /**
   * The caller references the constants using <tt>Constants.SETTINGS_FILE</tt>, and so on. Thus,
   * the caller should be prevented from constructing objects of this class, by declaring this
   * private constructor.
   */
  private Constants() {
    //this prevents even the native class from
    //calling this ctor as well :
    throw new AssertionError();
  }
}
