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

package com.purplepip.odin.sequence;

import com.purplepip.odin.events.Event;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.properties.runtime.Mutable;
import com.purplepip.odin.properties.runtime.ObservableProperty;
import com.purplepip.odin.properties.runtime.Property;
import com.purplepip.odin.sequence.flow.MutableFlow;
import com.purplepip.odin.sequence.measure.ConvertedMeasureProvider;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.tick.MovableTock;
import com.purplepip.odin.sequence.tick.SealedTock;
import com.purplepip.odin.sequence.tick.Tick;
import com.purplepip.odin.sequence.tick.Ticks;
import com.purplepip.odin.sequence.tick.Tock;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sequence roll.
 */
@ListenerPriority()
@ToString
public class MutableSequenceRoll<A> implements SequenceRoll<A>, ClockListener {
  private static final Logger LOG = LoggerFactory.getLogger(MutableSequenceRoll.class);
  private final Mutable<Tick> tick = new ObservableProperty<>();
  private MutableFlow<Sequence<A>, A> flow;
  private BeatClock beatClock;

  /*
   * Tick converted clock.
   */
  private Clock clock;
  private MeasureProvider beatMeasureProvider;
  private MeasureProvider measureProvider;
  private Event<A> nextEvent;
  private MovableTock tock;
  private Tock sealedTock;
  private SequenceConfiguration sequence;
  private SequenceFactory<A> sequenceFactory;
  private boolean tickDirty;
  private boolean sequenceDirty;
  private boolean typeNameDirty;
  private boolean flowDirty;
  private boolean enabled;

  /**
   * Create a base line mutable sequence roll onto which a sequence can be set and reset.
   *
   * @param clock clock
   * @param sequenceFactory sequence factory
   * @param beatMeasureProvider beat measure provider
   */
  public MutableSequenceRoll(BeatClock clock, SequenceFactory<A> sequenceFactory,
                             MeasureProvider beatMeasureProvider) {
    this.beatClock = clock;
    beatClock.addListener(this);
    this.beatMeasureProvider = beatMeasureProvider;
    this.sequenceFactory = sequenceFactory;
    assert sequenceFactory != null;
  }

  @Override
  public Property<Long> getOffsetProperty() {
    return () -> getSequence().getOffset();
  }

  /**
   * Set configuration for this sequence runtime.
   *
   * @param sequence sequence configuration
   */
  @Override
  public void setSequence(SequenceConfiguration sequence) {
    if (this.sequence == null
        || !this.sequence.getType().equals(sequence.getType())) {
      typeNameDirty = true;
    }

    /*
     * Set sequence roll active flag based on sequence active flag.  If the active flag for the
     * incoming sequence has changed then we change it in the sequence roll too - this is the
     * case where the sequence configuration was changed.  If the active flag in the incoming
     * sequence has not change then we leave the active flag as it is - this is the case where
     * a trigger might have changed and we don't want to loose the effect of this trigger.
     */
    if (this.sequence == null || this.sequence.isEnabled() != sequence.isEnabled()) {
      enabled = sequence.isEnabled();
    }

    this.sequence = sequence;
    sequenceDirty = true;
    refresh();
  }

  protected Rational getLength() {
    return Whole.valueOf(getSequence().getLength());
  }

  @Override
  public SequenceConfiguration getSequence() {
    return sequence;
  }

  /**
   * Get tick converted clock.
   *
   * @return tick converted clock.
   */
  protected Clock getClock() {
    return clock;
  }

  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  @Override
  public Property<Tick> getTick() {
    return tick;
  }

  @Override
  public void setFlow(MutableFlow<Sequence<A>, A> flow) {
    this.flow = flow;
  }

  @Override
  public MutableFlow<Sequence<A>, A> getFlow() {
    return flow;
  }


  private void afterSequenceChange() {
    /*
     * Determine if the tick has changed.
     */
    if (this.getTick() == null || !this.getTick().equals(getSequence().getTick())) {
      tickDirty = true;
      /*
       * Change tick.
       */
      tick.set(getSequence().getTick());
    }

    sequenceDirty = false;
    /*
     * Force next event to be taken from sequence flow.
     */
    nextEvent = null;
    LOG.debug("afterSequenceChange executed");
  }

