package com.purplepip.odin.midix;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import com.purplepip.odin.sequencer.CapturingOperationReceiver;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.sequencer.OperationReceiverCollection;
import com.purplepip.odin.sequencer.SequenceBuilder;
import org.junit.Test;

/**
 * Odin sequencer test with real sends to MIDI.
 */
public class MidiOdinSequencerTest {
  @Test
  public void testSequencer() throws OdinException {
    MidiDeviceWrapper midiDeviceWrapper = new MidiDeviceWrapper();

    CapturingOperationReceiver operationReceiver = new CapturingOperationReceiver();

    MeasureProvider measureProvider = new StaticMeasureProvider(4);
    OdinSequencer sequencer = new OdinSequencer(
        new OdinSequencerConfiguration()
            .setBeatsPerMinute(new StaticBeatsPerMinute(120))
            .setMeasureProvider(measureProvider)
            .setOperationReceiver(
                new OperationReceiverCollection(
                  operationReceiver,
                  new MidiOperationReceiver(midiDeviceWrapper)
                )
            ).setMicrosecondPositionProvider(
                new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper)));

    new SequenceBuilder(sequencer).addMetronome();
    sequencer.start();

    while (sequencer.getClock().getCurrentBeat() < 8) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    sequencer.stop();

    assertEquals("Number of operations sent not correct", 16,
        operationReceiver.getList().size());
  }
}
