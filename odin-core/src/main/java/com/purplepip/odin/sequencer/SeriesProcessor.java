package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import com.purplepip.odin.sequence.Sequence;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sequence processor.
 */
public class SeriesProcessor implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(SeriesProcessor.class);

  private Set<SeriesTrack> seriesTrackSet;
  private MicrosecondPositionProvider microsecondPositionProvider;
  private OperationProcessor operationProcessor;
  private boolean exit;
  // TODO : Externalise configuration
  private long refreshPeriod = 200;
  private long timeBufferInMicroSeconds = 2 * refreshPeriod * 1000;
  private int maxNotesPerBuffer = 1000;

  /**
   * Create a series processor.
   *
   * @param microsecondPositionProvider microsecond position provider
   * @param seriesTrackSet series track set
   * @param operationProcessor operation processor
   */
  public SeriesProcessor(MicrosecondPositionProvider microsecondPositionProvider,
                         Set<SeriesTrack> seriesTrackSet, OperationProcessor operationProcessor) {
    this.seriesTrackSet = seriesTrackSet;
    if (microsecondPositionProvider == null) {
      throw new RuntimeException("MicrosecondPositionProvider must not be null");
    }
    this.microsecondPositionProvider = microsecondPositionProvider;
    this.operationProcessor = operationProcessor;
  }

  /**
   * Run processor.
   */
  public void run() {
    while (!exit) {
      /*
       * Use a constant microsecond position for the whole loop to make it easier to debug
       * loop processing.  In this loop it is only used for setting forward scan windows and does
       * not need the precise microsecond positioning at the time of instruction execution.
       */
      long microsecondPosition = microsecondPositionProvider.getMicrosecondPosition();
      int noteCountThisBuffer = 0;
      for (SeriesTrack seriesTrack : seriesTrackSet) {
        Sequence<Note> sequence = seriesTrack.getSequence();
        LOG.trace("Processing sequence {} for device at position {}", sequence,
            microsecondPosition);
        if (noteCountThisBuffer > maxNotesPerBuffer) {
          LOG.debug("Too many notes in this buffer {} > {} ", noteCountThisBuffer,
              maxNotesPerBuffer);
          break;
        }
        if (sequence.peek() != null) {
          Event<Note> nextEvent = sequence.peek();
          while (nextEvent != null && nextEvent.getTime()
              < microsecondPosition + timeBufferInMicroSeconds) {
            if (noteCountThisBuffer > maxNotesPerBuffer) {
              LOG.debug("Too many notes in this buffer {} > {} ", noteCountThisBuffer,
                  maxNotesPerBuffer);
              break;
            }
            /*
             * Pop event to get it off the buffer.
             */
            nextEvent = sequence.pop();
            LOG.trace("Processing Event {}", nextEvent);
            if (nextEvent.getTime() < microsecondPosition) {
              LOG.debug("Skipping event, too late to process {} < {}", nextEvent.getTime(),
                  microsecondPosition);
            } else {
              Note note = nextEvent.getValue();
              LOG.debug("Sending note {} to {} ; {}",
                  note.getNumber(), seriesTrack.getChannel(), nextEvent.getTime());
              Operation noteOn = new Operation(OperationType.ON, seriesTrack.getChannel(),
                  note.getNumber(), note.getVelocity());
              Operation noteOff = new Operation(OperationType.OFF, seriesTrack.getChannel(),
                  note.getNumber(), note.getVelocity());
              try {
                operationProcessor.send(noteOn, nextEvent.getTime());
                operationProcessor.send(noteOff, nextEvent.getTime() + note.getDuration());
              } catch (OdinException e) {
                LOG.error("Cannot send operation to processor", e);
              }
            }
            noteCountThisBuffer++;
            nextEvent = sequence.peek();
            LOG.trace("Next event {}", nextEvent);
          }
        } else {
          LOG.debug("No event on sequence");
        }
      }
      try {
        Thread.sleep(refreshPeriod);
      } catch (InterruptedException e) {
        LOG.error("Thread interrupted", e);
      }
    }
  }

  public void stop() {
    exit = true;
  }
}
