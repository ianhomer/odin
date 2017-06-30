/*
 * Copyright (c) 2017 Ian Homer. All Rights Reserved
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

import java.util.concurrent.atomic.AtomicInteger;
import lombok.ToString;

@ToString
public class DefaultOdinSequencerStatistics
    implements MutableOdinSequencerStatistics {
  private AtomicInteger programChangeCount = new AtomicInteger();
  private AtomicInteger trackAddedCount = new AtomicInteger();
  private AtomicInteger trackUpdatedCount = new AtomicInteger();
  private AtomicInteger trackRemovedCount = new AtomicInteger();
  private AtomicInteger eventTooLateCount = new AtomicInteger();

  public void incrementTrackAddedCount() {
    trackAddedCount.incrementAndGet();
  }

  @Override
  public int getTrackAddedCount() {
    return trackAddedCount.get();
  }

  public void incrementTrackRemovedCount(int removedCount) {
    trackRemovedCount.addAndGet(removedCount);
  }

  @Override
  public int getTrackRemovedCount() {
    return trackRemovedCount.get();
  }

  public void incrementTrackUpdatedCount() {
    trackUpdatedCount.incrementAndGet();
  }

  @Override
  public int getTrackUpdatedCount() {
    return trackUpdatedCount.get();
  }

  public void incrementProgramChangeCount() {
    programChangeCount.incrementAndGet();
  }

  public int getProgramChangeCount() {
    return programChangeCount.get();
  }

  public void incrementEventTooLateCount() {
    eventTooLateCount.incrementAndGet();
  }

  @Override
  public int getEventTooLateCount() {
    return eventTooLateCount.get();
  }

}
