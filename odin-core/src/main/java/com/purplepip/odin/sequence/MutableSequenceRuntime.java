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

import com.purplepip.odin.sequence.measure.MeasureProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sequence.
 */
public abstract class MutableSequenceRuntime<S extends Sequence, A> implements SequenceRuntime<A>  {
  private static final Logger LOG = LoggerFactory.getLogger(MutableSequenceRuntime.class);

  private MeasureProvider measureProvider;
  private S sequence;
  private Event<A> nextEvent;
  private long length;
  private MutableTock tock;
  private Tock sealedTock;
  private RuntimeTick tick;

  @Override
  public RuntimeTick getTick() {
    return tick;
  }

  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  /**
   * Set measure provider.
   *
   * @param measureProvider measure provider
   */
  public void setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
    if (sequence != null) {
      reload();
    }
  }

  /**
   * Set configuration for this sequence runtime.
   *
   * @param sequence sequence configuration
   */
  public void setSequence(S sequence) {
    this.sequence = sequence;
    if (measureProvider != null) {
      reload();
    }
  }

  public S getSequence() {
    return sequence;
  }

  /**
   * Reload the sequence runtime.
   */
  private void reload() {
    tick = new DefaultRuntimeTick(sequence.getTick());
    TickConverter converter = new SameTimeUnitTickConverter(RuntimeTicks.BEAT,
        getTick());
    this.length = converter.convert(getSequence().getLength());
    // FIX : Currently reload resets tock to start of sequencer - we should set it to now
    tock = new MutableTock(getSequence().getTick(), 0);
    sealedTock = new SealedTock(tock);
    LOG.debug("Reloading runtime sequence : length = {} {}", length, tick);
  }

  protected abstract Event<A> getNextEvent(Tock tock);

  protected long getLength() {
    return length;
  }

  private boolean isActive() {
    LOG.trace("isActive {} : {} < {}", getLength(), tock.getCount(), getLength());
    return getLength() < 0 || tock.getCount() < getLength();
  }

  private Event<A> getNextEventInternal(MutableTock tock) {
    Event<A> event = getNextEvent(sealedTock);
    /*
     * Now increment internal tock to the time of the provided event
     */
    tock.setCount(event.getTime());
    /*
     * If the response was a scan forward signal then we return a null event since no event
     * was found and we've handled the scanning forward above.
     */
    if (event instanceof ScanForwardEvent) {
      return null;
    }
    return event;
  }

  @Override
  public Event<A> peek() {
    if (isActive() && nextEvent == null) {
      nextEvent = getNextEventInternal(tock);
    }
    return nextEvent;
  }

  @Override
  public Event<A> pop() {
    Event<A> thisEvent = nextEvent;
    if (isActive()) {
      nextEvent = getNextEventInternal(tock);
    } else {
      nextEvent = null;
    }
    return thisEvent;
  }
}
