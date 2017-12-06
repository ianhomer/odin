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

import com.codahale.metrics.MetricRegistry;
import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.clock.beats.BeatsPerMinute;
import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.creation.flow.FlowFactory;
import com.purplepip.odin.creation.triggers.TriggerFactory;
import com.purplepip.odin.music.notes.Note;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Sequencer configuration that can be used as a delta over and above another sequencer
 * configuration.
 */
@Accessors(fluent = true)
public class DeltaOdinSequencerConfiguration implements OdinSequencerConfiguration {
  public static final DeltaOdinSequencerConfiguration deltaConfiguration() {
    return new DeltaOdinSequencerConfiguration();
  }

  @Getter
  @Setter
  private MeasureProvider measureProvider;

  public DeltaOdinSequencerConfiguration beatsPerMeasure(int beatsPerMeasure) {
    return measureProvider(new StaticBeatMeasureProvider(beatsPerMeasure));
  }

  @Getter
  @Setter
  private BeatsPerMinute beatsPerMinute;

  public DeltaOdinSequencerConfiguration staticBeatsPerMinute(int staticBeatsPerMinute) {
    return beatsPerMinute(new StaticBeatsPerMinute(staticBeatsPerMinute));
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
    return null;
  }

  @Override
  public OperationReceiver getOperationReceiver() {
    return null;
  }

  @Override
  public OperationTransmitter getOperationTransmitter() {
    return null;
  }

  @Override
  public MicrosecondPositionProvider getMicrosecondPositionProvider() {
    return null;
  }

  @Override
  public FlowFactory<Note> getFlowFactory() {
    return null;
  }

  @Override
  public TriggerFactory getTriggerFactory() {
    return null;
  }

  @Override
  public long getClockStartRoundingFactor() {
    return 0;
  }

  @Override
  public long getClockStartOffset() {
    return 0;
  }

  @Override
  public long getTrackProcessorRefreshPeriod() {
    return 0;
  }

  @Override
  public int getTrackProcessorMaxNotesPerBuffer() {
    return 0;
  }

  @Override
  public long getOperationProcessorRefreshPeriod() {
    return 0;
  }
}
