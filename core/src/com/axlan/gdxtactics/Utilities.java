package com.axlan.gdxtactics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

  /**
   * Create a new color that's the same as an old one, but with a different alpha value
   *
   * @param color original color
   * @param alpha new alpha
   * @return new color with new alpha
   */
  public static Color getTransparentColor(Color color, float alpha) {
    Color newColor = new Color(color);
    newColor.a = alpha;
    return newColor;
  }

  /**
   * Method to generate buttons that map to the possible values of an enum type.
   * <p>
   * Names convert from Upper snake case to capitalized spaced words.
   *
   * @param enumList Result of EnumType.values()
   * @param <T>      Class of Enum
   * @return mapping of enums to buttons with text corresponding to the enums names
   */
  public static <T> Map<T, VisTextButton> enumToButtons(T[] enumList) {
    LinkedHashMap<T, VisTextButton> buttons = new LinkedHashMap<>();
    for (T val : enumList) {
      Enum enumVal = (Enum) val;
      String[] words = enumVal.name().toLowerCase().split("_");
      StringBuilder name = new StringBuilder();
      for (String word : words) {
        name.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
        //noinspection StringEquality
        if (word != words[words.length - 1]) {
          name.append(' ');
        }
      }
      //noinspection unchecked
      buttons.put((T) enumVal, new VisTextButton(name.toString()));
    }
    return buttons;
  }


}
