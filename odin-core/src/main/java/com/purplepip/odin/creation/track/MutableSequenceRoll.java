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
import com.purplepip.odin.creation.action.Action;
import com.purplepip.odin.creation.action.ActionFactory;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.flow.MutableFlow;
import com.purplepip.odin.creation.sequence.Sequence;
import com.purplepip.odin.creation.sequence.SequenceConfiguration;
import com.purplepip.odin.events.Event;
import com.purplepip.odin.events.ScanForwardEvent;
import com.purplepip.odin.math.Bound;
import com.purplepip.odin.math.Rational;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.properties.beany.MutablePropertiesProvider;
import com.purplepip.odin.properties.beany.Resetter;
import com.purplepip.odin.properties.runtime.MutableProperty;
import com.purplepip.odin.properties.runtime.ObservableProperty;
import com.purplepip.odin.properties.runtime.Property;
import com.purplepip.odin.properties.thing.ThingCopy;
import java.util.HashMap;
import java.util.Map;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract sequence roll.
 */
@ListenerPriority()
@ToString(exclude = { "clock", "nextEvent", "sealedTock", "resetter",
    "flowFactory", "measureProvider", "flow", "tick"})
public class MutableSequenceRoll implements SequenceRoll, PerformanceListener {
  private static final Logger LOG = LoggerFactory.getLogger(MutableSequenceRoll.class);

  private Map<String, Action> triggers = new HashMap<>();

  private final MutableProperty<Tick> tick = new ObservableProperty<>();
  /*
   * offset is the offset of the sequence.
   */
  private final MutableProperty<Rational> offset = new ObservableProperty<>(Wholes.ZERO);
  /*
   * beat offset is the offset measure in performance beats.
   */
  private final MutableProperty<Rational> beatOffset = new ObservableProperty<>(Wholes.ZERO);

  private MutableFlow<Sequence> flow;
  private BeatClock beatClock;

  private Rational length;
  private String name;
  private boolean endless;

  /*
   * Tick converted clock.
   */
  private final Clock clock;
  private final MeasureProvider measureProvider;
  private Event nextEvent;
  private MovableTock tock;
  private Tock sealedTock;
  private final SequenceConfiguration sequence;
  private Resetter resetter = new Resetter();
  private final FlowFactory flowFactory;
  private final ActionFactory actionFactory;
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
   * @param actionFactory action factory
   * @param beatMeasureProvider beat measure provider
   */
  public MutableSequenceRoll(SequenceConfiguration sequenceConfiguration,
                             BeatClock beatClock,
                             FlowFactory flowFactory, ActionFactory actionFactory,
                             MeasureProvider beatMeasureProvider) {
    this.beatClock = beatClock;
    beatClock.addListener(this);
    this.flowFactory = flowFactory;
    this.actionFactory = actionFactory;
    assert flowFactory != null;
    tickToMicrosecondConverter = new DefaultTickConverter(beatClock, tick,
        () -> Ticks.MICROSECOND, () -> Wholes.ZERO);
    clock = new TickConvertedClock(beatClock, tick, offset);
    tickToBeatConverter = new DefaultTickConverter(beatClock, tick,
        () -> Ticks.BEAT, () -> Wholes.ZERO);
    beatToSequenceTickConverter =
        new DefaultTickConverter(beatClock, () -> Ticks.BEAT, tick, beatOffset);
    measureProvider = new ConvertedMeasureProvider(beatMeasureProvider,
        beatToSequenceTickConverter);

    typeNameDirty = true;
    this.sequence = sequenceConfiguration;
    name = sequenceConfiguration.getName();
    enabled = sequence.isEnabled();
    offset.set(sequence.getOffset());
    resetter.reset((MutablePropertiesProvider) sequence);
    sequenceDirty = true;
    refresh();
  }

  @Override
  public Property<Rational> getOffsetProperty() {
    return offset;
  }

  @Override
  public Map<String, Action> getTriggers() {
    return triggers;
  }

  @Override
  public void start() {
    LOG.debug("{} : ... start : start", name);
    setEnabled(true);
    LOG.debug("current offset of sequence is {}", sequence.getOffset());
    // Start at least two beat from now, we might reduce this lag at some point ...
    // TODO : Start in future should be based on microseconds not ticks, since tests
    // are run at very high tick rate, and this refresh needs to be in the next
    // processor execution cycle.
    // TODO : We should reduce this start increment below 20 ms.  It is currently
    // set high due to timing variability of run on build machine.  We should be able to
    // optimise system to make this more reliable.
    // TODO : Perhaps we handle fire events straight to the processors to reduce further
    // need for lag.
    // TODO : This implementation needs to be sanitised, logic is confusing and fragile.
    LOG.debug("Clock {}", clock);
    /*
     * Reset beat offset so we can work out where this new offset should be ...
     */
    offset.set(Wholes.ZERO);
    beatOffset.set(Wholes.ZERO);
    Rational newOffset = measureProvider
        .getNextMeasureStart(
            clock.getPosition(clock.getMicroseconds() + 100_000)).wholeCeiling();
    resetter.set("offset", newOffset);
    LOG.debug("{} {} : sequence offset set to {}", beatClock, name, sequence.getOffset());
    offset.set(newOffset);
    tickDirty = true;
    refresh();
    LOG.debug("{} : ... start : done", name);
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
    // TODO : Handle special properties more generically
    if (name.equals("channel")) {
      return String.valueOf(sequence.getChannel());
    }
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

    name = sequenceConfiguration.getName();

    if (sequence.getOffset() != sequenceConfiguration.getOffset()) {
      offset.set(sequenceConfiguration.getOffset());
    }

    new ThingCopy().from(sequenceConfiguration).to(sequence).copy();

    if (sequence instanceof MutablePropertiesProvider) {
      resetter.reset((MutablePropertiesProvider) sequence);
    } else {
      LOG.warn("Sequence {} is not a MutablePropertiesProvider", sequenceConfiguration);
    }
    sequenceDirty = true;
    refresh();
  }