  private void afterTickChange() {
    /*
     * Calculate offset of this sequence in microseconds ...
     */
    long microsecondOffset = new DefaultTickConverter(beatClock, getTick(),
        () -> Ticks.MICROSECOND,
        () -> 0L)
        .convert(Whole.valueOf(getSequence().getOffset())).floor();
    LOG.debug("Microsecond start for this sequence {} for tick offset {}", microsecondOffset,
        getSequence().getOffset());
    /*
     * ... and use this to create a converter that will convert microseconds into tock count
     * for this sequence runtime.
     */
    TickConverter microsecondToSequenceTickConverter =
        new DefaultTickConverter(beatClock, () -> Ticks.MICROSECOND, getTick(),
            () -> - microsecondOffset);

    /*
     * Create the measure provider with a tick converter converting form beats.
     */
    long beatOffset = new DefaultTickConverter(beatClock, getTick(),
        () -> Ticks.BEAT, () -> 0L).convert(Whole.valueOf(getSequence().getOffset())).floor();
    TickConverter beatToSequenceTickConverter =
        new DefaultTickConverter(beatClock, () -> Ticks.BEAT, getTick(),
            () -> - beatOffset);
    measureProvider = new ConvertedMeasureProvider(beatMeasureProvider,
        beatToSequenceTickConverter);

    /*
     * Set the tock count, that this sequence runtime should start at, to the current tock
     * count according to the beatClock.  There is no point starting the tock any earlier since
     * that time has passed.
     */
    long tockCountStart = microsecondToSequenceTickConverter
        .convert(Whole.valueOf(beatClock.getMicroseconds())).floor();
    if (tockCountStart < 0) {
      /*
       * If sequence start is the future then set tock to 0 so that it is ready to
       * start when the time is right.
       */
      tockCountStart = 0;
    }
    LOG.debug("Tock count start is {} at {}", tockCountStart, beatClock);
    setTock(Whole.valueOf(tockCountStart));

    clock = new TickConvertedClock(beatClock, tick, getOffsetProperty());
    tickDirty = false;
    LOG.debug("afterTickChange executed");
    /*
     * After a tick change the flow is dirty.
     */
    flowDirty = true;
    /*
     * ... and we can refresh the flow
     */
    refreshFlow();
  }

  @Override
  public void setTock(Whole tockToSet) {
    tock = new MovableTock(getSequence().getTick(), tockToSet);
    sealedTock = new SealedTock(tock);
    /*
     * Force next event to be taken from sequence flow.
     */
    nextEvent = null;
  }

  private void refreshFlow() {
    /*
     * Only update the flow if the flow name has changed.
     */
    if (getFlow() == null || flowDirty || typeNameDirty) {
      if (clock != null) {
        setFlow(sequenceFactory.createFlow(sequence, clock, measureProvider));
        typeNameDirty = false;
        flowDirty = false;
      } else {
        LOG.debug("Waiting until clock is set to create flow");
      }
    } else {
      sequenceFactory.refreshSequence(getFlow(), sequence);
    }
  }

  /**
   * Refresh the sequence runtime after a sequence change.
   */
  @Override
  public final void refresh() {
    if (sequenceDirty) {
      afterSequenceChange();
    }
    /*
     * Tick change handling must be after sequence change handling since the sequence may
     * change the tick.
     */
    if (beatClock.isStarted() && tickDirty) {
      afterTickChange();
    }
    /*
     * ... then after tick has changed, which may set or change the clock we can refresh the flow
     */
    refreshFlow();

    LOG.debug("Refreshed {}", this);
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
    /*
     * No action is needed on a clock stop.
     */
  }

  @Override
  public Event<A> peek() {
    if (isRolling() && nextEvent == null) {
      nextEvent = getNextEventInternal(tock);
    }
    return nextEvent;
  }

  private boolean isRolling() {
    if (tock == null) {
      LOG.trace("is rolling false : not started");
      return false;
    }
    if (!enabled) {
      LOG.trace("is rolling false : track not enabled");
      return false;
    }
    boolean result = getLength().isNegative() || tock.getPosition().lt(getLength());
    LOG.trace("isRolling {} : {} < {}", result, tock.getPosition(), getLength());
    return result;
  }

  private Event<A> getNextEventInternal(MovableTock tock) {
    Event<A> event = getNextEvent(sealedTock);
    LOG.debug("Next event after {} is at {}", tock, event.getTime());
    /*
     * Now increment internal tock to the time of the provided event
     */
    tock.setPosition(event.getTime());
    /*
     * If the response was a scan forward signal then we return a null event since no event
     * was found and we've handled the scanning forward above.
     */
    if (event instanceof ScanForwardEvent) {
      return null;
    }
    return event;
  }

  protected Event<A> getNextEvent(Tock tock) {
    return flow.getNextEvent(tock);
  }

  @Override
  public Event<A> pop() {
    Event<A> thisEvent = nextEvent;
    if (isRolling()) {
      nextEvent = getNextEventInternal(tock);
    } else {
      nextEvent = null;
    }
    return thisEvent;
  }

  @Override
  public boolean isEmpty() {
    return sequence.isEmpty() || (flow != null && flow.isEmpty());
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
