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

import com.purplepip.odin.clock.beats.BeatsPerMinute;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;

/**
 * Beat clock that maintains absolute precision by using rationals instead of reals.  This is
 * useful to testing purposes since strong assertions can be made without rounding error.  It
 * does however come with a performance overhead which makes it not suitable for general use.
 */
public class PrecisionBeatClock extends BeatClock {
  public static BeatClock newPrecisionBeatClock(int staticBeatsPerMinute) {
    return new PrecisionBeatClock(new Configuration()
        .staticBeatsPerMinute(staticBeatsPerMinute));
  }

  /**
   * New precision beat clock.
   *
   * @param beatsPerMinute BPM provider
   * @return new beat clock
   */
  public static BeatClock newPrecisionBeatClock(BeatsPerMinute beatsPerMinute) {
    return new PrecisionBeatClock(new Configuration()
        .beatsPerMinute(beatsPerMinute));
  }

  /**
   * New precision beat clock.
   *
   * @param staticBeatsPerMinute static BPM
   * @param microsecondPositionProvider microsecond position provider
   * @return new beat clock
   */
  public static BeatClock newPrecisionBeatClock(int staticBeatsPerMinute,
                                       MicrosecondPositionProvider microsecondPositionProvider) {
    return new PrecisionBeatClock(new Configuration()
        .staticBeatsPerMinute(staticBeatsPerMinute)
        .microsecondPositionProvider(microsecondPositionProvider));
  }

  /**
   * New precision beat clock.
   *
   * @param beatsPerMinute BPM provider
   * @param microsecondPositionProvider microsecond position provider
   * @return new beat clock
   */
  public static BeatClock newPrecisionBeatClock(BeatsPerMinute beatsPerMinute,
                                       MicrosecondPositionProvider microsecondPositionProvider) {
    return new PrecisionBeatClock(new Configuration()
          .beatsPerMinute(beatsPerMinute)
          .microsecondPositionProvider(microsecondPositionProvider));
  }

  public PrecisionBeatClock(Configuration configuration) {
    super(configuration);
  }

  @Override
  public Real getPosition(long microseconds) {
    return Whole.valueOf(microseconds).divide(getBeatsPerMinute().getMicroSecondsPerBeat());
  }
}
