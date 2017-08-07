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

package com.purplepip.odin.music.composition;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.math.CoercedRational;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Rationals;
import org.junit.Test;

public class CompositionTest {
  @Test
  public void testLoopStart() {
    Composition composition = new CompositionFactory().create("C#5/q, B4, A4, G#4");
    assertEquals(new Rational(4), composition.getTocks());
    assertEquals(Rationals.ZERO, composition.getLoopStart(Rationals.ZERO));
    assertEquals(new Rational(8), composition.getLoopStart(new CoercedRational(9.3)));
  }
}