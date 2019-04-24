package com.purplepip.odin.midix;

import static com.purplepip.odin.system.Environments.isAudioEnabled;
import static com.purplepip.odin.system.Environments.newEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import com.purplepip.odin.clock.beats.StaticBeatsPerMinute;
import com.purplepip.odin.clock.measure.MeasureProvider;
import com.purplepip.odin.clock.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.devices.DeviceUnavailableException;
import com.purplepip.odin.operation.OperationHandler;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.performance.TransientPerformance;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.PerformanceBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/** Odin sequencer test with real sends to MIDI. */
@Slf4j
public class MidiOdinSequencerTest {
  @Test
  public void testSequencer() throws InterruptedException, DeviceUnavailableException {
    assumeTrue(isAudioEnabled());
    try (MidiDevice sink = newEnvironment().findOneSinkOrThrow(MidiHandle.class)) {
      final CountDownLatch lock = new CountDownLatch(16);

      OperationHandler operationReceiver =
          (operation, time) -> {
            LOG.debug("Operation countdown {}", lock.getCount());
            lock.countDown();
          };

      MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
      OdinSequencer sequencer =
          new OdinSequencer(
              new DefaultOdinSequencerConfiguration()
                  .setClockStartOffset(10000)
                  .setBeatsPerMinute(new StaticBeatsPerMinute(12000))
                  .setMeasureProvider(measureProvider)
                  .setOperationReceiver(
                      new OperationReceiverCollection(
                          operationReceiver,
                          new MidiOperationReceiver(sink)))
                  .setMicrosecondPositionProvider(sink));

      DefaultPerformanceContainer container =
          new DefaultPerformanceContainer(new TransientPerformance());
      container.addApplyListener(sequencer);
      new PerformanceBuilder(container)
          .addLayer("groove")
          .withLayers("groove")
          .withOffset(16)
          .withLength(16)
          .addMetronome();
      container.apply();
      sequencer.prepare();
      sequencer.start();

      try {
        lock.await(5000, TimeUnit.MILLISECONDS);
      } finally {
        sequencer.stop();
        sequencer.shutdown();
      }

      assertEquals("Some events have not yet fired", 0, lock.getCount());
    }
  }
}
