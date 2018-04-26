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

package com.purplepip.odin.sequencer;

import static com.purplepip.odin.configuration.ActionFactories.newActionFactory;
import static com.purplepip.odin.configuration.FlowFactories.newNoteFlowFactory;
import static com.purplepip.odin.configuration.TriggerFactories.newTriggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.clock.beats.BeatsPerMinute;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.creation.action.ActionFactory;
import com.purplepip.odin.creation.flow.DefaultFlowConfiguration;
import com.purplepip.odin.creation.flow.FlowConfiguration;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.operation.OperationReceiver;
import java.util.ArrayList;

/**
 * Odin Sequencer Configuration.
 */
public class DefaultOdinSequencerConfiguration
    implements OdinSequencerConfiguration {
  private BeatsPerMinute beatsPerMinute;
  private MeasureProvider measureProvider;
  private OperationReceiver operationReceiver;
  private OperationTransmitter operationTransmitter;
  private MicrosecondPositionProvider microsecondPositionProvider;
  private boolean isLoggingOperationReceiverEnabled;
  private ActionFactory actionFactory;
  private FlowFactory flowFactory;
  private final TriggerFactory triggerFactory;
  private long clockStartRoundingFactor;
  private long clockStartOffset;
  private int maxLookForward;
  private long trackProcessorRefreshPeriod;
  private int trackProcessorMaxNotesPerBuffer;
  private long operationProcessorRefreshPeriod;
  private FlowConfiguration flowConfiguration = new DefaultFlowConfiguration();
  private MetricRegistry metrics;
  private boolean strictEventOrder;

  /**
   * Create new configuration with defaults set.
   */
  public DefaultOdinSequencerConfiguration() {
    actionFactory = newActionFactory();
    flowFactory = newNoteFlowFactory(flowConfiguration);
    triggerFactory = newTriggerFactory();
    setMeasureProvider(new StaticBeatMeasureProvider(4));
    setBeatsPerMinute(new StaticBeatsPerMinute(140));
    setLoggingOperationReceiverEnabled(true);
    setClockStartRoundingFactor(1);
    setClockStartOffset(0);
    setMaxLookForward(10_000);
    setMetrics(new MetricRegistry());
    setOperationTransmitter(new DefaultOperationTransmitter());
    setOperationProcessorRefreshPeriod(50);
    setTrackProcessorRefreshPeriod(200);
    setTrackProcessorMaxNotesPerBuffer(1000);
    setStrictEventOrder(false);
  }

  /**
   * Override configuration with non null property values in the provided override configuration.
   *
   * @param override configuration to use to override property values
   * @return this
   */
  public DefaultOdinSequencerConfiguration merge(
      OdinSequencerConfiguration override) {

    if (override.getBeatsPerMinute() != null) {
      setBeatsPerMinute(override.getBeatsPerMinute());
    }
    if (override.getClockStartOffset() > 0) {
      setClockStartOffset(override.getClockStartOffset());
    }
    return this;
  }

  public final DefaultOdinSequencerConfiguration
      setBeatsPerMinute(BeatsPerMinute beatsPerMinute) {
    this.beatsPerMinute = beatsPerMinute;
    return this;
  }

  public final DefaultOdinSequencerConfiguration
      setMeasureProvider(MeasureProvider measureProvider) {
    this.measureProvider = measureProvider;
    return this;
  }

  public final DefaultOdinSequencerConfiguration setFlowFactory(FlowFactory flowFactory) {
    this.flowFactory = flowFactory;
    return this;
  }

  public final DefaultOdinSequencerConfiguration setActionFactory(ActionFactory actionFactory) {
    this.actionFactory = actionFactory;
    return this;
  }

  /**
   * Set operation receiver.
   *
   * @param operationReceiver operation receiver
   * @return this configuration
   */
  public DefaultOdinSequencerConfiguration setOperationReceiver(
      OperationReceiver operationReceiver) {
    if (isLoggingOperationReceiverEnabled()) {
      if (operationReceiver instanceof OperationReceiverCollection) {
        ArrayList<OperationReceiver> operationReceiverList =
            new ArrayList<>(Lists.newArrayList(operationReceiver));
        operationReceiverList.add(new LoggingOperationReceiver());
        this.operationReceiver = new OperationReceiverCollection(operationReceiverList);
      } else {
        this.operationReceiver = new OperationReceiverCollection(operationReceiver,
            new LoggingOperationReceiver());
      }
    } else {
      this.operationReceiver = operationReceiver;
    }
    return this;
  }

  /*
   * TODO : Should transmitter and receiver implementations look the same, they are really
   * the same just input and output so should support same functionality.
   */
  public final DefaultOdinSequencerConfiguration setOperationTransmitter(
      OperationTransmitter operationTransmitter) {
    this.operationTransmitter = operationTransmitter;
    return this;
  }

  public final DefaultOdinSequencerConfiguration setMicrosecondPositionProvider(
      MicrosecondPositionProvider microsecondPositionProvider) {
    this.microsecondPositionProvider = microsecondPositionProvider;
    return this;
  }

  @Override
  public BeatsPerMinute getBeatsPerMinute() {
    return beatsPerMinute;
  }

  @Override
  public MeasureProvider getMeasureProvider() {
    return measureProvider;
  }

  @Override
  public MetricRegistry getMetrics() {
    return metrics;
  }

  public final DefaultOdinSequencerConfiguration setMetrics(MetricRegistry metrics) {
    this.metrics = metrics;
    return this;
  }

  @Override
  public OperationReceiver getOperationReceiver() {
    return operationReceiver;
  }

  @Override
  public OperationTransmitter getOperationTransmitter() {
    return operationTransmitter;
  }

  @Override
  public MicrosecondPositionProvider getMicrosecondPositionProvider() {
    return microsecondPositionProvider;
  }

  @Override
  public FlowFactory getFlowFactory() {
    return flowFactory;
  }

  @Override
  public ActionFactory getActionFactory() {
    return actionFactory;
  }

  @Override
  public TriggerFactory getTriggerFactory() {
    return triggerFactory;
  }

  @Override
  public long getClockStartRoundingFactor() {
    return clockStartRoundingFactor;
  }

  public final DefaultOdinSequencerConfiguration setClockStartRoundingFactor(
      long clockStartRoundingFactor) {
    this.clockStartRoundingFactor = clockStartRoundingFactor;
    return this;
  }

  @Override
  public long getClockStartOffset() {
    return clockStartOffset;
  }

  @Override
  public long getMaxLookForward() {
    return maxLookForward;
  }

  public DefaultOdinSequencerConfiguration setMaxLookForward(int maxLookForward) {
    this.maxLookForward = maxLookForward;
    return this;
  }

  @Override
  public long getTrackProcessorRefreshPeriod() {
    return trackProcessorRefreshPeriod;
  }

  public DefaultOdinSequencerConfiguration setTrackProcessorRefreshPeriod(
      long trackProcessorRefreshPeriod) {
    this.trackProcessorRefreshPeriod = trackProcessorRefreshPeriod;
    return this;
  }

  @Override
  public int getTrackProcessorMaxNotesPerBuffer() {
    return trackProcessorMaxNotesPerBuffer;
  }

  public DefaultOdinSequencerConfiguration setTrackProcessorMaxNotesPerBuffer(
      int trackProcessorMaxNotesPerBuffer) {
    this.trackProcessorMaxNotesPerBuffer = trackProcessorMaxNotesPerBuffer;
    return this;
  }

  @Override
  public long getOperationProcessorRefreshPeriod() {
    return operationProcessorRefreshPeriod;
  }

  public DefaultOdinSequencerConfiguration setOperationProcessorRefreshPeriod(
      long operationProcessorRefreshPeriod) {
    this.operationProcessorRefreshPeriod = operationProcessorRefreshPeriod;
    return this;
  }

  public final DefaultOdinSequencerConfiguration setFlowConfiguration(
      FlowConfiguration flowConfiguration) {
    this.flowConfiguration = flowConfiguration;
    return this;
  }

  public final DefaultOdinSequencerConfiguration setClockStartOffset(
      long clockStartOffset) {
    this.clockStartOffset = clockStartOffset;
    return this;
  }

  /**
   * Get whether logging operation receiver is automatically added.
   *
   * @return whether logging operation receiver is automatically added
   */
  public boolean isLoggingOperationReceiverEnabled() {
    return  isLoggingOperationReceiverEnabled;
  }

  /**
   * Set whether logging operation receiver is automatically added.
   *
   * @param isLoggingOperationReceiverEnabled is logging operation receiver is automatically added
   * @return this configuration
   */
  public final DefaultOdinSequencerConfiguration setLoggingOperationReceiverEnabled(
      boolean isLoggingOperationReceiverEnabled) {
    this.isLoggingOperationReceiverEnabled = isLoggingOperationReceiverEnabled;
    return this;
  }

  public final boolean isStrictEventOrder() {
    return strictEventOrder;
  }

  public final DefaultOdinSequencerConfiguration setStrictEventOrder(
      boolean strictEventOrder) {
    this.strictEventOrder = strictEventOrder;
    return this;
  }
}
