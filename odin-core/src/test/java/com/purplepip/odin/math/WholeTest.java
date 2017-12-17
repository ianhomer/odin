package com.purplepip.odin.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

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
public class WholeTest {
  @Test
  public void testValueOf() {
    assertEquals(Wholes.MINUS_ONE, Whole.valueOf(-1));
    assertEquals(Wholes.ZERO, Whole.valueOf(0));
  }

  @Test
  public void testPlus() {
    assertEquals(Rational.valueOf(5,2), Wholes.TWO.plus(Rationals.HALF));
  }

  @Test
  public void testFloor() {
    assertEquals(3, Whole.valueOf(3).floor());
    assertEquals(2, Real.valueOf(2.1).floor());
  }

  @Test
  public void testNextFloor() {
    assertEquals(4, Whole.valueOf(3).nextFloor());
    assertEquals(3, Real.valueOf(2.1).nextFloor());
  }


  @Test
  public void testModulo() {
    assertEquals(Whole.valueOf(2), Whole.valueOf(10).modulo(Whole.valueOf(4)));
    assertEquals(Whole.valueOf(0), Whole.valueOf(10).modulo(Whole.valueOf(1)));
  }

  @Test
  public void testFloorWithRadix() {
    assertEquals(Whole.valueOf(8), Whole.valueOf(10).floor(Whole.valueOf(4)));
  }

  @Test
  public void testEquals() {
    assertEquals(Whole.valueOf(1), new Whole(1));
  }

  @Test
  public void testToString() {
    assertEquals("-1", new Whole(-1).toString());
  }

  @Test
  public void testIsNegative() {
    assertFalse(Whole.valueOf(1).isNegative());
    assertFalse(Whole.valueOf(0).isNegative());
    assertTrue(Whole.valueOf(-1).isNegative());
  }

  @Test
  public void testNegative() {
    assertEquals(Whole.valueOf(1), Whole.valueOf(-1).negative());
    assertEquals(Whole.valueOf(-1), Whole.valueOf(1).negative());
    assertEquals(Wholes.ZERO, Wholes.ZERO);
  }

  @Test
  public void testAbsolute() {
    assertEquals(Whole.valueOf(1), Whole.valueOf(1).absolute());
    assertEquals(Whole.valueOf(1), Whole.valueOf(-1).absolute());
    assertEquals(Whole.valueOf(0), Whole.valueOf(0).absolute());
  }

  @Test
  public void testCeiling() {
    assertEquals(10, Whole.valueOf(10).ceiling());
    assertEquals(-13, Whole.valueOf(-13).ceiling());
  }


}