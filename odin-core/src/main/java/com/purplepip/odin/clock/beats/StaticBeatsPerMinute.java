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

package com.purplepip.odin.clock.beats;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;

/**
 * Static beats per minute.
 */
public class StaticBeatsPerMinute implements BeatsPerMinute {
  private static final int MICROSECONDS_PER_MINUTE = 60_000_000;
  private final int beatsPerMinute;
  private final Real microsecondsPerBeat;

  public StaticBeatsPerMinute(int beatsPerMinute) {
    this.beatsPerMinute = beatsPerMinute;
    microsecondsPerBeat = Wholes.valueOf(MICROSECONDS_PER_MINUTE / beatsPerMinute);
  }

  @Override
  public int getBeatsPerMinute() {
    return beatsPerMinute;
  }

  @Override
  public Real getMicroSecondsPerBeat() {
    return microsecondsPerBeat;
  }

  public String toString() {
    return "˻" + beatsPerMinute + " BPM˼";
  }
}
