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

package com.purplepip.odin.sequence;

import com.purplepip.odin.sequence.measure.MeasureProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sequence.
 */
@ListenerPriority()
public abstract class AbstractMutableSequenceRuntime<A> extends AbstractSequenceRuntime<A>
    implements ClockListener {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractMutableSequenceRuntime.class);

  private Clock clock;
  private MeasureProvider measureProvider;
  private Event<A> nextEvent;
  private MutableTock tock;
  private Tock sealedTock;
  private final MutableRuntimeTick tick = new MutableRuntimeTick();
  private final UnmodifiableRuntimeTick unmodifiableRuntimeTick = new UnmodifiableRuntimeTick(tick);
  private boolean tickDirty;
  private TickConverter microsecondToSequenceTickConverter;
  private Sequence sequence;
  private boolean sequenceDirty;

  @Override
  public RuntimeTick getTick() {
    return unmodifiableRuntimeTick;
  }

  protected final void setClock(Clock clock) {
    this.clock = clock;
    clock.addListener(this);
  }

  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  /**
   * Set measure provider.
   *
   * @param measureProvider measure provider
   */
  protected final void setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
  }

  /**
   * Set configuration for this sequence runtime.
   *
   * @param sequence sequence configuration
   */
  public void setSequence(Sequence sequence) {
    this.sequence = sequence;
    sequenceDirty = true;
  }

  @Override
  public Sequence getSequence() {
    return sequence;
  }

  /**
   * Refresh the sequence runtime after a sequence change.
   */
  public final void refresh() {
    if (sequenceDirty) {
      afterSequenceChange();
    }
    if (clock.isStarted() && tickDirty) {
      afterTickChange();
    }
  }

  private void afterSequenceChange() {
    /*
     * Determine if the tick has changed
     */
    tickDirty = this.getTick() == null || !getSequence().getTick().equals(this.getTick());
    if (tickDirty) {
      /*
       * Change runtime tick
       */
      tick.setTick(getSequence().getTick());
    }
    /*
     * Calculate offset of this sequence in microseconds ...
     */
    long microsecondOffset = new DefaultTickConverter(clock, getTick(),
        RuntimeTicks.MICROSECOND, () -> 0).convert(getSequence().getOffset());
    LOG.debug("Microsecond start for this sequence : {}", microsecondOffset);
    /*
     * ... and use this to create a converter that will convert microseconds into tock count
     * for this sequence runtime.
     */
    microsecondToSequenceTickConverter =
        new DefaultTickConverter(clock, RuntimeTicks.MICROSECOND, getTick(),
            () -> - microsecondOffset);
    sequenceDirty = false;
    LOG.debug("afterSequenceChange executed");
  }

  /**
   * Action on clock start.
   */
  @Override
  public void onClockStart() {
    if (tickDirty) {
      afterTickChange();
    }
  }

  /**
   * Action on clock stop.
   */
  @Override
  public void onClockStop() {

  }

  private void afterTickChange() {
    /*
     * Set the tock count, that this sequence runtime should start at, to the current tock
     * count according to the clock.  There is no point starting the tock any earlier since
     * that time has passed.
     */
    long tockCountStart = microsecondToSequenceTickConverter
        .convert(clock.getMicrosecondPosition());
    if (tockCountStart < 0) {
      /*
       * If sequence start is the future then set tock to 0 so that it is ready to
       * start when the time is right.
       */
      tockCountStart = 0;
    }
    LOG.debug("Tock count start is {} at {}", tockCountStart, clock);
    tock = new MutableTock(getSequence().getTick(), tockCountStart);
    sealedTock = new SealedTock(tock);
    tickDirty = false;
    LOG.debug("afterTickChange executed");

  }

  protected abstract Event<A> getNextEvent(Tock tock);

  protected long getLength() {
    return getSequence().getLength();
  }

  private boolean isActive() {
    if (tock == null) {
      LOG.trace("is Active false : not started");
      return false;
    }
    LOG.trace("isActive {} : {} < {}", getLength(), tock.getCount(), getLength());
    return getLength() < 0 || tock.getCount() < getLength();
  }

  private Event<A> getNextEventInternal(MutableTock tock) {
    Event<A> event = getNextEvent(sealedTock);
    LOG.trace("Next event after {} is at {}", tock, event.getTime());
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
