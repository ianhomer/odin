package com.purplepip.odin.server;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.midix.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiOperationReceiver;
import com.purplepip.odin.midix.MidiSystemWrapper;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequence.measure.StaticMeasureProvider;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.DefaultOdinSequencerConfiguration;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring Boot of Odin.
 */
@Configuration
public class OdinConfiguration {
  @Bean
  public MidiDeviceWrapper midiDeviceWrapper() {
    return new MidiDeviceWrapper();
  }

  @Bean
  public MeasureProvider measureProvider() {
    return new StaticMeasureProvider(4);
  }

  @Bean
  public MidiSystemWrapper midiSystemWrapper() {
    return new MidiSystemWrapper();
  }

  @Bean
  public OdinSequencer odinSequencer(MeasureProvider measureProvider,
                                     MidiDeviceWrapper midiDeviceWrapper) throws OdinException {
    return new OdinSequencer(
        new DefaultOdinSequencerConfiguration()
            .setBeatsPerMinute(new StaticBeatsPerMinute(120))
            .setMeasureProvider(measureProvider)
            .setOperationReceiver(new MidiOperationReceiver(midiDeviceWrapper))
            .setMicrosecondPositionProvider(new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper)));

  }
}
