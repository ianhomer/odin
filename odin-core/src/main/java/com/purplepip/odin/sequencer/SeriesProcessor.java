package com.purplepip.odin.sequencer;

import java.util.Set;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.series.Event;
import com.purplepip.odin.series.MicrosecondPositionProvider;
import com.purplepip.odin.series.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Series processor.
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

  public SeriesProcessor(MicrosecondPositionProvider microsecondPositionProvider,
                         Set<SeriesTrack> seriesTrackSet, OperationProcessor operationProcessor) {
    this.seriesTrackSet = seriesTrackSet;
    if (microsecondPositionProvider == null) {
      throw new RuntimeException("MicrosecondPositionProvider must not be null");
    }
    this.microsecondPositionProvider = microsecondPositionProvider;
    this.operationProcessor = operationProcessor;
  }

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
        Series<Note> series = seriesTrack.getSeries();
        LOG.trace("Processing series {} for device at position {}", series, microsecondPosition);
        if (noteCountThisBuffer > maxNotesPerBuffer) {
          LOG.debug("Too many notes in this buffer {} > {} ", noteCountThisBuffer,
              maxNotesPerBuffer);
          break;
        }
        if (series.peek() != null) {
          Event<Note> nextEvent = series.peek();
          while (nextEvent != null && nextEvent.getTime() <
              microsecondPosition + timeBufferInMicroSeconds) {
            if (noteCountThisBuffer > maxNotesPerBuffer) {
              LOG.debug("Too many notes in this buffer {} > {} ", noteCountThisBuffer,
                  maxNotesPerBuffer);
              break;
            }
                        /*
                         * Pop event to get it off the buffer.
                         */
            nextEvent = series.pop();
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
            nextEvent = series.peek();
            LOG.trace("Next event {}", nextEvent);
          }
        } else {
          LOG.debug("No event on series");
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
