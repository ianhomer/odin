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
public class RealTest {
  @Test
  public void testValueOf() {
    assertEquals(Wholes.ZERO, Real.valueOf(0));
  }

  @Test
  public void testPlus() {
    assertEquals(Wholes.TWO, Real.valueOf(1).plus(Real.valueOf(1)));
    assertEquals(Real.valueOf(2.1), Real.valueOf(1.1).plus(Real.valueOf(1)));
    assertEquals(Real.valueOf(2.1), Real.valueOf(1).plus(Real.valueOf(1.1)));
  }

  @Test
  public void testFloorToRadix() {
    assertEquals(Real.valueOf(8), Real.valueOf(9.3).floor(Real.valueOf(4)));
  }
}