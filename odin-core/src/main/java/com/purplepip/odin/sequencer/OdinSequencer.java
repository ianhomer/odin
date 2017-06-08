package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.music.operations.ProgramChangeOperation;
import com.purplepip.odin.project.Project;
import com.purplepip.odin.project.ProjectListener;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.RuntimeTicks;
import com.purplepip.odin.sequence.Sequence;
import com.purplepip.odin.sequence.SeriesTimeUnitConverterFactory;
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
    clock = new Clock(configuration.getBeatsPerMinute(),
        configuration.getMicrosecondPositionProvider(),
        configuration.getClockStartRoundingFactor());
  }

  @Override
  public void onProjectApply() {
    refreshTracks();
  }

  private void refreshTracks() {
    LOG.debug("Refreshing tracks at {}micros", clock.getMicrosecondPosition());
    sequenceTracks.clear();
    for (Channel channel : configuration.getProject().getChannels()) {
      try {
        LOG.debug("Sending channel operation : {}", channel);
        operationProcessor.send(new ProgramChangeOperation(channel), -1);
      } catch (OdinException e) {
        LOG.warn("Cannot send operation", e);
      }
    }

    for (Sequence sequence : configuration.getProject().getSequences()) {
      addSequenceTrack(sequence);
    }
  }

  private void addSequenceTrack(Sequence sequence) {
    DefaultSequenceRuntime sequenceRuntime;
    try {
      sequenceRuntime = createSequenceRuntime(
          configuration.getFlowFactory().createFlow(sequence));
    } catch (OdinException e) {
      LOG.error("Cannot add sequence", e);
      return;
    }
    sequenceTracks.add(new SequenceTrack(new SeriesTimeUnitConverterFactory(
        new DefaultTickConverter(clock, sequenceRuntime.getTick(), RuntimeTicks.MICROSECOND,
            sequence.getOffset()))
        .convertSeries(sequenceRuntime), sequence.getChannel()));
  }

  private DefaultSequenceRuntime
      createSequenceRuntime(Flow<Sequence, Note> flow) {
    DefaultSequenceRuntime sequenceRuntime = new DefaultSequenceRuntime(flow);
    sequenceRuntime.setSequence(flow.getSequence());
    sequenceRuntime.setMeasureProvider(configuration.getMeasureProvider());
    return sequenceRuntime;
  }

  /**
   * Start the sequencer.
   */
  public void start() {
    started = true;
    operationProcessor = new DefaultOperationProcessor(clock, configuration.getOperationReceiver());
    sequenceProcessor = new SequenceProcessor(clock, sequenceTracks, operationProcessor);
    configuration.getProject().apply();
    clock.start();
  }

  public Clock getClock() {
    return clock;
  }

  /**
   * Stop the sequencer.
   */
  public void stop() {
    clock.stop();
    if (sequenceProcessor != null) {
      sequenceProcessor.close();
    }
    if (operationProcessor != null) {
      operationProcessor.close();
    }
    started = false;
  }

  public boolean isStarted() {
    return started;
  }
}
