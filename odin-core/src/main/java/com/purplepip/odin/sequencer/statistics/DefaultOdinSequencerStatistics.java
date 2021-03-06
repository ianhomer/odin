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

package com.purplepip.odin.sequencer.statistics;

import com.purplepip.odin.bag.ThingStatistics;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.ToString;

@ToString
public class DefaultOdinSequencerStatistics
    implements MutableOdinSequencerStatistics {
  private final ThingStatistics trackStatistics;
  private final ThingStatistics reactorStatistics;
  private final AtomicInteger programChangeCount = new AtomicInteger();
  private final AtomicInteger eventTooLateCount = new AtomicInteger();

  public DefaultOdinSequencerStatistics(ThingStatistics trackStatistics,
                                        ThingStatistics reactorStatistics) {
    this.trackStatistics = trackStatistics;
    this.reactorStatistics = reactorStatistics;
  }

  @Override
  public void incrementProgramChangeCount() {
    programChangeCount.incrementAndGet();
  }

  @Override
  public ThingStatistics getTrackStatistics() {
    return trackStatistics;
  }

  @Override
  public ThingStatistics getReactorStatistics() {
    return reactorStatistics;
  }

  @Override
  public int getProgramChangeCount() {
    return programChangeCount.get();
  }

  @Override
  public void incrementEventTooLateCount() {
    eventTooLateCount.incrementAndGet();
  }

  @Override
  public int getEventTooLateCount() {
    return eventTooLateCount.get();
  }

}
