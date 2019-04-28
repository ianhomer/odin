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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Function;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
class WholeTest {
  /**
   * Parameters for test.
   *
   * @return parameters
   */
  static Stream<Function<Integer, Whole>> parameters() {
    return Stream.of(
        (value) -> Wholes.valueOf(value.longValue()),
        (value) -> Wholes.mutableOf(value.longValue())
    );
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testValueOf(Function<Integer, Whole> newWhole) {
    assertEquals(Wholes.MINUS_ONE, newWhole.apply(-1).asImmutable());
    assertEquals(Wholes.ZERO, newWhole.apply(0).asImmutable());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testPlus(Function<Integer, Whole> newWhole) {
    assertEquals(Rationals.valueOf(5,2),
        newWhole.apply(2).plus(Rationals.HALF).asImmutable());
    assertEquals(Wholes.THREE, newWhole.apply(2).plus(Wholes.ONE).asImmutable());
    assertEquals(Reals.valueOf(5.3), newWhole.apply(1).plus(Reals.valueOf(4.3)).asImmutable());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testMinus(Function<Integer, Whole> newWhole) {
    assertEquals(newWhole.apply(1), newWhole.apply(2).minus(newWhole.apply(1)));
    assertEquals(Reals.valueOf(0.9).getValue(),
        newWhole.apply(2).minus(Reals.valueOf(1.1)).getValue(), Reals.DOUBLE_PRECISION);
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testFloor(Function<Integer, Whole> newWhole) {
    assertEquals(3, newWhole.apply(3).floor());
    assertEquals(2, Reals.valueOf(2.1).floor());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testNextFloor(Function<Integer, Whole> newWhole) {
    assertEquals(4, newWhole.apply(3).nextFloor());
    assertEquals(3, Reals.valueOf(2.1).nextFloor());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testModulo(Function<Integer, Whole> newWhole) {
    assertEquals(Wholes.valueOf(2), newWhole.apply(10).modulo(Wholes.valueOf(4)).asImmutable());
    assertEquals(Wholes.valueOf(0), newWhole.apply(10).modulo(Wholes.valueOf(1)).asImmutable());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testFloorWithRadix(Function<Integer, Whole> newWhole) {
    assertEquals(Wholes.valueOf(8), newWhole.apply(10).floor(Wholes.valueOf(4)).asImmutable());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  public void testEquals(Function<Integer, Whole> newWhole) {
    assertEquals(Wholes.ONE, newWhole.apply(1).asImmutable());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testToString(Function<Integer, Whole> newWhole) {
    assertEquals("-1", newWhole.apply(-1).toString());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testIsNegative(Function<Integer, Whole> newWhole) {
    assertFalse(newWhole.apply(1).isNegative());
    assertFalse(newWhole.apply(0).isNegative());
    assertTrue(newWhole.apply(-1).isNegative());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testIsZero(Function<Integer, Whole> newWhole) {
    assertFalse(newWhole.apply(1).isZero());
    assertTrue(newWhole.apply(0).isZero());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testNegative(Function<Integer, Whole> newWhole) {
    assertEquals(Wholes.valueOf(1), newWhole.apply(-1).negative().asImmutable());
    assertEquals(Wholes.valueOf(-1), newWhole.apply(1).negative().asImmutable());
    assertEquals(Wholes.ZERO, newWhole.apply(0).asImmutable());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  public void testAbsolute(Function<Integer, Whole> newWhole) {
    assertEquals(Wholes.valueOf(1), newWhole.apply(1).absolute().asImmutable());
    assertEquals(Wholes.valueOf(1), newWhole.apply(-1).absolute().asImmutable());
    assertEquals(Wholes.valueOf(0), newWhole.apply(0).absolute().asImmutable());
  }

  @ParameterizedTest
  @MethodSource("parameters")
  void testCeiling(Function<Integer, Whole> newWhole) {
    assertEquals(10, newWhole.apply(10).ceiling());
    assertEquals(-13, newWhole.apply(-13).ceiling());
  }
}