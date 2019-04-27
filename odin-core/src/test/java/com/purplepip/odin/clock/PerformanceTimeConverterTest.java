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

package com.purplepip.odin.clock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

public class PerformanceTimeConverterTest {
  @Test
  public void convert() throws Exception {
    MicrosecondPositionProvider microsecondPositionProvider =
        mock(MicrosecondPositionProvider.class);
    when(microsecondPositionProvider.getMicroseconds()).thenReturn(100L);
    PerformanceTimeConverter timeConverter =
        new PerformanceTimeConverter(microsecondPositionProvider);
    timeConverter.onPerformanceStart();
    assertEquals(300, timeConverter.convert(200L));
  }
}