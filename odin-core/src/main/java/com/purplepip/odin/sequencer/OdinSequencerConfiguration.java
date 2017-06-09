package com.purplepip.odin.sequencer;

import com.purplepip.odin.music.Note;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.BeatsPerMinute;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import com.purplepip.odin.sequence.flow.FlowFactory;
import com.purplepip.odin.sequence.measure.MeasureProvider;

/**
 * Odin Sequencer configuration.
 */
public interface OdinSequencerConfiguration {
  ProjectContainer getProjectContainer();

  BeatsPerMinute getBeatsPerMinute();

  MeasureProvider getMeasureProvider();

  OperationReceiver getOperationReceiver();

  MicrosecondPositionProvider getMicrosecondPositionProvider();

  FlowFactory<Note> getFlowFactory();

  long getClockStartRoundingFactor();

  /*
   * Get clock start offset.  This is can be used as a count in period.  Technically it can
   * give time for the processors to start and determine first events before the clock starts.
   */
  long getClockStartOffset();
}
