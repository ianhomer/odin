package com.purplepip.odin.server;

import com.purplepip.odin.midi.MidiDeviceMicrosecondPositionProvider;
import com.purplepip.odin.midi.MidiDeviceWrapper;
import com.purplepip.odin.midi.MidiOperationReceiver;
import com.purplepip.odin.midi.MidiSystemHelper;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.sequencer.OdinSequencerConfiguration;
import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.music.StaticMeasureProvider;
import com.purplepip.odin.sequencer.SequenceBuilder;
import com.purplepip.odin.series.StaticBeatsPerMinute;
import com.purplepip.odin.series.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sound.midi.MidiDevice;

/**
 * Odin Application.
 */
@SpringBootApplication
@ComponentScan({"com.purplepip.odin"})
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            MidiDeviceWrapper midiDeviceWrapper = new MidiDeviceWrapper(false);
            MeasureProvider measureProvider = new StaticMeasureProvider(4);
            OdinSequencer sequencer = new OdinSequencer(
                    new OdinSequencerConfiguration()
                            .setBeatsPerMinute(new StaticBeatsPerMinute(120))
                            .setMeasureProvider(measureProvider)
                            .setOperationReceiver(new MidiOperationReceiver(midiDeviceWrapper))
                            .setMicrosecondPositionProvider(new MidiDeviceMicrosecondPositionProvider(midiDeviceWrapper)));
            new SequenceBuilder(sequencer, measureProvider)
                    .addMetronome()
                    .addPattern(Tick.BEAT, 2)
                    .withChannel(9).withNote(42).addPattern(Tick.QUARTER, 61435)
                    .addPattern(Tick.EIGHTH, 127)
                    .withNote(46).addPattern(Tick.TWO_THIRDS, 7);
            sequencer.start();
            LOG.info("Odin Started.");
        };
    }
}

