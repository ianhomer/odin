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
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.purplepip.odin.common.OdinRuntimeException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class RationalTest {
  @Test
  void testValueOf() {
    assertEquals(1, Rationals.valueOf(1, 1, true).getNumerator());
    assertEquals(1, Rationals.valueOf(2, 2, true).getNumerator());
    assertEquals(1, Rationals.valueOf(2, 2).getNumerator());
    assertEquals(3, Rationals.valueOf(3, 2).getNumerator());
    assertEquals(3, Rationals.valueOf(6, 4).getNumerator());
    assertEquals(3, Rationals.valueOf(6, 4, true).getNumerator());
    assertEquals(6, Rationals.valueOf(6, 4, false).getNumerator());
  }

  @Test
  void testValue() {
    assertEquals(0.5, Rationals.valueOf(1,2).getValue(), 0.01);
    assertEquals(-0.5, Rationals.valueOf(-1,2).getValue(), 0.01);
    assertEquals(3.333, Rationals.valueOf(10,3).getValue(), 0.001);
  }


  @Test
  void testToString() {
    assertEquals("1", Rationals.valueOf(1,1).toString());
    assertEquals("1", Rationals.valueOf(1,1, false).toString());
    assertEquals("1", Rationals.valueOf(1,1, true).toString());
    assertEquals("1", Rationals.valueOf(2,2).toString());
    assertEquals("2/2", Rationals.valueOf(2,2, false).toString());
    assertEquals("½", Rationals.valueOf(1,2).toString());
    assertEquals("1½", Rationals.valueOf(3,2, true).toString());
    assertEquals("1½", Rationals.valueOf(3,2).toString());
    assertEquals("1⅔", Rationals.valueOf(5,3).toString());
    assertEquals("1⅔", Rationals.valueOf(5,3, true).toString());
    assertEquals("½", Rationals.valueOf(2,4).toString());
    assertEquals("½", Rationals.valueOf(2,4, true).toString());
    assertEquals("2¼", Rationals.valueOf(9,4).toString());
    assertEquals("2¼", Rationals.valueOf(9,4, true).toString());
    assertEquals("1+3/7", Rationals.valueOf(10,7).toString());
    assertEquals("1+3/7", Rationals.valueOf(10,7, true).toString());
    assertEquals("-½", Rationals.valueOf(-2,4).toString());
    assertEquals("-½", Rationals.valueOf(-2,4, true).toString());
    assertEquals("½", Rationals.valueOf(-2,-4).toString());
    assertEquals("2/4", Rationals.valueOf(-2,-4, false).toString());
    assertEquals("½", Rationals.valueOf(-2,-4, true).toString());
    assertEquals("-½", Rationals.valueOf(1,-2).toString());
    assertEquals("-½", Rationals.valueOf(2,-4).toString());
    assertEquals("-1", Rationals.valueOf(-1,1, false).toString());
    assertEquals("-1", Rationals.valueOf(-1,1).toString());
  }

  @Test
  void testPlus() {
    assertEquals("1½", Rationals.valueOf(1,1, true)
        .plus(Rationals.valueOf(1,2)).toString());
    assertEquals("1½", Rationals.valueOf(1,1)
        .plus(Rationals.valueOf(1,2)).toString());
    assertEquals("7/12", Rationals.valueOf(1,3)
        .plus(Rationals.valueOf(1,4)).toString());
    assertEquals("½", Wholes.ZERO
        .plus(Rationals.valueOf(1,2)).toString());
  }

  @Test
  void testMinus() {
    assertEquals("½", Rationals.valueOf(1,1)
        .minus(Rationals.valueOf(1,2)).toString());
    assertEquals("1/12", Rationals.valueOf(1,3)
        .minus(Rationals.valueOf(1,4)).toString());
  }

  @Test
  void testTimes() {
    assertEquals("1", Rationals.valueOf(2,1, true)
        .times(Rationals.valueOf(1,2)).toString());
    assertEquals("1", Rationals.valueOf(2,1)
        .times(Rationals.valueOf(1,2)).toString());
    assertEquals("1/12", Rationals.valueOf(1,3)
        .times(Rationals.valueOf(1,4)).toString());
  }

  @Test
  void testGe() {
    assertTrue(Rationals.valueOf(2, 1).ge(Rationals.valueOf(2, 1)));
    assertTrue(Rationals.valueOf(2, 1).ge(Rationals.valueOf(4, 2)));
  }

  @Test
  void testGt() {
    assertFalse(Rationals.valueOf(2, 1).gt(Rationals.valueOf(2, 1)));
    assertFalse(Rationals.valueOf(-2, 5).gt(Wholes.valueOf(0)));
    assertTrue(Rationals.valueOf(3, 1).gt(Rationals.valueOf(4, 2)));
  }

  @Test
  void testLt() {
    assertFalse(Rationals.valueOf(2, 1).lt(Rationals.valueOf(2, 1)));
    assertTrue(Rationals.valueOf(1, 1).lt(Rationals.valueOf(4, 2)));
  }

  @Test
  void testModulo() {
    assertEquals("0", Rationals.valueOf(3, 1)
        .modulo(Rationals.valueOf(1, 1)).toString());
    assertEquals("1", Rationals.valueOf(3, 1)
        .modulo(Rationals.valueOf(2, 1)).toString());
    assertEquals("0", Rationals.valueOf(4, 1)
        .modulo(Rationals.valueOf(2, 1)).toString());
    assertEquals("½", Rationals.valueOf(3,2)
        .modulo(Rationals.valueOf(1, 1)).toString());
  }

  @Test
  void testFloor() {
    assertEquals(0, Rationals.valueOf(1,2).floor());
    assertEquals(3, Rationals.valueOf(10,3).floor());
  }

  @Test
  void testFloorToRadix() {
    assertEquals("2", Rationals.valueOf(3, 1)
        .floor(Rationals.valueOf(2, 1)).toString());
    assertEquals("1½", Rationals.valueOf(2, 1, true)
        .floor(Rationals.valueOf(3, 2)).toString());
    assertEquals("3", Rationals.valueOf(4, 1, true)
        .floor(Rationals.valueOf(3, 2)).toString());

    assertEquals("2.0", Rationals.valueOf(3, 1)
        .floor(Reals.valueOf(2.0)).toString());
    assertEquals("2", Rationals.valueOf(3, 1)
        .floor(Rationals.valueOf("2")).toString());
  }

  @Test
  void testNextFloor() {
    assertEquals(1, Rationals.valueOf(1,2).nextFloor());
    assertEquals(4, Rationals.valueOf(10,3).nextFloor());
  }

  @Test
  void testEgyptianFractions() {
    assertEquals("1+⅓", getEgyptianFractionsAsString(4,3));
    assertEquals("1+⅓", getEgyptianFractionsAsString(4,3, 1));
    assertEquals("⅕+⅕", getEgyptianFractionsAsString(2, 5));
    assertEquals("⅕+⅕", getEgyptianFractionsAsString(2, 5,1));
    assertEquals("1+½+¼", getEgyptianFractionsAsString(7, 4, 1));
    assertEquals("2+1+½+¼", getEgyptianFractionsAsString(15, 4, 2));
    assertEquals("3+½+¼", getEgyptianFractionsAsString(15, 4, 4));
    assertEquals("4+3+½+¼", getEgyptianFractionsAsString(31, 4, 4));
    assertEquals("-1", getEgyptianFractionsAsString(-1, 1, 1));
    assertEquals("-1-½-¼", getEgyptianFractionsAsString(-7, 4, 1));
  }

  @Test
  void testEgyptianFractionsOverflow() {
    assertThrows(OdinRuntimeException.class, () ->
        getEgyptianFractionsAsString(21, 1, 1)
    );
  }

  private String getEgyptianFractionsAsString(long numerator, long denominator) {
    Rational rational = Rationals.valueOf(numerator, denominator, true);
    return join(rational.getEgyptianFractions(), rational.isNegative());
  }

  private String getEgyptianFractionsAsString(
      long numerator, long denominator, int maxIntegerPart) {
    Rational rational = Rationals.valueOf(numerator, denominator, true);
    return join(rational.getEgyptianFractions(maxIntegerPart), rational.isNegative());
  }

  private String join(Stream<Rational> parts, boolean isNegative) {
    String delimiter = isNegative ? "" : "+";
    return parts.map(Rational::toString)
        .collect(Collectors.joining(delimiter));
  }

  @Test
  void testIsNegative() {
    assertTrue(Rationals.valueOf(-1,2).isNegative());
    assertTrue(Rationals.valueOf(1,-2).isNegative());
    assertFalse(Rationals.valueOf(1,2).isNegative());
    assertFalse(Rationals.valueOf(-1,-2).isNegative());
  }

  @Test
  void testNegative() {
    assertEquals(Rationals.valueOf(1, 2), Rationals.valueOf(-1,2).negative());
    assertEquals(Rationals.valueOf(1, 2), Rationals.valueOf(1,-2).negative());
    assertEquals(Rationals.valueOf(1, 2), Rationals.valueOf(1,-2, false).negative());
    assertEquals(Rationals.valueOf(-1, 2), Rationals.valueOf(1,2).negative());
  }

  @Test
  void testAbsolute() {
    assertEquals(Rationals.valueOf(1,2), Rationals.valueOf(1,2).absolute());
    assertEquals(Rationals.valueOf(1,2), Rationals.valueOf(-1,2).absolute());
    assertEquals(Rationals.valueOf(1,2), Rationals.valueOf(1,-2).absolute());
    assertEquals(Rationals.valueOf(1,2), Rationals.valueOf(-1,-2).absolute());
  }

  @Test
  void testZeroDenominator() {
    assertThrows(OdinRuntimeException.class, () ->
        Rationals.valueOf(1,0)
    );
  }

  @Test
  void testValueOfString() {
    assertEquals(Wholes.ONE, Rationals.valueOf("1"));
    assertEquals(Rationals.HALF, Rationals.valueOf("1/2"));
    assertEquals(Rationals.HALF, Rationals.valueOf("1 / 2"));
    assertEquals(Wholes.ZERO, Rationals.valueOf(null));
    assertEquals(Wholes.ZERO, Rationals.valueOf(""));
  }

  @Test
  void testValueOfStringFail() {
    assertThrows(OdinRuntimeException.class, () ->
        assertEquals(Wholes.ZERO, Rationals.valueOf("/2"))
    );
  }

  @Test
  void testCeiling() {
    assertEquals(1, Rationals.HALF.ceiling());
    assertEquals(0, Rationals.HALF.negative().ceiling());
    assertEquals(2, Rationals.FOUR_THIRDS.ceiling());
    assertEquals(-1, Rationals.FOUR_THIRDS.negative().ceiling());
  }
}