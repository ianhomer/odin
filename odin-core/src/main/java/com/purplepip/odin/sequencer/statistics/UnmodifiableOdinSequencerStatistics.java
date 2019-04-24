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
import lombok.ToString;

@ToString
public class UnmodifiableOdinSequencerStatistics implements OdinSequencerStatistics {
  private OdinSequencerStatistics statistics;

  public UnmodifiableOdinSequencerStatistics(OdinSequencerStatistics statistics) {
    this.statistics = statistics;
  }

  @Override
  public ThingStatistics getTrackStatistics() {
    return statistics.getTrackStatistics();
  }

  @Override
  public ThingStatistics getReactorStatistics() {
    return statistics.getReactorStatistics();
  }

  @Override
  public int getProgramChangeCount() {
    return statistics.getProgramChangeCount();
  }

  @Override
  public int getEventTooLateCount() {
    return statistics.getEventTooLateCount();
  }
}