  protected Rational getLength() {
    return length;
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
  public void setFlow(MutableFlow<Sequence> flow) {
    this.flow = flow;
  }

  @Override
  public MutableFlow<Sequence> getFlow() {
    return flow;
  }

  private void afterSequenceChange() {
    LOG.debug("{} : ... afterSequenceChange : start", name);
    sequence.initialise();
    length = getSequence().getLength();
    endless = length.isNegative();

    /*
     * Load action plugins into trigger.
     */
    triggers.clear();
    sequence.getTriggers().forEach((key, value) -> {
          triggers.put(key, value instanceof Action ? (Action) value :
              actionFactory.newInstance(value));
        }
    );

    /*
     * Determine if the tick has changed.
     */
    if (!getSequence().getTick().equals(tick.get())) {
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
    LOG.debug("{} : ... afterSequenceChange : done", name);
  }

  private void afterTickChange() {
    LOG.debug("{} : ... afterTickChange : start", name);

    /*
     * Set the beat offset.
     */
    // TODO : Can we do this conversion by maintaining rational precision?
    beatOffset.set(tickToBeatConverter.convert(offset.get()).wholeFloor().negative());

    /*
     * Calculate offset of this sequence in microseconds ...
     */
    Whole microsecondOffset = tickToMicrosecondConverter.convert(offset.get()).wholeFloor();
    LOG.debug("Microsecond start is {} for {} for tick offset {}", microsecondOffset,
        name, offset.get());

    /*
     * ... and use this to create a converter that will convert microseconds into tock count
     * for this sequence runtime.
     */
    TickConverter microsecondToSequenceTickConverter =
        new DefaultTickConverter(beatClock, () -> Ticks.MICROSECOND, tick,
            microsecondOffset::negative);
    /*
     * Set the tock count, that this sequence runtime should start at, to the current tock
     * count according to the beatClock.  There is no point starting the tock any earlier since
     * that time has passed.
     */
    long tockCountStart = microsecondToSequenceTickConverter
        .convert(Wholes.valueOf(beatClock.getMicroseconds())).floor();
    if (tockCountStart < 0) {
      /*
       * If sequence start is the future then set tock to 0 so that it is ready to
       * start when the time is right.
       */
      tockCountStart = 0;
    }
    LOG.debug("Tock count start is {} at {}", tockCountStart, beatClock);
    setTock(lessThan(Wholes.valueOf(tockCountStart)));

    tickDirty = false;
    LOG.debug("{} : ... afterTickChange : done", name);
  }

  @Override
  public void setTock(Bound tockToSet) {
    if (!endless && !tockToSet.lt(length)) {
      LOG.warn("Sequence roll {} is starting too late, no events will fire.  "
          + "Tock {} > sequence length {}", name, tockToSet, length);
    }

    tock = new MovableTock(getSequence().getTick(), tockToSet);
    sealedTock = new SealedTock(tock);
    /*
     * Force next event to be taken from sequence flow.
     */
    nextEvent = null;
  }

  private void refreshFlow() {
    LOG.debug("{} : ... refreshFlow : start", name);
    /*
     * Only update the flow if the flow name has changed.
     */
    if (flow == null || typeNameDirty) {
      setFlow(flowFactory.createFlow(sequence, clock, measureProvider));
      typeNameDirty = false;
    } else {
      flowFactory.refreshSequence(flow, sequence);
    }
    flow.initialise();
    LOG.debug("{} : ... refreshFlow : done", name);
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
    if (beatClock.isStartingOrStarted() && tickDirty) {
      afterTickChange();
    }
    /*
     * ... then after tick has changed, which may set or change the clock we can refresh the flow
     */
    refreshFlow();

    LOG.debug("Refreshed {}", this);
  }

  /**
   * Action on clock prepare.
   */
  @Override
  public void onPerformancePrepare() {
    refresh();
  }

  /**
   * Action on clock prepare.
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
  public Event peek() {
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
    return endless || tock.getPosition().lt(length);
  }

  private Event getNextEventInternal(MovableTock tock) {
    Event event = getNextEvent(sealedTock);
    LOG.debug("{} : {} is next after {}", name, event, tock);
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

  private Event getNextEvent(Tock tock) {
    return flow.getNextEvent(tock);
  }

  @Override
  public Event pop() {
    /*
     * Take event from local value since we might have peeked before we pop.
     */
    if (nextEvent != null) {
      Event thisEvent = nextEvent;
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
    return name;
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
