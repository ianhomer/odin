package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.TransientProject;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SeriesTimeUnitConverterFactory;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.logic.Logic;

import java.util.HashSet;
import java.util.Set;

/**
 * Core Odin Sequencer.
 */
public class OdinSequencer {
  private OdinSequencerConfiguration configuration;
  private Set<SequenceTrack> sequenceTracks = new HashSet<>();
  private SequenceProcessor sequenceProcessor;
  private OperationProcessor operationProcessor;
  private Clock clock;
  private boolean started = false;
  private Project project = new TransientProject();

  public OdinSequencer(OdinSequencerConfiguration configuration) throws OdinException {
    this.configuration = configuration;
    init();
  }

  private void init() {
    clock = new Clock(configuration.getBeatsPerMinute());
    clock.start(configuration.getMicrosecondPositionProvider(), true);
  }

  /**
   * Add sequence to the sequencer.
   *
   * @param sequence sequence to add.
   */
  void addSequence(Sequence<Note> sequence) {
    project.addSequence(sequence);
  }


  private void refreshTracks() {
    sequenceTracks.clear();
    for (Sequence<Note> sequence : project.getSequences()) {
      addSequenceTrack(sequence);
    }
  }

  private void addSequenceTrack(Sequence<Note> sequence) {
    DefaultSequenceRuntime sequenceRuntime = createSequenceRuntime(sequence.getLogic());
    sequenceTracks.add(new SequenceTrack(new SeriesTimeUnitConverterFactory(
        new DefaultTickConverter(clock, sequenceRuntime.getTick(), Tick.MICROSECOND,
            sequence.getOffset()))
        .convertSeries(sequenceRuntime), sequence.getChannel()));
  }

  private DefaultSequenceRuntime
      createSequenceRuntime(Logic<Sequence<Note>, Note> logic) {
    DefaultSequenceRuntime sequenceRuntime = new DefaultSequenceRuntime(logic);
    sequenceRuntime.setConfiguration(logic.getSequence());
    sequenceRuntime.setMeasureProvider(configuration.getMeasureProvider());
    return sequenceRuntime;
  }

  /**
   * Start the sequencer.
   */
  public void start() {
    started = true;
    refreshTracks();
    operationProcessor = new DefaultOperationProcessor(clock, configuration.getOperationReceiver());
    Thread thread = new Thread(operationProcessor);
    thread.start();
    sequenceProcessor = new SequenceProcessor(clock, sequenceTracks, operationProcessor);
    thread = new Thread(sequenceProcessor);
    thread.start();
  }

  public Clock getClock() {
    return clock;
  }

  /**
   * Stop the sequencer.
   */
  public void stop() {
    if (sequenceProcessor != null) {
      sequenceProcessor.stop();
    }
    if (operationProcessor != null) {
      operationProcessor.stop();
    }
    started = false;
  }

  public boolean isStarted() {
    return started;
  }
}
