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

package com.purplepip.odin.creation.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.purplepip.odin.creation.track.Track;
import org.junit.jupiter.api.Test;

class IncrementActionTest {
  @Test
  void testCopy() {
    IncrementAction action = new IncrementAction();
    IncrementAction copy = action.copy();
    assertEquals("increment", copy.getType());
  }

  @Test
  void testExecute() {
    Action action = new IncrementAction().propertyName("channel").increment(1);
    assertTrue(action.arePropertiesDeclared());
    Track track = spy(Track.class);
    when(track.getProperty("channel")).thenReturn("5");
    action.execute(new ActionContext(track));
    verify(track, times(1)).setProperty("channel", "6");

  }
}