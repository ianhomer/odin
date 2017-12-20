package com.purplepip.odin.math;

import static com.purplepip.odin.math.Wholes.mutableOf;
import static org.junit.Assert.assertEquals;

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
public class MutableWholeTest {
  @Test
  public void plus() throws Exception {
    assertEquals(Rationals.valueOf(5,2),
        Wholes.mutableOf(2).plus(Rationals.HALF));
    assertEquals(Wholes.THREE.getNumerator(),
        Wholes.mutableOf(2).plus(Wholes.ONE).getNumerator());
    assertEquals(Reals.valueOf(5.3),
        Wholes.mutableOf(1).plus(Reals.valueOf(4.3)));
  }

  @Test
  public void minus() throws Exception {
    assertEquals(Rationals.valueOf(3,2),
        Wholes.mutableOf(2).minus(Rationals.HALF));
    assertEquals(Wholes.ONE.getNumerator(),
        Wholes.mutableOf(2).minus(Wholes.ONE).getNumerator());
    assertEquals(Reals.valueOf(4.7),
        Wholes.mutableOf(9).minus(Reals.valueOf(4.3)));
  }
}