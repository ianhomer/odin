/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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
 * Rational numbers.
 */
public final class Rationals {
  public static final Rational MINUS_ONE = new Rational(-1);
  public static final Rational ZERO = new Rational(0);
  public static final Rational ONE = new Rational(1);
  public static final Rational TWO = new Rational(2);
  public static final Rational HALF = new Rational(1, 2);
  public static final Rational THIRD = new Rational(1, 3);
  public static final Rational TWO_THIRDS = new Rational(2, 3);
  public static final Rational FOUR_THIRDS = new Rational(4, 3);
  public static final Rational QUARTER = new Rational(1, 4);
  public static final Rational THREE_QUARTERS = new Rational(3, 4);
  public static final Rational EIGTH = new Rational(1, 8);

  private Rationals() {
  }
}
