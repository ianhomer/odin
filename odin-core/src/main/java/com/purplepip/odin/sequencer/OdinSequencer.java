package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Meter;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Clock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.Sequence;
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
  private Set<SeriesTrack> seriesTrackSet = new HashSet<>();
  private SeriesProcessor seriesProcessor;
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

  public void addSeries(Sequence<Note> sequence, long offset) throws OdinException {
    addSeries(sequence, offset, 0);
  }

  /**
   * Add sequence at the given time offset, where offset is in the time units of the sequence being
   * added.
   *
   * @param sequence sequence to add.
   * @param offset offset to add the sequence to.
   * @throws OdinException exception
   */
  public void addSeries(Sequence<Note> sequence, long offset, int channel) {
    LOG.debug("Adding sequence {} with time units {}", sequence, sequence.getTick());
    seriesTrackSet.add(new SeriesTrack(new SeriesTimeUnitConverterFactory(
        new DefaultTickConverter(clock, sequence.getTick(), Tick.MICROSECOND, offset))
        .convertSeries(sequence), channel));
  }

  /**
   * Start the sequencer.
   */
  public void start() {
    operationProcessor = new DefaultOperationProcessor(clock, configuration.getOperationReceiver());
    Thread thread = new Thread(operationProcessor);
    thread.start();
    seriesProcessor = new SeriesProcessor(clock, seriesTrackSet, operationProcessor);
    thread = new Thread(seriesProcessor);
    thread.start();
  }

  public Clock getClock() {
    return clock;
  }

  /**
   * Stop the sequencer.
   */
  public void stop() {
    if (seriesProcessor != null) {
      seriesProcessor.stop();
    }
    if (operationProcessor != null) {
      operationProcessor.stop();
    }
  }
}
