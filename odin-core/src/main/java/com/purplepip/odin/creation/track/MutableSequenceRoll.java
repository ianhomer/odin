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
import com.purplepip.odin.properties.thing.ThingCopy;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sequence roll.
 */
@ListenerPriority()
@ToString(exclude = { "clock", "nextEvent", "sealedTock", "resetter",
    "flowFactory", "measureProvider", "flow", "tick"})
public class MutableSequenceRoll<A> implements SequenceRoll<A>, PerformanceListener {
  private static final Logger LOG = LoggerFactory.getLogger(MutableSequenceRoll.class);

  private final MutableProperty<Tick> tick = new ObservableProperty<>();
  /*
   * offset is the offset of the sequence.
   */
  private final MutableProperty<Long> offset = new ObservableProperty<>(0L);
  /*
   * beat offset is the offset measure in performance beats.
   */
  private final MutableProperty<Long> beatOffset = new ObservableProperty<>(0L);

  private MutableFlow<Sequence<A>, A> flow;
  private BeatClock beatClock;

  /*
   * Tick converted clock.
   */
  private final Clock clock;
  private final MeasureProvider measureProvider;
  private Event<A> nextEvent;
  private MovableTock tock;
  private Tock sealedTock;
  private final SequenceConfiguration sequence;
  private Resetter resetter = new Resetter();
  private FlowFactory<A> flowFactory;
  private boolean tickDirty;
  private boolean sequenceDirty;
  private boolean typeNameDirty;
  private boolean enabled;

  private final TickConverter tickToMicrosecondConverter;
  private final TickConverter tickToBeatConverter;
  private final TickConverter beatToSequenceTickConverter;

  /**
   * Create a base line mutable sequence roll onto which a sequence can be set and reset.
   *
   * @param beatClock clock
   * @param flowFactory flow factory
   * @param beatMeasureProvider beat measure provider
   */
  public MutableSequenceRoll(SequenceConfiguration sequenceConfiguration,
                             BeatClock beatClock, FlowFactory<A> flowFactory,
                             MeasureProvider beatMeasureProvider) {
    this.beatClock = beatClock;
    beatClock.addListener(this);
    this.flowFactory = flowFactory;
    assert flowFactory != null;
    tickToMicrosecondConverter = new DefaultTickConverter(beatClock, getTick(),
        () -> Ticks.MICROSECOND, () -> 0L);
    clock = new TickConvertedClock(beatClock, getTick(), getOffsetProperty());
    tickToBeatConverter = new DefaultTickConverter(beatClock, getTick(),
        () -> Ticks.BEAT, () -> 0L);
    beatToSequenceTickConverter =
        new DefaultTickConverter(beatClock, () -> Ticks.BEAT, getTick(), beatOffset);
    measureProvider = new ConvertedMeasureProvider(beatMeasureProvider,
        beatToSequenceTickConverter);

    typeNameDirty = true;
    this.sequence = sequenceConfiguration;
    enabled = sequence.isEnabled();
    offset.set(sequence.getOffset());
    resetter.reset((MutablePropertiesProvider) sequence);
    sequenceDirty = true;
    refresh();
  }

  @Override
  public Property<Long> getOffsetProperty() {
    return offset;
  }

  @Override
  public void start() {
    LOG.debug("{} : ... start : start", sequence.getName());
    setEnabled(true);
    LOG.debug("current offset of sequence is {}", sequence.getOffset());
    // Start at least two beat from now, we might reduce this lag at some point ...
    // TODO : Start in future should be based on microseconds not ticks, since tests
    // are run at very high tick rate, and this refresh needs to be in the next
    // processor execution cycle.
    // TODO : We should reduce this start increment below 20 ms.  It is currently
    // set high due to timing variability of run on build machine.  We should be able to
    // optimise system to make this more reliable.
    // TODO : Perhaps we send fire events straight to the processors to reduce further
    // need for lag.
    // TODO : This implementation needs to be sanitised, logic is confusing and fragile.
    LOG.debug("Clock {}", clock);
    /*
     * Reset beat offset so we can work out where this new offset should be ...
     */
    offset.set(0L);
    beatOffset.set(0L);
    long newOffset = measureProvider
        .getNextMeasureStart(
            clock.getPosition(clock.getMicroseconds() + 20_000)).ceiling();

    resetter.set("offset", newOffset);
    LOG.debug("{} {} : sequence offset set to {}", beatClock, sequence.getName(),
        sequence.getOffset());
    offset.set(newOffset);
    tickDirty = true;
    refresh();
    LOG.debug("{} : ... start : done", sequence.getName());
  }

  @Override
  public void stop() {
    setEnabled(false);
  }

  @Override
  public void initialise() {
    flow.initialise();
  }

  @Override
  public void setProperty(String name, String value) {
    resetter.set(name, value);
  }

  @Override
  public String getProperty(String name) {
    return sequence.getProperty(name);
  }

  /**
   * Set configuration for this sequence runtime.
   *
   * @param sequenceConfiguration sequence configuration
   */
  @Override
  public void setSequence(SequenceConfiguration sequenceConfiguration) {
    if (sequence.getType().equals(sequenceConfiguration.getType())) {
      typeNameDirty = true;
    }

    /*
     * Set sequence roll active flag based on sequence active flag.  If the active flag for the
     * incoming sequence has changed then we change it in the sequence roll too - this is the
     * case where the sequence configuration was changed.  If the active flag in the incoming
     * sequence has not change then we leave the active flag as it is - this is the case where
     * a trigger might have changed and we don't want to loose the effect of this trigger.
     */
    if (sequence.isEnabled() != sequenceConfiguration.isEnabled()) {
      enabled = sequenceConfiguration.isEnabled();
    }

    if (sequence.getOffset() != sequenceConfiguration.getOffset()) {
      offset.set(sequenceConfiguration.getOffset());
    }

    new ThingCopy().from(sequenceConfiguration).to(sequence).copy();

    if (sequenceConfiguration instanceof MutablePropertiesProvider) {
      resetter.reset((MutablePropertiesProvider) sequenceConfiguration);
    } else {
      LOG.warn("Sequence {} is not a MutablePropertiesProvider", sequenceConfiguration);
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
    sequence.initialise();

    /*
     * Determine if the tick has changed.
     */
    if (!getSequence().getTick().equals(this.getTick().get())) {
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
     * Set the beat offset.
     */
    beatOffset.set(-tickToBeatConverter
        .convert(Whole.valueOf(offset.get())).floor());

    /*
     * Calculate offset of this sequence in microseconds ...
     */
    long microsecondOffset = tickToMicrosecondConverter
        .convert(Whole.valueOf(offset.get())).floor();
    LOG.debug("Microsecond start for this sequence {} for tick offset {}", microsecondOffset,
        offset.get());

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
    if (getFlow() == null || typeNameDirty) {
      if (clock != null) {
        setFlow(flowFactory.createFlow(sequence, clock, measureProvider));
        typeNameDirty = false;
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
