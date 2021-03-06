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

package com.purplepip.odin.creation.flow;

import com.purplepip.odin.clock.Clock;
import com.purplepip.odin.clock.Loop;
import com.purplepip.odin.clock.MeasureContext;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.tick.Tock;
import com.purplepip.odin.creation.sequence.Sequence;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.ScanForwardEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Default flow implementation.
 */
@Slf4j
public class DefaultFlow<S extends Sequence> implements MutableFlow<S> {
  private S sequence;
  private final MeasureContext context;

  /**
   * Create flow.
   *
   * @param clock clock
   * @param measureProvider measure provider
   */
  public DefaultFlow(Clock clock, MeasureProvider measureProvider) {
    this.context = new MeasureContext(clock, measureProvider);
  }

  @Override
  public void setSequence(S sequence) {
    this.sequence = sequence;
  }

  @Override
  public S getSequence() {
    return sequence;
  }

  @Override
  public MeasureContext getContext() {
    return context;
  }

  @Override
  public Event getNextEvent(Tock tock) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    Loop loop = new Loop(sequence.getLoopLength(), tock.getPosition());
    int i = 0;
    long maxScanForward = context.getClock().getMaxLookForward().floor();
    Event event = null;
    while (event == null && i < maxScanForward) {
      event = sequence.getNextEvent(context, loop);
      if (event == null) {
        LOG.trace("{} : No event found at tock {}, incrementing loop", sequence.getName(), loop);
        loop.increment();
        i++;
      }
    }

    if (event == null) {
      LOG.trace("No notes found in the next {} ticks after tock {} for sequence {}",
          maxScanForward, tock, getSequence());
      event = new ScanForwardEvent(loop.getPosition().getLimit());
    }
    return event;
  }

  @Override
  public void initialise() {
    sequence.initialise();
  }
}
