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
import com.purplepip.odin.sequence.tick.MovableTock;
import com.purplepip.odin.sequence.tick.MutableRuntimeTick;
import com.purplepip.odin.sequence.tick.RuntimeTick;
import com.purplepip.odin.sequence.tick.RuntimeTicks;
import com.purplepip.odin.sequence.tick.SealedTock;
import com.purplepip.odin.sequence.tick.Tock;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sequence.
 */
@ListenerPriority()
@ToString(callSuper = true)
public abstract class AbstractMutableSequenceRuntime<A> implements SequenceRoll<A>, ClockListener {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractMutableSequenceRuntime.class);

  private BeatClock beatClock;
  /*
   * Tick converted clock.
   */
  private Clock clock;
  private MeasureProvider measureProvider;
  private Event<A> nextEvent;
  private MovableTock tock;
  private Tock sealedTock;
  private final MutableRuntimeTick tick = new MutableRuntimeTick();
  private final UnmodifiableRuntimeTick unmodifiableRuntimeTick = new UnmodifiableRuntimeTick(tick);
  private boolean tickDirty;
  private Sequence sequence;
  private boolean sequenceDirty;

  @Override
  public OffsetProvider getOffsetProvider() {
    return () -> getSequence().getOffset();
  }

  @Override
  public RuntimeTick getTick() {
    return unmodifiableRuntimeTick;
  }

  protected final void setBeatClock(BeatClock beatClock) {
    this.beatClock = beatClock;
    beatClock.addListener(this);
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
  @Override
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
  @Override
  public final void refresh() {
    if (sequenceDirty) {
      afterSequenceChange();
    }
    if (beatClock.isStarted() && tickDirty) {
      afterTickChange();
    }
    LOG.debug("Refreshed {}", this);
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

    sequenceDirty = false;
    LOG.debug("afterSequenceChange executed");
  }

  private void afterTickChange() {
    /*
     * Calculate offset of this sequence in microseconds ...
     */
    long microsecondOffset = new DefaultTickConverter(beatClock, getTick(),
        RuntimeTicks.MICROSECOND, () -> 0).convert(getSequence().getOffset());
    LOG.debug("Microsecond start for this sequence : {}", microsecondOffset);
    /*
     * ... and use this to create a converter that will convert microseconds into tock count
     * for this sequence runtime.
     */
    TickConverter microsecondToSequenceTickConverter =
        new DefaultTickConverter(beatClock, RuntimeTicks.MICROSECOND, getTick(),
            () -> - microsecondOffset);
    /*
     * Set the tock count, that this sequence runtime should start at, to the current tock
     * count according to the beatClock.  There is no point starting the tock any earlier since
     * that time has passed.
     */
    long tockCountStart = microsecondToSequenceTickConverter
        .convert(beatClock.getMicroseconds());
    if (tockCountStart < 0) {
      /*
       * If sequence start is the future then set tock to 0 so that it is ready to
       * start when the time is right.
       */
      tockCountStart = 0;
    }
    LOG.debug("Tock count start is {} at {}", tockCountStart, beatClock);
    tock = new MovableTock(getSequence().getTick(), tockCountStart);
    sealedTock = new SealedTock(tock);

    clock = new TickConvertedClock(beatClock, getTick(), getOffsetProvider());
    tickDirty = false;
    LOG.debug("afterTickChange executed");
  }

  /**
   * Action on beatClock start.
   */
  @Override
  public void onClockStart() {
    if (tickDirty) {
      afterTickChange();
    }
  }

  /**
   * Action on beatClock stop.
   */
  @Override
  public void onClockStop() {

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

  /**
   * Get tick converted clock.
   *
   * @return tick converted clock.
   */
  protected Clock getClock() {
    return clock;
  }

  private Event<A> getNextEventInternal(MovableTock tock) {
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
