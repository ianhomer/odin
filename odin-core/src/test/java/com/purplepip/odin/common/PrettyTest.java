package com.purplepip.odin.common;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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
class PrettyTest {
  @Test
  void testReplaceTrailingZeros() {
    assertEquals("1···", Pretty.replaceTrailingZeros(1000, 3));
    assertEquals("101···", Pretty.replaceTrailingZeros(101000, 3));
    assertEquals("100···", Pretty.replaceTrailingZeros(100000, 3));
    assertEquals("10000·", Pretty.replaceTrailingZeros(100000, 1));
    assertEquals("100000", Pretty.replaceTrailingZeros(100000, 0));
    assertEquals("55···", Pretty.replaceTrailingZeros(55000, 4));
  }

  @Test
  void testReplaceTrailingZerosTooHigh() {
    assertThrows(OdinRuntimeException.class, () ->
        Pretty.replaceTrailingZeros(1000, 8)
    );
  }

  @Test
  void testReplaceTrailingZerosTooLow() {
    assertThrows(OdinRuntimeException.class, () ->
        Pretty.replaceTrailingZeros(1000, -1)
    );
  }

}