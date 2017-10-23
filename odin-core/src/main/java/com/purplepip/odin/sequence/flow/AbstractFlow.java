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

package com.purplepip.odin.sequence.flow;

import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Real;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.ScanForwardEvent;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.tick.Tock;
import java.lang.reflect.ParameterizedType;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract logic class.
 */
@Slf4j
public abstract class AbstractFlow<S extends Sequence<A>, A>
    implements MutableFlow<S, A> {
  private FlowConfiguration configuration;
  private S sequence;
  private Class<S> sequenceClass;
  private FlowContext context;

  /**
   * Create flow.
   *
   * @param clock clock
   * @param measureProvider measure provider
   */
  @SuppressWarnings("unchecked")
  public AbstractFlow(Clock clock, MeasureProvider measureProvider) {
    this.context = new FlowContext(clock, measureProvider);
    this.sequenceClass = (Class<S>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
  }

  @Override
  public Class<S> getSequenceClass() {
    return sequenceClass;
  }

  @Override
  public void setSequence(S sequence) {
    this.sequence = sequence;
  }

  @Override
  public S getSequence() {
    return sequence;
  }

  public FlowContext getContext() {
    return context;
  }

  @Override
  public void setConfiguration(FlowConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public FlowConfiguration getConfiguration() {
    return configuration;
  }

  protected Real getMaxScanForward() {
    return getContext().getClock().getDuration(getConfiguration().getMaxForwardScan());
  }

  @Override
  public Event<A> getNextEvent(Tock tock) {
    /*
     * Create local and temporary mutable tock for this function execution.
     */
    Loop loop = new Loop(sequence.getLoopLength(), tock.getPosition());
    int i = 0;
    long maxScanForward = getMaxScanForward().floor();
    Event<A> event = null;
    while (event == null && i < maxScanForward) {
      event = sequence.getNextEvent(getContext(), loop);
      if (event == null) {
        LOG.debug("No event found at tock {}, incrementing loop", loop);
        loop.increment();
        i++;
      }
    }

    if (event == null) {
      LOG.debug("No notes found in the next {} ticks after tock {} for sequence {}",
          maxScanForward, tock, getSequence());
      event = new ScanForwardEvent<>(loop.getPosition().getLimit());
    }
    return event;
  }
}
