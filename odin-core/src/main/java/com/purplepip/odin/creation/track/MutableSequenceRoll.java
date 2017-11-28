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

package com.purplepip.odin.creation.track;

import static com.purplepip.odin.math.LessThan.lessThan;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.Clock;
import com.purplepip.odin.clock.PerformanceListener;
import com.purplepip.odin.clock.TickConvertedClock;
import com.purplepip.odin.clock.measure.ConvertedMeasureProvider;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.tick.DefaultTickConverter;
import com.purplepip.odin.clock.tick.MovableTock;
import com.purplepip.odin.clock.tick.SealedTock;
import com.purplepip.odin.clock.tick.Tick;
import com.purplepip.odin.clock.tick.TickConverter;
import com.purplepip.odin.clock.tick.Ticks;
import com.purplepip.odin.clock.tick.Tock;
import com.purplepip.odin.common.ListenerPriority;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.flow.MutableFlow;
import com.purplepip.odin.creation.sequence.Sequence;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.ScanForwardEvent;
import com.purplepip.odin.math.Bound;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.properties.beany.MutablePropertiesProvider;
import com.purplepip.odin.properties.beany.Resetter;
import com.purplepip.odin.properties.runtime.MutableProperty;
import com.purplepip.odin.properties.runtime.ObservableProperty;
import com.purplepip.odin.properties.runtime.Property;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sequence roll.
 */
@ListenerPriority()
@ToString(exclude = { "clock", "beatMeasureProvider", "nextEvent", "sealedTock", "resetter",
    "flowFactory", "measureProvider", "flow", "tick"})
public class MutableSequenceRoll<A> implements SequenceRoll<A>, PerformanceListener {
  private static final Logger LOG = LoggerFactory.getLogger(MutableSequenceRoll.class);

  private final MutableProperty<Tick> tick = new ObservableProperty<>();
  private final MutableProperty<Long> offset = new ObservableProperty<>(0L);

  private MutableFlow<Sequence<A>, A> flow;
  private BeatClock beatClock;

  /*
   * Tick converted clock.
   */
  private final Clock clock;

  private MeasureProvider beatMeasureProvider;
  private MeasureProvider measureProvider;
  private Event<A> nextEvent;
  private MovableTock tock;
  private Tock sealedTock;
  private SequenceConfiguration sequence;
  private Resetter resetter = new Resetter();
  private FlowFactory<A> flowFactory;
  private boolean tickDirty;
  private boolean sequenceDirty;
  private boolean typeNameDirty;
  private boolean flowDirty;
  private boolean enabled;

  private final TickConverter tickToMicrosecondConverter;
  private final TickConverter tickToBeatConverter;

  /**
   * Create a base line mutable sequence roll onto which a sequence can be set and reset.
   *
   * @param beatClock clock
   * @param flowFactory flow factory
   * @param beatMeasureProvider beat measure provider
   */
  public MutableSequenceRoll(BeatClock beatClock, FlowFactory<A> flowFactory,
                             MeasureProvider beatMeasureProvider) {
    this.beatClock = beatClock;
    beatClock.addListener(this);
    this.beatMeasureProvider = beatMeasureProvider;
    this.flowFactory = flowFactory;
    assert flowFactory != null;
    tickToMicrosecondConverter = new DefaultTickConverter(beatClock, getTick(),
        () -> Ticks.MICROSECOND, () -> 0L);
    clock = new TickConvertedClock(beatClock, getTick(), getOffsetProperty());
    tickToBeatConverter = new DefaultTickConverter(beatClock, getTick(),
        () -> Ticks.BEAT, () -> 0L);
  }

  @Override
  public Property<Long> getOffsetProperty() {
    return () -> getSequence().getOffset();
  }

  @Override
  public void start() {
    LOG.debug("{} : ... start : start", sequence.getName());
    setEnabled(true);
    refresh();
    LOG.debug("current offset of sequence is {}", sequence.getOffset());
    // Start at least two beat from now, we might reduce this lag at some point ...
    // TODO : Start in future should be based on microseconds not ticks, since tests
    // are run at very high tick rate, and this refresh needs to be in the next
    // processor execution cycle.
    LOG.debug("Clock {}", clock);
    long newOffset = measureProvider
        .getNextMeasureStart(
            clock.getPosition(clock.getMicroseconds() + 10_000)).ceiling();

    resetter.set("offset", newOffset);
    LOG.debug("{} {} : sequence offset set to {} at", beatClock, sequence.getName(),
        sequence.getOffset());
    tickDirty = true;
    refresh();
    LOG.debug("{} : ... start : done", sequence.getName());
  }

  @Override
  public void stop() {
    setEnabled(false);
  }

