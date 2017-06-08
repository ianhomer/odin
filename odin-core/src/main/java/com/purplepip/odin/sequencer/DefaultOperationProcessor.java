package com.purplepip.odin.sequencer;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.sequence.Clock;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

  private long refreshPeriod = 10;
  private long forwardPollingTime = refreshPeriod * 1000 * 5;
  private PriorityQueue<OperationEvent> queue = new PriorityQueue<>(127,
      new OperationEventComparator());
  private Clock clock;
  private OperationReceiver operationReceiver;
  private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);

  /**
   * Create an operation processor.
   *
   * @param clock clock
   * @param operationReceiver operation receiver
   */
  DefaultOperationProcessor(Clock clock, OperationReceiver operationReceiver) {
    this.clock = clock;
    if (operationReceiver == null) {
      throw new OdinRuntimeException("OperationReceiver must not be null");
    }
    this.operationReceiver = operationReceiver;
    DefaultOperationProcessorExecutor executor = new DefaultOperationProcessorExecutor();
    scheduledPool.scheduleWithFixedDelay(executor, 0, refreshPeriod, TimeUnit.MILLISECONDS);
  }

  @Override
  public void send(Operation operation, long time) throws OdinException {
    OperationEvent operationEvent = new OperationEvent(operation, time);
    queue.add(operationEvent);
  }

  @Override
  public void close() {
    scheduledPool.shutdown();
  }

  class DefaultOperationProcessorExecutor implements Runnable {
    @Override
    public void run() {
      OperationEvent nextEvent = queue.peek();
      long microsecondPosition = clock.getMicrosecondPosition();
      LOG.debug("Processing operations : {}", microsecondPosition);
      while (nextEvent != null && nextEvent.getTime()
          < microsecondPosition + forwardPollingTime) {
        nextEvent = queue.poll();
        if (nextEvent == null) {
          LOG.error("Next event in queue is null, where did it go?");
        } else {
          try {
            operationReceiver.send(nextEvent.getOperation(), nextEvent.getTime());
          } catch (OdinException e) {
            LOG.error("Cannot action operation " + nextEvent.getOperation(), e);
          }
        }
      }
    }
  }

}
