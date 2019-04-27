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

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.purplepip.odin.creation.track.Track;
import org.junit.jupiter.api.Test;

class SetActionTest {
  @Test
  void testSetAction() {
    SetAction action = new SetAction().nameValuePairs("a=1;b=2");
    action.initialise();
    Track track = spy(Track.class);
    action.execute(new ActionContext(track));
    verify(track, times(1)).setProperty("a", "1");
    verify(track, times(1)).setProperty("b", "2");
    verify(track, times(0)).setProperty("not-set", "3");
  }
}