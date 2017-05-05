package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Meter;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultSequenceRuntime;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.MutableSequenceRuntime;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SeriesTimeUnitConverterFactory;
import com.purplepip.odin.sequence.Tick;
import com.purplepip.odin.sequence.logic.Logic;

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

  private <S extends Sequence, L extends Logic<S, Note>>
      MutableSequenceRuntime<S, Note>
      createMutableSequenceRuntime(Class<S> clazz, L logic) {
    MutableSequenceRuntime<S, Note> sequenceRuntime = new DefaultSequenceRuntime<>(logic);
    sequenceRuntime.setConfiguration(logic.getSequence());
    sequenceRuntime.setMeasureProvider(configuration.getMeasureProvider());
    return sequenceRuntime;
  }

  /**
   * Add sequenceRuntime at the given time offset, where offset is in the time units of the
   * sequence runtime being added.
   *
   * @param logic sequence logic to add.
   * @param offset offset to add the sequenceRuntime to.
   * @param channel channel to send the sequence to.
   * @throws OdinException exception
   */
  <S extends Sequence> void addSequence(Class<S> clazz, Logic<S, Note> logic, long offset,
                                        int channel) {
    // TODO : Pass Sequence in NOT Logic and create a Logic Factory to create appropriate Logic
    // from Sequence, then this OdinSequencer can take responsibility for persisting Sequence
    // configuration and re-spin up from the configuration.
    LOG.debug("Adding sequence runtime {} with time units {}",
        clazz.getSimpleName(),
        logic.getSequence().getTick().getClass().getSimpleName());
    MutableSequenceRuntime<S, Note> sequenceRuntime = createMutableSequenceRuntime(
        clazz, logic);
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
