package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OperationReceiver;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.ProjectBuilder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Odin sequencer test with real sends to MIDI.
 */
@Slf4j
public class MidiOdinSequencerTest {
  @Test
  public void testSequencer() throws OdinException, InterruptedException {
    try (MidiDeviceWrapper midiDeviceWrapper = new MidiDeviceWrapper(true)) {
      final CountDownLatch lock = new CountDownLatch(16);

      OperationReceiver operationReceiver = (operation, time) -> {
        LOG.debug("Operation countdown {}", lock.getCount());
        lock.countDown();
      };

      MeasureProvider measureProvider = new StaticMeasureProvider(4);
      OdinSequencer sequencer = new OdinSequencer(
          new DefaultOdinSequencerConfiguration()
              .setBeatsPerMinute(new StaticBeatsPerMinute(10000))
              .setMeasureProvider(measureProvider)
              .setOperationReceiver(
                  new OperationReceiverCollection(
                      operationReceiver,
                      new MidiOperationReceiver(midiDeviceWrapper)
                  )
              ).setMicrosecondPositionProvider(
                new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper))
              );

      /* TODO : Reduce offset and lock.await time and fix code so test reliably runs */
      new ProjectBuilder(sequencer.getProject()).withOffset(0).withLength(8).addMetronome();
      lock.await(2000, TimeUnit.MILLISECONDS);

      sequencer.start();

      try {
        lock.await(1000, TimeUnit.MILLISECONDS);
      } finally {
        sequencer.stop();
      }

      assertEquals("Some events have not yet fired", 0, lock.getCount());
    }
  }
}
