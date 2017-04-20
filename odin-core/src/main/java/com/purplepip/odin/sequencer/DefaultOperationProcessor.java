package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.series.MicrosecondPositionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PriorityQueue;

/**
 * Processor responsible for taking MIDI messages off the queue and sending them to the MIDI
 * receivers in a timely manner.  Note that according to the Java MIDI specification messages
 * should not be sent to devices too early.  The time argument in the receiver send method call is
 * really for synchronisation.  If events are fired into a receiver too early then the MIDI
 * instrument might end up handling them early.
 */
public class DefaultOperationProcessor implements OperationProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultOperationProcessor.class);

  boolean exit = false;
  // TODO : Externalise configuration
  private long refreshPeriod = 10;
  private long forwardPollingTime = refreshPeriod * 1000 * 5;
  private PriorityQueue<OperationEvent> queue = new PriorityQueue<>(127,
      new OperationEventComparator());
  private MicrosecondPositionProvider microsecondPositionProvider;
  private OperationReceiver operationReceiver;

  public DefaultOperationProcessor(MicrosecondPositionProvider microsecondPositionProvider,
                                   OperationReceiver operationReceiver) {
    if (microsecondPositionProvider == null) {
      throw new RuntimeException("MicrosecondPositionProvider must not be null");
    }
    this.microsecondPositionProvider = microsecondPositionProvider;
    if (operationReceiver == null) {
      throw new RuntimeException("OperationReceiver must not be null");
    }
    this.operationReceiver = operationReceiver;
  }

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
          // TODO : Understand why this might have happened, and if can't reproduce then remove
          // this branch.
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
      }

    }
  }

  @Override
  public void stop() {
    exit = true;
  }
}
