package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.sequence.BeatsPerMinute;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import com.purplepip.odin.sequence.flow.FlowFactory;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Odin Sequencer configuration.
 */
public interface OdinSequencerConfiguration {
  Project getProject();

  BeatsPerMinute getBeatsPerMinute();

  MeasureProvider getMeasureProvider();

  OperationReceiver getOperationReceiver();

  MicrosecondPositionProvider getMicrosecondPositionProvider();

  FlowFactory<Note> getFlowFactory();

  long getClockStartRoundingFactor();
}
