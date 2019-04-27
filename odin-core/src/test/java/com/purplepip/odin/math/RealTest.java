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

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
import org.junit.jupiter.api.Test;

class RealTest {
  @Test
  void testValueOf() {
    assertEquals(Wholes.ZERO, Wholes.valueOf(0));
    try (LogCaptor captor = new LogCapture().warn().from(Reals.class).start()) {
      assertEquals(Wholes.ZERO, Reals.valueOf(0));
      /*
       * We should have received a warn message for using the Real static not the Whole static
       */
      assertEquals("Warn message count not correct", captor.size(), 1);
    }
  }

  @Test
  void testValueOfString() {
    assertEquals(Wholes.ZERO, Reals.valueOf("0"));
    assertEquals(Reals.valueOf(0.2), Reals.valueOf("0.2"));
  }


  @Test
  void testPlus() {
    assertEquals(Wholes.TWO, Wholes.valueOf(1).plus(Wholes.valueOf(1)));
    assertEquals(Reals.valueOf(2.2), Reals.valueOf(1.1).plus(Reals.valueOf(1.1)));
    assertEquals(Reals.valueOf(1.6), Reals.valueOf(1.1).plus(Rationals.HALF));
    assertEquals(Reals.valueOf(2.1), Reals.valueOf(1.1).plus(Wholes.ONE));
    assertEquals(Reals.valueOf(2.1), Wholes.ONE.plus(Reals.valueOf(1.1)));
  }

  @Test
  void testMinus() {
    assertEquals(Reals.valueOf(1.1).getValue(),
        Reals.valueOf(2.1).minus(Wholes.ONE).getValue(), Reals.DOUBLE_PRECISION);
    assertEquals(1.6,
        Reals.valueOf(2.1).minus(Rationals.HALF).getValue(), Reals.DOUBLE_PRECISION);
    assertEquals(1.2,
        Reals.valueOf(2.3).minus(Reals.valueOf(1.1)).getValue(),
        Reals.DOUBLE_PRECISION);
  }

  @Test
  void testFloor() {
    assertEquals(10, Reals.valueOf(10.4).floor());
    assertEquals(-11, Reals.valueOf(-10.4).floor());
  }

  @Test
  void testFloorToWhole() {
    assertEquals(Wholes.valueOf(10), Reals.valueOf(10.4).wholeFloor());
    assertEquals(Wholes.valueOf(-11), Reals.valueOf(-10.4).wholeFloor());
  }

  @Test
  void testNextFloor() {
    assertEquals(11, Reals.valueOf(10.4).nextFloor());
    assertEquals(-10, Reals.valueOf(-10.4).nextFloor());
  }

  @Test
  void testNextWholeFloor() {
    assertEquals(Wholes.valueOf(11), Reals.valueOf(10.4).nextWholeFloor());
    assertEquals(Wholes.valueOf(-10), Reals.valueOf(-10.4).nextWholeFloor());
  }

  @Test
  void testFloorToWholeRadix() {
    assertEquals(Wholes.valueOf(8), Reals.valueOf(9.3).floor(Wholes.valueOf(4)));
  }

  @Test
  void testFloorToRationalRadix() {
    assertEquals(Rationals.HALF, Reals.valueOf(0.7).floor(Rationals.HALF));
  }

  @Test
  void testFloorToRealRadix() {
    assertEquals(Reals.valueOf(8.2), Reals.valueOf(9.3).floor(Reals.valueOf(4.1)));
  }

  @Test
  void testCeiling() {
    assertEquals(10, Reals.valueOf(10.0).ceiling());
    assertEquals(11, Reals.valueOf(10.4).ceiling());
    assertEquals(-12, Reals.valueOf(-12.0).ceiling());
    assertEquals(-12, Reals.valueOf(-12.4).ceiling());
  }


  @Test
  void testIsNegative() {
    assertTrue(Reals.valueOf(-0.2).isNegative());
    assertFalse(Reals.valueOf(0.2).isNegative());
  }

  @Test
  void testNegative() {
    assertEquals(Reals.valueOf(-0.2), Reals.valueOf(0.2).negative());
    assertEquals(Reals.valueOf(0.2), Reals.valueOf(-0.2).negative());
  }

  @Test
  void testAbsolute() {
    assertEquals(Reals.valueOf(0.2), Reals.valueOf(-0.2).absolute());
    assertEquals(Reals.valueOf(0.2), Reals.valueOf(0.2).absolute());
  }

  @Test
  void testIsZero() {
    assertTrue(new ConcreteReal(0).isZero());
    assertFalse(Reals.valueOf(1.1).isZero());
  }

  @Test
  void testToString() {
    assertEquals("0.1", Reals.valueOf(0.1).toString());
    assertEquals("-1.0", new ConcreteReal(-1).toString());
    assertEquals("1.0", new ConcreteReal(1).toString());
    assertEquals("0.0", new ConcreteReal(0).toString());
  }
}