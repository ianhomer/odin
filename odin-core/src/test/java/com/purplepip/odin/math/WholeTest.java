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
    assertEquals(Wholes.MINUS_ONE, Wholes.valueOf(-1));
    assertEquals(Wholes.ZERO, Wholes.valueOf(0));
  }

  @Test
  public void testPlus() {
    assertEquals(Rationals.valueOf(5,2), Wholes.TWO.plus(Rationals.HALF));
  }

  @Test
  public void testFloor() {
    assertEquals(3, Wholes.valueOf(3).floor());
    assertEquals(2, Reals.valueOf(2.1).floor());
  }

  @Test
  public void testNextFloor() {
    assertEquals(4, Wholes.valueOf(3).nextFloor());
    assertEquals(3, Reals.valueOf(2.1).nextFloor());
  }


  @Test
  public void testModulo() {
    assertEquals(Wholes.valueOf(2), Wholes.valueOf(10).modulo(Wholes.valueOf(4)));
    assertEquals(Wholes.valueOf(0), Wholes.valueOf(10).modulo(Wholes.valueOf(1)));
  }

  @Test
  public void testFloorWithRadix() {
    assertEquals(Wholes.valueOf(8), Wholes.valueOf(10).floor(Wholes.valueOf(4)));
  }

  @Test
  public void testEquals() {
    assertEquals(Wholes.valueOf(1), Wholes.ONE);
  }

  @Test
  public void testToString() {
    assertEquals("-1", Wholes.MINUS_ONE.toString());
  }

  @Test
  public void testIsNegative() {
    assertFalse(Wholes.valueOf(1).isNegative());
    assertFalse(Wholes.valueOf(0).isNegative());
    assertTrue(Wholes.valueOf(-1).isNegative());
  }

  @Test
  public void testNegative() {
    assertEquals(Wholes.valueOf(1), Wholes.valueOf(-1).negative());
    assertEquals(Wholes.valueOf(-1), Wholes.valueOf(1).negative());
    assertEquals(Wholes.ZERO, Wholes.ZERO);
  }

  @Test
  public void testAbsolute() {
    assertEquals(Wholes.valueOf(1), Wholes.valueOf(1).absolute());
    assertEquals(Wholes.valueOf(1), Wholes.valueOf(-1).absolute());
    assertEquals(Wholes.valueOf(0), Wholes.valueOf(0).absolute());
  }

  @Test
  public void testCeiling() {
    assertEquals(10, Wholes.valueOf(10).ceiling());
    assertEquals(-13, Wholes.valueOf(-13).ceiling());
  }


}