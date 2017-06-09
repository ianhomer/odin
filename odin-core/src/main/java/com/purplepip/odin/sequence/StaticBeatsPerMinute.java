/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.purplepip.odin.sequence;

/**
 * Static beats per minute.
 */
public class StaticBeatsPerMinute implements BeatsPerMinute {
  private static final int MICROSECONDS_PER_MINUTE = 60000000;

  private int beatsPerMinute;
  private long microsecondsPerBeat;

  public StaticBeatsPerMinute(int beatsPerMinute) {
    this.beatsPerMinute = beatsPerMinute;
    microsecondsPerBeat = MICROSECONDS_PER_MINUTE / beatsPerMinute;
  }

  @Override
  public int getBeatsPerMinute() {
    return beatsPerMinute;
  }

  @Override
  public long getMicroSecondsPerBeat() {
    return microsecondsPerBeat;
  }
}
