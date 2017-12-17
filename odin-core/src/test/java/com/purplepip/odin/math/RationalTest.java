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

import com.purplepip.odin.common.OdinRuntimeException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class RationalTest {
  @Test
  public void testValueOf() {
    assertEquals(1, Rational.valueOf(1, 1, true).getNumerator());
    assertEquals(1, Rational.valueOf(2, 2, true).getNumerator());
    assertEquals(1, Rational.valueOf(2, 2).getNumerator());
    assertEquals(3, Rational.valueOf(3, 2).getNumerator());
    assertEquals(3, Rational.valueOf(6, 4).getNumerator());
    assertEquals(3, Rational.valueOf(6, 4, true).getNumerator());
    assertEquals(6, Rational.valueOf(6, 4, false).getNumerator());
  }

  @Test
  public void testValue() {
    assertEquals(0.5, Rational.valueOf(1,2).getValue(), 0.01);
    assertEquals(-0.5, Rational.valueOf(-1,2).getValue(), 0.01);
    assertEquals(3.333, Rational.valueOf(10,3).getValue(), 0.001);
  }


  @Test
  public void testToString() {
    assertEquals("1", Rational.valueOf(1,1).toString());
    assertEquals("1", Rational.valueOf(1,1, false).toString());
    assertEquals("1", Rational.valueOf(1,1, true).toString());
    assertEquals("1", Rational.valueOf(2,2).toString());
    assertEquals("2/2", Rational.valueOf(2,2, false).toString());
    assertEquals("½", Rational.valueOf(1,2).toString());
    assertEquals("1½", Rational.valueOf(3,2, true).toString());
    assertEquals("1½", Rational.valueOf(3,2).toString());
    assertEquals("1⅔", Rational.valueOf(5,3).toString());
    assertEquals("1⅔", Rational.valueOf(5,3, true).toString());
    assertEquals("½", Rational.valueOf(2,4).toString());
    assertEquals("½", Rational.valueOf(2,4, true).toString());
    assertEquals("2¼", Rational.valueOf(9,4).toString());
    assertEquals("2¼", Rational.valueOf(9,4, true).toString());
    assertEquals("1+3/7", Rational.valueOf(10,7).toString());
    assertEquals("1+3/7", Rational.valueOf(10,7, true).toString());
    assertEquals("-½", Rational.valueOf(-2,4).toString());
    assertEquals("-½", Rational.valueOf(-2,4, true).toString());
    assertEquals("½", Rational.valueOf(-2,-4).toString());
    assertEquals("2/4", Rational.valueOf(-2,-4, false).toString());
    assertEquals("½", Rational.valueOf(-2,-4, true).toString());
    assertEquals("-½", Rational.valueOf(1,-2).toString());
    assertEquals("-½", Rational.valueOf(2,-4).toString());
    assertEquals("-1", Rational.valueOf(-1,1, false).toString());
    assertEquals("-1", Rational.valueOf(-1,1).toString());
  }

  @Test
  public void testPlus() {
    assertEquals("1½", Rational.valueOf(1,1, true)
        .plus(Rational.valueOf(1,2)).toString());
    assertEquals("1½", Rational.valueOf(1,1)
        .plus(Rational.valueOf(1,2)).toString());
    assertEquals("7/12", Rational.valueOf(1,3)
        .plus(Rational.valueOf(1,4)).toString());
    assertEquals("½", Wholes.ZERO
        .plus(Rational.valueOf(1,2)).toString());
  }

  @Test
  public void testMinus() {
    assertEquals("½", Rational.valueOf(1,1)
        .minus(Rational.valueOf(1,2)).toString());
    assertEquals("1/12", Rational.valueOf(1,3)
        .minus(Rational.valueOf(1,4)).toString());
  }

  @Test
  public void testTimes() {
    assertEquals("1", Rational.valueOf(2,1, true)
        .times(Rational.valueOf(1,2)).toString());
    assertEquals("1", Rational.valueOf(2,1)
        .times(Rational.valueOf(1,2)).toString());
    assertEquals("1/12", Rational.valueOf(1,3)
        .times(Rational.valueOf(1,4)).toString());
  }

  @Test
  public void testGe() {
    assertTrue(Rational.valueOf(2, 1).ge(Rational.valueOf(2, 1)));
    assertTrue(Rational.valueOf(2, 1).ge(Rational.valueOf(4, 2)));
  }

  @Test
  public void testGt() {
    assertFalse(Rational.valueOf(2, 1).gt(Rational.valueOf(2, 1)));
    assertFalse(Rational.valueOf(-2, 5).gt(Rational.valueOf(0)));
    assertTrue(Rational.valueOf(3, 1).gt(Rational.valueOf(4, 2)));
  }

  @Test
  public void testLt() {
    assertFalse(Rational.valueOf(2, 1).lt(Rational.valueOf(2, 1)));
    assertTrue(Rational.valueOf(1, 1).lt(Rational.valueOf(4, 2)));
  }

  @Test
  public void testModulo() {
    assertEquals("0", Rational.valueOf(3, 1)
        .modulo(Rational.valueOf(1, 1)).toString());
    assertEquals("1", Rational.valueOf(3, 1)
        .modulo(Rational.valueOf(2, 1)).toString());
    assertEquals("0", Rational.valueOf(4, 1)
        .modulo(Rational.valueOf(2, 1)).toString());
    assertEquals("½", Rational.valueOf(3,2)
        .modulo(Rational.valueOf(1, 1)).toString());
  }

  @Test
  public void testFloor() {
    assertEquals(0, Rational.valueOf(1,2).floor());
    assertEquals(3, Rational.valueOf(10,3).floor());
  }

  @Test
  public void testFloorToRadix() {
    assertEquals("2", Rational.valueOf(3, 1)
        .floor(Rational.valueOf(2, 1)).toString());
    assertEquals("1½", Rational.valueOf(2, 1, true)
        .floor(Rational.valueOf(3, 2)).toString());
    assertEquals("3", Rational.valueOf(4, 1, true)
        .floor(Rational.valueOf(3, 2)).toString());

    assertEquals("2.0", Rational.valueOf(3, 1)
        .floor(Real.valueOf(2.0)).toString());
    assertEquals("2", Rational.valueOf(3, 1)
        .floor(Whole.valueOf("2")).toString());
  }

  @Test
  public void testNextFloor() {
    assertEquals(1, Rational.valueOf(1,2).nextFloor());
    assertEquals(4, Rational.valueOf(10,3).nextFloor());
  }

  @Test
  public void testEgyptianFractions() {
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

  @Test(expected = OdinRuntimeException.class)
  public void testEgyptianFractionsOverflow() {
    getEgyptianFractionsAsString(21, 1, 1);
  }

  private String getEgyptianFractionsAsString(long numerator, long denominator) {
    Rational rational = Rational.valueOf(numerator, denominator, true);
    return join(rational.getEgyptianFractions(), rational.isNegative());
  }

  private String getEgyptianFractionsAsString(
      long numerator, long denominator, int maxIntegerPart) {
    Rational rational = Rational.valueOf(numerator, denominator, true);
    return join(rational.getEgyptianFractions(maxIntegerPart), rational.isNegative());
  }

  private String join(Stream<Rational> parts, boolean isNegative) {
    String delimiter = isNegative ? "" : "+";
    return parts.map(Rational::toString)
        .collect(Collectors.joining(delimiter));
  }

  @Test
  public void testIsNegative() {
    assertTrue(Rational.valueOf(-1,2).isNegative());
    assertTrue(Rational.valueOf(1,-2).isNegative());
    assertFalse(Rational.valueOf(1,2).isNegative());
    assertFalse(Rational.valueOf(-1,-2).isNegative());
  }

  @Test
  public void testNegative() {
    assertEquals(Rational.valueOf(1, 2), Rational.valueOf(-1,2).negative());
    assertEquals(Rational.valueOf(1, 2), Rational.valueOf(1,-2).negative());
    assertEquals(Rational.valueOf(1, 2), Rational.valueOf(1,-2, false).negative());
    assertEquals(Rational.valueOf(-1, 2), Rational.valueOf(1,2).negative());
  }

  @Test
  public void testAbsolute() {
    assertEquals(Rational.valueOf(1,2), Rational.valueOf(1,2).absolute());
    assertEquals(Rational.valueOf(1,2), Rational.valueOf(-1,2).absolute());
    assertEquals(Rational.valueOf(1,2), Rational.valueOf(1,-2).absolute());
    assertEquals(Rational.valueOf(1,2), Rational.valueOf(-1,-2).absolute());
  }

  @Test(expected = OdinRuntimeException.class)
  public void testZeroDenominator() {
    Rational.valueOf(1,0);
  }

  @Test
  public void testValueOfString() {
    assertEquals(Wholes.ONE, Rational.valueOf("1"));
    assertEquals(Rationals.HALF, Rational.valueOf("1/2"));
    assertEquals(Rationals.HALF, Rational.valueOf("1 / 2"));
    assertEquals(Wholes.ZERO, Rational.valueOf(null));
    assertEquals(Wholes.ZERO, Rational.valueOf(""));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testValueOfStringFail() {
    assertEquals(Wholes.ZERO, Rational.valueOf("/2"));
  }

  @Test
  public void testCeiling() {
    assertEquals(1, Rationals.HALF.ceiling());
    assertEquals(0, Rationals.HALF.negative().ceiling());
    assertEquals(2, Rationals.FOUR_THIRDS.ceiling());
    assertEquals(-1, Rationals.FOUR_THIRDS.negative().ceiling());
  }

}