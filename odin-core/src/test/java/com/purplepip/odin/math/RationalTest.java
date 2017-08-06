package com.purplepip.odin.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
public class RationalTest {
  @Test
  public void testValue() {
    assertEquals(0.5, new Rational(1,2).getValue(), 0.01);
    assertEquals(3.333, new Rational(10,3).getValue(), 0.001);
  }

  @Test
  public void testFloor() {
    assertEquals(0, new Rational(1,2).floor());
    assertEquals(3, new Rational(10,3).floor());
  }

  @Test
  public void testToString() {
    assertEquals("1", new Rational(1,1).toString());
    assertEquals("1", new Rational(2,2).toString());
    assertEquals("½", new Rational(1,2).toString());
    assertEquals("1½", new Rational(3,2).toString());
    assertEquals("1⅔", new Rational(5,3).toString());
    assertEquals("½", new Rational(2,4).toString());
    assertEquals("½", new Rational(2,4, true).toString());
    assertEquals("2¼", new Rational(9,4).toString());
    assertEquals("1+3/7", new Rational(10,7).toString());
  }
}