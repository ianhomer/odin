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

package com.purplepip.odin.clock.tick;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.purplepip.odin.math.Wholes;
import org.junit.Test;

public class TimeThingTest {

  @Test
  public void isEndless() {
    TimeThing timeThing = mock(TimeThing.class);
    when(timeThing.isEndless()).thenCallRealMethod();
    when(timeThing.getLength()).thenReturn(Wholes.ONE);
    assertFalse(timeThing.isEndless());
    when(timeThing.getLength()).thenReturn(Wholes.MINUS_ONE);
    assertTrue(timeThing.isEndless());
  }
}