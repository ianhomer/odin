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
public class WholeTest {
  @Test
  public void testFloor() {
    assertEquals(3, Real.valueOf(3).floor());
    assertEquals(2, Real.valueOf(2.1).floor());
  }

  @Test
  public void testModulo() {
    assertEquals(Real.valueOf(2), Real.valueOf(10).modulo(Real.valueOf(4)));
  }

  @Test
  public void testFloorWithRadix() {
    assertEquals(Real.valueOf(8), Real.valueOf(10).floor(Real.valueOf(4)));
  }

  @Test
  public void testEquals() {
    assertEquals(Real.valueOf(1), new Whole(1));
  }
}