  @Override
  public void reset() {
    flow.reset();
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

    if (this.sequence == null || this.sequence.getOffset() != sequence.getOffset()) {
      offset.set(sequence.getOffset());
    }

    // TODO : We currently do setSequence(sequence.copy()) in  SequenceRollTrack when
    // sequence is set.  Perhaps we should use sequence.copy() in this method instead
    // to ensure use is predictable.
    this.sequence = sequence;
    if (sequence instanceof MutablePropertiesProvider) {
      resetter.reset((MutablePropertiesProvider) sequence);
    } else {
      resetter.reset(null);
    }
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
    LOG.debug("{} : ... afterSequenceChange : start", sequence.getName());

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
    LOG.debug("{} : ... afterSequenceChange : done", sequence.getName());
  }

  private void afterTickChange() {
    LOG.debug("{} : ... afterTickChange : start", sequence.getName());
    /*
     * Calculate offset of this sequence in microseconds ...
     */
    long microsecondOffset = tickToMicrosecondConverter
        .convert(Whole.valueOf(getSequence().getOffset())).floor();
    LOG.debug("Microsecond start for this sequence {} for tick offset {}", microsecondOffset,
        getSequence().getOffset());

    /*
     * Create the measure provider with a tick converter converting from beats.
     */
    long beatOffset = tickToBeatConverter
        .convert(Whole.valueOf(getSequence().getOffset())).floor();
    TickConverter beatToSequenceTickConverter =
        new DefaultTickConverter(beatClock, () -> Ticks.BEAT, getTick(),
            () -> - beatOffset);
    measureProvider = new ConvertedMeasureProvider(beatMeasureProvider,
        beatToSequenceTickConverter);

    /*
     * ... and use this to create a converter that will convert microseconds into tock count
     * for this sequence runtime.
     */
    TickConverter microsecondToSequenceTickConverter =
        new DefaultTickConverter(beatClock, () -> Ticks.MICROSECOND, getTick(),
            () -> - microsecondOffset);
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
    setTock(lessThan(Whole.valueOf(tockCountStart)));

    tickDirty = false;
    /*
     * After a tick change the flow is dirty.
     */
    flowDirty = true;
    LOG.debug("{} : ... afterTickChange : done", sequence.getName());
  }

  @Override
  public void setTock(Bound tockToSet) {
    if (sequence.getLength() > -1 && !tockToSet.lt(Whole.valueOf(sequence.getLength()))) {
      LOG.warn("Sequence roll is starting too late, no events will fire.  "
          + "Tock {} > sequence length {}", tockToSet, sequence.getLength());
    }

    tock = new MovableTock(getSequence().getTick(), tockToSet);
    sealedTock = new SealedTock(tock);
    /*
     * Force next event to be taken from sequence flow.
     */
    nextEvent = null;
  }

  private void refreshFlow() {
    LOG.debug("{} : ... refreshFlow : start", sequence.getName());
    /*
     * Only update the flow if the flow name has changed.
     */
    if (getFlow() == null || flowDirty || typeNameDirty) {
      if (clock != null) {
        setFlow(flowFactory.createFlow(sequence, clock, measureProvider));
        typeNameDirty = false;
        flowDirty = false;
      } else {
        LOG.trace("Waiting until clock is set to create flow");
      }
    } else {
      flowFactory.refreshSequence(getFlow(), sequence);
    }
    LOG.debug("{} : ... refreshFlow : done", sequence.getName());
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
  public void onPerformanceStart() {
    refresh();
  }

  /**
   * Action on beatClock stop.
   */
  @Override
  public void onPerformanceStop() {
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
    LOG.trace("{} : isRolling {} : {} < {}", sequence.getName(),
        result, tock.getPosition(), getLength());
    return result;
  }

  private Event<A> getNextEventInternal(MovableTock tock) {
    Event<A> event = getNextEvent(sealedTock);
    LOG.debug("{} : {} is next after {}", sequence.getName(), event, tock);
    /*
     * Now increment internal tock to the time of the provided event
     */
    tock.setPosition(event.getTime());
    /*
     * If the response was a scan forward signal then we return a null event since no event
     * was found and we've handled the scanning forward above.
     */
    if (event instanceof ScanForwardEvent) {
      LOG.trace("Event is a scan forward event");
      return null;
    }
    return event;
  }

  private Event<A> getNextEvent(Tock tock) {
    return flow.getNextEvent(tock);
  }

  @Override
  public Event<A> pop() {
    /*
     * Take event from local value since we might have peeked before we pop.
     */
    if (nextEvent != null) {
      Event<A> thisEvent = nextEvent;
      nextEvent = null;
      return thisEvent;
    }
    /*
     * Otherwise we'll get it directly.
     */
    if (isRolling()) {
      return getNextEventInternal(tock);
    } else {
      return null;
    }
  }

  @Override
  public boolean isEmpty() {
    return sequence.isEmpty() || (flow != null && flow.isEmpty());
  }

  @Override
  public String getName() {
    return sequence.getName();
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
