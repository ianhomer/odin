package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Meter;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.SequenceRuntime;
import com.purplepip.odin.sequence.SeriesTimeUnitConverterFactory;
import com.purplepip.odin.sequence.Tick;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core Odin Sequencer.
 */
public class OdinSequencer {
  private static final Logger LOG = LoggerFactory.getLogger(OdinSequencer.class);

  private OdinSequencerConfiguration configuration;
  private Set<SequenceTrack> sequenceTrackSet = new HashSet<>();
  private SequenceProcessor sequenceProcessor;
  private OperationProcessor operationProcessor;
  private Clock clock;
  private Meter meter;

  public OdinSequencer(OdinSequencerConfiguration configuration) throws OdinException {
    this.configuration = configuration;
    init();
  }

  private void init() {
    clock = new Clock(configuration.getBeatsPerMinute());
    clock.start(configuration.getMicrosecondPositionProvider(), true);
    meter = new Meter(clock, configuration.getMeasureProvider());
  }

  public void addSequence(SequenceRuntime<Note> sequenceRuntime, long offset) throws OdinException {
    addSequence(sequenceRuntime, offset, 0);
  }

  /**
   * Add sequenceRuntime at the given time offset, where offset is in the time units of the
   * sequence runtime being added.
   *
   * @param sequenceRuntime sequence runtime to add.
   * @param offset offset to add the sequenceRuntime to.
   * @throws OdinException exception
   */
  public void addSequence(SequenceRuntime<Note> sequenceRuntime, long offset, int channel) {
    LOG.debug("Adding sequence runtime {} with time units {}",
        sequenceRuntime.getClass().getSimpleName(),
        sequenceRuntime.getTick().getClass().getSimpleName());
    sequenceTrackSet.add(new SequenceTrack(new SeriesTimeUnitConverterFactory(
        new DefaultTickConverter(clock, sequenceRuntime.getTick(), Tick.MICROSECOND, offset))
        .convertSeries(sequenceRuntime), channel));
  }

  /**
   * Start the sequencer.
   */
  public void start() {
    operationProcessor = new DefaultOperationProcessor(clock, configuration.getOperationReceiver());
    Thread thread = new Thread(operationProcessor);
    thread.start();
    sequenceProcessor = new SequenceProcessor(clock, sequenceTrackSet, operationProcessor);
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
  }
}
