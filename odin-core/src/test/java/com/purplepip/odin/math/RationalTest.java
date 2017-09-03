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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.common.OdinRuntimeException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class RationalTest {
  @Test
  public void testValue() {
    assertEquals(0.5, new Rational(1,2).getValue(), 0.01);
    assertEquals(-0.5, new Rational(-1,2).getValue(), 0.01);
    assertEquals(3.333, new Rational(10,3).getValue(), 0.001);
  }


  @Test
  public void testToString() {
    assertEquals("1", new Rational(1,1).toString());
    assertEquals("1/1", new Rational(1,1, false).toString());
    assertEquals("1", new Rational(2,2).toString());
    assertEquals("2/2", new Rational(2,2, false).toString());
    assertEquals("½", new Rational(1,2).toString());
    assertEquals("1½", new Rational(3,2).toString());
    assertEquals("1⅔", new Rational(5,3).toString());
    assertEquals("½", new Rational(2,4).toString());
    assertEquals("½", new Rational(2,4, true).toString());
    assertEquals("2¼", new Rational(9,4).toString());
    assertEquals("1+3/7", new Rational(10,7).toString());
    assertEquals("-½", new Rational(-2,4).toString());
    assertEquals("½", new Rational(-2,-4).toString());
    assertEquals("-½", new Rational(2,-4).toString());
    assertEquals("-1/1", new Rational(-1,1, false).toString());
    assertEquals("-1", new Rational(-1,1).toString());
  }

  @Test
  public void testPlus() {
    assertEquals("1½", new Rational(1,1)
        .plus(new Rational(1,2)).toString());
    assertEquals("7/12", new Rational(1,3)
        .plus(new Rational(1,4)).toString());
    assertEquals("½", Wholes.ZERO
        .plus(Rational.valueOf(1,2)).toString());
  }

  @Test
  public void testMinus() {
    assertEquals("½", new Rational(1,1)
        .minus(new Rational(1,2)).toString());
    assertEquals("1/12", new Rational(1,3)
        .minus(new Rational(1,4)).toString());
  }

  @Test
  public void testTimes() {
    assertEquals("1", new Rational(2,1)
        .times(new Rational(1,2)).toString());
    assertEquals("1/12", new Rational(1,3)
        .times(new Rational(1,4)).toString());
  }

  @Test
  public void testGe() {
    assertTrue(new Rational(2, 1).ge(new Rational(2, 1)));
    assertTrue(new Rational(2, 1).ge(new Rational(4, 2)));
  }

  @Test
  public void testGt() {
    assertFalse(new Rational(2, 1).gt(new Rational(2, 1)));
    assertFalse(new Rational(-2, 5).gt(new Whole(0)));
    assertTrue(new Rational(3, 1).gt(new Rational(4, 2)));
  }

  @Test
  public void testLt() {
    assertFalse(new Rational(2, 1).lt(new Rational(2, 1)));
    assertTrue(new Rational(1, 1).lt(new Rational(4, 2)));
  }

  @Test
  public void testModulo() {
    assertEquals("0", new Rational(3, 1)
        .modulo(new Rational(1, 1)).toString());
    assertEquals("1", new Rational(3, 1)
        .modulo(new Rational(2, 1)).toString());
    assertEquals("0", new Rational(4, 1)
        .modulo(new Rational(2, 1)).toString());
    assertEquals("½", new Rational(3,2)
        .modulo(new Rational(1, 1)).toString());
  }

  @Test
  public void testFloor() {
    assertEquals(0, new Rational(1,2).floor());
    assertEquals(3, new Rational(10,3).floor());
  }

  @Test
  public void testFloorToRadix() {
    assertEquals("2", new Rational(3, 1)
        .floor(new Rational(2, 1)).toString());
    assertEquals("1½", new Rational(2, 1)
        .floor(new Rational(3, 2)).toString());
    assertEquals("3", new Rational(4, 1)
        .floor(new Rational(3, 2)).toString());

    assertEquals("2.0", new Rational(3, 1)
        .floor(Real.valueOf(2.0)).toString());
    assertEquals("2", new Rational(3, 1)
        .floor(Real.valueOf("2")).toString());
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
    Rational rational = Rational.valueOf(numerator, denominator);
    return join(rational.getEgyptianFractions(), rational.isNegative());
  }

  private String getEgyptianFractionsAsString(
      long numerator, long denominator, int maxIntegerPart) {
    Rational rational = Rational.valueOf(numerator, denominator);
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
    assertNotEquals(Rational.valueOf(1, 2), Rational.valueOf(1,-2, false).negative());
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
}