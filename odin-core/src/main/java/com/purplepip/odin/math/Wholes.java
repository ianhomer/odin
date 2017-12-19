/*
 * Copyright (c) 2017 the original author or authors. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.math;

/**
 * Whole numbers.
 */
public final class Wholes {
  private static final int LOW_CACHE = -1;
  private static final int HIGH_CACHE = 10;
  private static final Whole[] WHOLES_CACHE;

  static {
    WHOLES_CACHE = new Whole[1 + Wholes.HIGH_CACHE - Wholes.LOW_CACHE];
    for (int i = Wholes.LOW_CACHE; i <= Wholes.HIGH_CACHE ; i++) {
      Wholes.WHOLES_CACHE[i - Wholes.LOW_CACHE] = new Whole(i);
    }
  }

  public static final Whole MINUS_ONE = valueOf(-1);
  public static final Whole ZERO = valueOf(0);
  public static final Whole ONE = valueOf(1);
  public static final Whole TWO = valueOf(2);
  public static final Whole THREE = valueOf(3);
  public static final Whole FOUR = valueOf(4);
  public static final Whole FIVE = valueOf(5);
  public static final Whole SIX = valueOf(6);
  public static final Whole SEVEN = valueOf(7);
  public static final Whole EIGHT = valueOf(8);
  public static final Whole NINE = valueOf(9);

  private Wholes() {
  }

  /**
   * Return whole for the given integer.
   *
   * @param integer integer value
   * @return whole object
   */
  public static Whole valueOf(long integer) {
    if (integer >= LOW_CACHE && integer <= HIGH_CACHE) {
      return WHOLES_CACHE[(int) integer - LOW_CACHE];
    }
    return new Whole(integer);
  }
}
