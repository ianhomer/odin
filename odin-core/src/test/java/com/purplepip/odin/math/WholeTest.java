package com.purplepip.odin.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
@Slf4j
@RunWith(Parameterized.class)
public class WholeTest {
  private Function<Integer, Whole> newWhole;

  /**
   * Parameters for test.
   *
   * @return parameters
   */
  @Parameterized.Parameters
  public static Iterable<WholeTestParameter> parameters() {
    Collection<WholeTestParameter> parameters = new ArrayList<>();
    parameters.add(newParameter(value -> Wholes.valueOf(value.longValue())));
    parameters.add(newParameter(value -> Wholes.mutableOf(value.longValue())));
    return parameters;
  }

  public WholeTest(WholeTestParameter parameter) {
    newWhole = parameter.factory;
  }

  @Test
  public void testValueOf() {
    assertEquals(Wholes.MINUS_ONE, newWhole.apply(-1).asImmutable());
    assertEquals(Wholes.ZERO, newWhole.apply(0).asImmutable());
  }

  @Test
  public void testPlus() {
    assertEquals(Rationals.valueOf(5,2),
        newWhole.apply(2).plus(Rationals.HALF).asImmutable());
    assertEquals(Wholes.THREE, newWhole.apply(2).plus(Wholes.ONE).asImmutable());
    assertEquals(Reals.valueOf(5.3), newWhole.apply(1).plus(Reals.valueOf(4.3)).asImmutable());
  }

  @Test
  public void testFloor() {
    assertEquals(3, newWhole.apply(3).floor());
    assertEquals(2, Reals.valueOf(2.1).floor());
  }

  @Test
  public void testNextFloor() {
    assertEquals(4, newWhole.apply(3).nextFloor());
    assertEquals(3, Reals.valueOf(2.1).nextFloor());
  }

  @Test
  public void testModulo() {
    assertEquals(Wholes.valueOf(2), newWhole.apply(10).modulo(Wholes.valueOf(4)).asImmutable());
    assertEquals(Wholes.valueOf(0), newWhole.apply(10).modulo(Wholes.valueOf(1)).asImmutable());
  }

  @Test
  public void testFloorWithRadix() {
    assertEquals(Wholes.valueOf(8), newWhole.apply(10).floor(Wholes.valueOf(4)).asImmutable());
  }

  @Test
  public void testEquals() {
    assertEquals(Wholes.ONE, newWhole.apply(1).asImmutable());
  }

  @Test
  public void testToString() {
    assertEquals("-1", newWhole.apply(-1).toString());
  }

  @Test
  public void testIsNegative() {
    assertFalse(newWhole.apply(1).isNegative());
    assertFalse(newWhole.apply(0).isNegative());
    assertTrue(newWhole.apply(-1).isNegative());
  }

  @Test
  public void testIsZero() {
    assertFalse(newWhole.apply(1).isZero());
    assertTrue(newWhole.apply(0).isZero());
  }

  @Test
  public void testNegative() {
    assertEquals(Wholes.valueOf(1), newWhole.apply(-1).negative().asImmutable());
    assertEquals(Wholes.valueOf(-1), newWhole.apply(1).negative().asImmutable());
    assertEquals(Wholes.ZERO, newWhole.apply(0).asImmutable());
  }

  @Test
  public void testAbsolute() {
    assertEquals(Wholes.valueOf(1), newWhole.apply(1).absolute().asImmutable());
    assertEquals(Wholes.valueOf(1), newWhole.apply(-1).absolute().asImmutable());
    assertEquals(Wholes.valueOf(0), newWhole.apply(0).absolute().asImmutable());
  }

  @Test
  public void testCeiling() {
    assertEquals(10, newWhole.apply(10).ceiling());
    assertEquals(-13, newWhole.apply(-13).ceiling());
  }

  private static WholeTestParameter newParameter(Function<Integer, Whole> factory) {
    return new WholeTestParameter().factory(factory);
  }

  @Accessors(fluent = true)
  private static class WholeTestParameter {
    @Getter
    @Setter
    private Function<Integer, Whole> factory;
  }
}