package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;

import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor responsible for taking MIDI messages off the queue and sending them to the MIDI
 * receivers in a timely manner.  Note that according to the Java MIDI specification messages
 * should not be sent to devices too early.  The time argument in the receiver send method call is
 * really for synchronisation.  If events are fired into a receiver too early then the MIDI
 * instrument might end up handling them early.
 */
public class DefaultOperationProcessor implements OperationProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultOperationProcessor.class);

  private boolean exit = false;
  private long refreshPeriod = 10;
  private long forwardPollingTime = refreshPeriod * 1000 * 5;
  private PriorityQueue<OperationEvent> queue = new PriorityQueue<>(127,
      new OperationEventComparator());
  private MicrosecondPositionProvider microsecondPositionProvider;
  private OperationReceiver operationReceiver;

  /**
   * Create an operation processor.
   *
   * @param microsecondPositionProvider microsecond position provider
   * @param operationReceiver operation receiver
   */
  DefaultOperationProcessor(MicrosecondPositionProvider microsecondPositionProvider,
                                   OperationReceiver operationReceiver) {
    if (microsecondPositionProvider == null) {
      throw new OdinRuntimeException("MicrosecondPositionProvider must not be null");
    }
    this.microsecondPositionProvider = microsecondPositionProvider;
    if (operationReceiver == null) {
      throw new OdinRuntimeException("OperationReceiver must not be null");
    }
    this.operationReceiver = operationReceiver;
  }

  @Override
  public void send(Operation operation, long time) throws OdinException {
    OperationEvent operationEvent = new OperationEvent(operation, time);
    queue.add(operationEvent);
  }

  @Override
  public void run() {
    while (!exit) {
      OperationEvent nextEvent = queue.peek();
      long microsecondPosition = microsecondPositionProvider.getMicrosecondPosition();
      while (nextEvent != null && nextEvent.getTime() < microsecondPosition + forwardPollingTime) {
        nextEvent = queue.poll();
        if (nextEvent == null) {
          LOG.error("Next event in queue is null, where did it go?");
        } else {
          LOG.trace("Send operation {} at time {} ; device time {}", nextEvent.getOperation(),
              nextEvent.getTime(), microsecondPosition);
          try {
            operationReceiver.send(nextEvent.getOperation(), nextEvent.getTime());
          } catch (OdinException e) {
            LOG.error("Cannot action operation " + nextEvent.getOperation(), e);
          }
        }
      }

      try {
        Thread.sleep(refreshPeriod);
      } catch (InterruptedException e) {
        LOG.error("Thread interrupted", e);
        Thread.currentThread().interrupt();
      }

    }
  }

  @Override
  public void stop() {
    exit = true;
  }
}
