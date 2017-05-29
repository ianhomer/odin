package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectListener;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SeriesTimeUnitConverterFactory;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequence.flow.Flow;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core Odin Sequencer.
 */
public class OdinSequencer implements ProjectListener {
  private static final Logger LOG = LoggerFactory.getLogger(OdinSequencer.class);

  private OdinSequencerConfiguration configuration;
  private Set<SequenceTrack> sequenceTracks = new HashSet<>();
  private SequenceProcessor sequenceProcessor;
  private OperationProcessor operationProcessor;
  private Clock clock;
  private boolean started = false;

  /**
   * Create an Odin sequencer.
   *
   * @param configuration configuration for the sequencer
   * @throws OdinException exception
   */
  public OdinSequencer(OdinSequencerConfiguration configuration)
      throws OdinException {
    this.configuration = configuration;
    init();
  }

  public Project getProject() {
    return configuration.getProject();
  }

  private void init() {
    configuration.getProject().addListener(this);
    clock = new Clock(configuration.getBeatsPerMinute());
    clock.start(configuration.getMicrosecondPositionProvider(), true);
  }

  /**
   * Add sequence to the sequencer.
   *
   * @param sequence sequence to add.
   */
  void addSequence(Sequence<Note> sequence) {
    configuration.getProject().addSequence(sequence);
  }

  @Override
  public void onProjectApply() {
    refreshTracks();
  }

  private void refreshTracks() {
    sequenceTracks.clear();
    for (Sequence<Note> sequence : configuration.getProject().getSequences()) {
      addSequenceTrack(sequence);
    }
  }

  private void addSequenceTrack(Sequence<Note> sequence) {
    DefaultSequenceRuntime sequenceRuntime = null;
    try {
      sequenceRuntime = createSequenceRuntime(
          configuration.getFlowFactory().createFlow(sequence));
    } catch (OdinException e) {
      LOG.error("Cannot add sequence", e);
    }
    sequenceTracks.add(new SequenceTrack(new SeriesTimeUnitConverterFactory(
        new DefaultTickConverter(clock, sequenceRuntime.getTick(), Ticks.MICROSECOND,
            sequence.getOffset()))
        .convertSeries(sequenceRuntime), sequence.getChannel()));
  }

  private DefaultSequenceRuntime
      createSequenceRuntime(Flow<Sequence<Note>, Note> flow) {
    DefaultSequenceRuntime sequenceRuntime = new DefaultSequenceRuntime(flow);
    sequenceRuntime.setConfiguration(flow.getSequence());
    sequenceRuntime.setMeasureProvider(configuration.getMeasureProvider());
    return sequenceRuntime;
  }

  /**
   * Start the sequencer.
   */
  public void start() {
    started = true;
    configuration.getProject().apply();
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
