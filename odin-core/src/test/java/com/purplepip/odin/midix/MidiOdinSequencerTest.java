package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.composition.StaticBeatsPerMinute;
import com.purplepip.odin.composition.measure.MeasureProvider;
import com.purplepip.odin.composition.measure.StaticBeatMeasureProvider;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.project.TransientProject;
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

      MeasureProvider measureProvider = new StaticBeatMeasureProvider(4);
      OdinSequencer sequencer = new OdinSequencer(
          new DefaultOdinSequencerConfiguration()
              .setClockStartOffset(10000)
              .setBeatsPerMinute(new StaticBeatsPerMinute(12000))
              .setMeasureProvider(measureProvider)
              .setOperationReceiver(
                  new OperationReceiverCollection(
                      operationReceiver,
                      new MidiOperationReceiver(midiDeviceWrapper)
                  )
              ).setMicrosecondPositionProvider(
                new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper))
              );

      ProjectContainer container = new ProjectContainer(new TransientProject());
      container.addApplyListener(sequencer);
      new ProjectBuilder(container)
          .addLayer("groove").withLayers("groove")
          .withOffset(16).withLength(16).addMetronome();
      container.apply();
      sequencer.start();

      try {
        lock.await(100, TimeUnit.MILLISECONDS);
      } finally {
        sequencer.stop();
      }

      assertEquals("Some events have not yet fired", 0, lock.getCount());
    }
  }
}
