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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Reals {
  public static final double DOUBLE_PRECISION = 0.00000000000001;

  private Reals() {
  }

  /**
   * Create Whole number.  Please use the Whole.valueOf method.  This only exists
   * to prevent auto use of the valueOf with a double argument when an long is passed in.
   *
   * @param integer integer to create the whole number from
   * @return whole number
   */
  public static Whole valueOf(long integer) {
    StackTraceElement stackTraceElement = new Exception().getStackTrace()[1];
    LOG.warn("Please call 'Whole.valueOf({})' not 'Real.valueOf({})' "
        + "in {} @ line {} "
        + "given you know at compile time you what a whole number", integer, integer,
        stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
    return Wholes.valueOf(integer);
  }

  /**
   * Create a real number from the given double.  This will ALWAYS return a real number using a
   * double underlying value.  This ensure that once a logic pathway chooses to go to double
   * arithmetic it will stay with double arithmetic.
   *
   *
   * @param value double value
   * @return real value
   */
  public static Real valueOf(double value) {
    return new ConcreteReal(value);
  }

  /**
   * Calculate real value from string.
   *
   * @param value real as string
   * @return real value
   */
  public static Real valueOf(String value) {
    if (value == null || value.indexOf('.') < 0) {
      return Rationals.valueOf(value);
    }
    return valueOf(Double.valueOf(value));
  }

  static double floor(double value, double radix) {
    return value - (value % radix);
  }
}
