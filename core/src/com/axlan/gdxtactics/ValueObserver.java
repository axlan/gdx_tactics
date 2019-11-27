package com.axlan.gdxtactics;

/**
 * Interface for a callback that takes a String
 */
public interface ValueObserver<T> {
  void processValue(T val);
}
