package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.music.Note;
import com.purplepip.odin.sequence.Event;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import com.purplepip.odin.sequence.SequenceRuntime;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SequenceRuntime processor.
 */
public class SequenceProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(SequenceProcessor.class);

  private final Set<SequenceTrack> sequenceTrackSet;
  private final MicrosecondPositionProvider microsecondPositionProvider;
  private final OperationProcessor operationProcessor;
  private long refreshPeriod = 200;
  private long timeBufferInMicroSeconds = 2 * refreshPeriod * 1000;
  private int maxNotesPerBuffer = 1000;
  private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
  private SequenceProcessorExecutor executor = new SequenceProcessorExecutor();

  /**
   * Create a series processor.
   *
   * @param microsecondPositionProvider microsecond position provider
   * @param sequenceTrackSet series track set
   * @param operationProcessor operation processor
   */
  SequenceProcessor(MicrosecondPositionProvider microsecondPositionProvider,
                           Set<SequenceTrack> sequenceTrackSet,
                           OperationProcessor operationProcessor) {
    this.sequenceTrackSet = sequenceTrackSet;
    this.microsecondPositionProvider = microsecondPositionProvider;
    this.operationProcessor = operationProcessor;

    scheduledPool.scheduleWithFixedDelay(executor, 0, refreshPeriod, TimeUnit.MILLISECONDS);
  }

  /**
   * Close sequence processor.
   */
  public void close() {
    scheduledPool.shutdown();
  }

  class SequenceProcessorExecutor implements Runnable {

    /**
     * Run processor.
     */
    @Override
    public void run() {
      /*
       * Use a constant microsecond position for the whole loop to make it easier to debug
       * loop processing.  In this loop it is only used for setting forward scan windows and does
       * not need the precise microsecond positioning at the time of instruction execution.
       */
      long microsecondPosition = microsecondPositionProvider.getMicrosecondPosition();
      int noteCountThisBuffer = 0;
      for (SequenceTrack sequenceTrack : sequenceTrackSet) {
        SequenceRuntime<Note> sequenceRuntime = sequenceTrack.getSequenceRuntime();
        LOG.trace("Processing sequenceRuntime {} for device at position {}", sequenceRuntime,
            microsecondPosition);
        if (noteCountThisBuffer > maxNotesPerBuffer) {
          LOG.debug("Too many notes in this buffer {} > {} ", noteCountThisBuffer,
              maxNotesPerBuffer);
          break;
        }
        if (sequenceRuntime.peek() != null) {
          Event<Note> nextEvent = sequenceRuntime.peek();
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
            nextEvent = sequenceRuntime.pop();
            LOG.trace("Processing Event {}", nextEvent);
            if (nextEvent.getTime() < microsecondPosition) {
              LOG.debug("Skipping event, too late to process {} < {}", nextEvent.getTime(),
                  microsecondPosition);
            } else {
              sendToProcessor(nextEvent.getValue(), nextEvent, sequenceTrack);

            }
            noteCountThisBuffer++;
            nextEvent = sequenceRuntime.peek();
            LOG.trace("Next event {}", nextEvent);
          }
        } else {
          LOG.debug("No event on sequenceRuntime");
        }
      }
    }

    private void sendToProcessor(Note note, Event<Note> nextEvent, SequenceTrack sequenceTrack) {
      LOG.debug("Sending note {} to channel {} at time {}",
          note.getNumber(), sequenceTrack.getChannel(), nextEvent.getTime());
      Operation noteOn = new Operation(OperationType.ON, sequenceTrack.getChannel(),
          note.getNumber(), note.getVelocity());
      Operation noteOff = new Operation(OperationType.OFF, sequenceTrack.getChannel(),
          note.getNumber(), note.getVelocity());
      try {
        operationProcessor.send(noteOn, nextEvent.getTime());
        operationProcessor.send(noteOff, nextEvent.getTime() + note.getDuration());
      } catch (OdinException e) {
        LOG.error("Cannot send operation to processor", e);
      }
    }
  }


}
