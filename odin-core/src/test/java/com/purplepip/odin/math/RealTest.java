package com.purplepip.odin.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.purplepip.logcapture.LogCaptor;
import com.purplepip.logcapture.LogCapture;
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
public class RealTest {
  @Test
  public void testValueOf() {
    assertEquals(Wholes.ZERO, Whole.valueOf(0));
    try (LogCaptor captor = new LogCapture().warn().from(Real.class).start()) {
      assertEquals(Wholes.ZERO, Real.valueOf(0));
      /*
       * We should have received a warn message for using the Real static not the Whole static
       */
      assertEquals(captor.size(), 1);
    }
  }

  @Test
  public void testValueOfString() {
    assertEquals(Wholes.ZERO, Real.valueOf("0"));
    assertEquals(Real.valueOf(0.2), Real.valueOf("0.2"));
  }


  @Test
  public void testPlus() {
    assertEquals(Wholes.TWO, Whole.valueOf(1).plus(Whole.valueOf(1)));
    assertEquals(Real.valueOf(2.1), Real.valueOf(1.1).plus(Whole.valueOf(1)));
    assertEquals(Real.valueOf(2.1), Whole.valueOf(1).plus(Real.valueOf(1.1)));
  }

  @Test
  public void testFloor() {
    assertEquals(10, Real.valueOf(10.4).floor());
    assertEquals(-11, Real.valueOf(-10.4).floor());
  }

  @Test
  public void testFloorToWhole() {
    assertEquals(Whole.valueOf(10), Real.valueOf(10.4).wholeFloor());
    assertEquals(Whole.valueOf(-11), Real.valueOf(-10.4).wholeFloor());
  }

  @Test
  public void testNextFloor() {
    assertEquals(11, Real.valueOf(10.4).nextFloor());
    assertEquals(-10, Real.valueOf(-10.4).nextFloor());
  }

  @Test
  public void testNextWholeFloor() {
    assertEquals(Whole.valueOf(11), Real.valueOf(10.4).nextWholeFloor());
    assertEquals(Whole.valueOf(-10), Real.valueOf(-10.4).nextWholeFloor());
  }

  @Test
  public void testFloorToWholeRadix() {
    assertEquals(Whole.valueOf(8), Real.valueOf(9.3).floor(Whole.valueOf(4)));
  }

  @Test
  public void testFloorToRationalRadix() {
    assertEquals(Rationals.HALF, Real.valueOf(0.7).floor(Rationals.HALF));
  }

  @Test
  public void testFloorToRealRadix() {
    assertEquals(Real.valueOf(8.2), Real.valueOf(9.3).floor(Real.valueOf(4.1)));
  }

  @Test
  public void testCeiling() {
    assertEquals(10, Real.valueOf(10.0).ceiling());
    assertEquals(11, Real.valueOf(10.4).ceiling());
    assertEquals(-12, Real.valueOf(-12.0).ceiling());
    assertEquals(-12, Real.valueOf(-12.4).ceiling());
  }


  @Test
  public void testIsNegative() {
    assertTrue(Real.valueOf(-0.2).isNegative());
    assertFalse(Real.valueOf(0.2).isNegative());
  }

  @Test
  public void testNegative() {
    assertEquals(Real.valueOf(-0.2), Real.valueOf(0.2).negative());
    assertEquals(Real.valueOf(0.2), Real.valueOf(-0.2).negative());
  }

  @Test
  public void testAbsolute() {
    assertEquals(Real.valueOf(0.2), Real.valueOf(-0.2).absolute());
    assertEquals(Real.valueOf(0.2), Real.valueOf(0.2).absolute());
  }

  @Test
  public void testToString() {
    assertEquals("0.1", Real.valueOf(0.1).toString());
    assertEquals("1.0", new Real(1).toString());
    assertEquals("-1.0", new Real(-1).toString());
    assertEquals("0.0", new Real(0).toString());
  }
}