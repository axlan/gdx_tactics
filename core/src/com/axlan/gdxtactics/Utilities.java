package com.axlan.gdxtactics;

import com.badlogic.gdx.utils.Array;
import java.util.List;
import java.util.Random;

/**
 * Class for general utility functions
 */
public class Utilities {

  private Utilities() {
    //this prevents even the native class from
    //calling this ctor as well :
    throw new AssertionError();
  }

  /**
   * Randomly select n different numbers from 0 to (length-1) . Use to pick elements from a list.
   *
   * @param rand   Random number generator
   * @param n      Number of elements to choose. length >= n
   * @param length Length of list to get indexes for. length >= n
   * @return n different numbers from 0 to (length-1)
   */
  public static Array<Integer> getNElements(Random rand, int n, int length) {
    assert (length >= n);
    Array<Integer> ret = new Array<>();
    for (int i = 0; i < n; i++) {
      int val;
      do {
        val = rand.nextInt(length);
      } while (ret.contains(val, false));
      ret.add(val);
    }
    return ret;
  }

  /**
   * Return the last item in a list.  Exception if empty.
   *
   * @param list list to get tail from
   * @param <T>  type of items in list (can be implicit)
   * @return the last element in list
   */
  public static <T> T listGetTail(List<T> list) {
    return list.get(list.size() - 1);
  }

  /**
   * Get an item from a list of lists
   *
   * @param list 2d list to get item in
   * @param x    first index
   * @param y    second index
   * @param <T>  type of items in list (can be implicit)
   * @return item at x,y
   */
  public static <T> T listGet2d(List<List<T>> list, int x, int y) {
    return list.get(x).get(y);
  }

  /**
   * Generate an Array [start, start + inc, start + inc *2, ... ] ending with the greatest value
   * less then end
   *
   * @param start first value in Array
   * @param end   Last value will be the last greates value in the sequence still less then end
   * @param inc   Increment each value in the sequence by inc
   * @return Array of values in range
   */
  public static Array<Integer> getIntRange(int start, int end, int inc) {
    Integer[] ret = new Integer[(end - start) / inc];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = start + inc * i;
    }
    return new Array<>(ret);
  }
}
