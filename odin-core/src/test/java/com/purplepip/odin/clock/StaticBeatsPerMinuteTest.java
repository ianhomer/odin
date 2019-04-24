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
import static org.junit.Assert.assertTrue;

import com.purplepip.odin.clock.beats.BeatsPerMinute;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import org.junit.Test;

public class StaticBeatsPerMinuteTest {
  @Test
  public void testBeatsPerMinute() {
    BeatsPerMinute bpm = new StaticBeatsPerMinute(60);
    assertTrue(bpm.getMicroSecondsPerBeat() instanceof Whole);
    assertEquals(Wholes.valueOf(1000000), bpm.getMicroSecondsPerBeat());
  }

  @Test
  public void testToString() {
    BeatsPerMinute bpm = new StaticBeatsPerMinute(60);
    assertEquals("˻60 BPM˼", bpm.toString());
  }
}