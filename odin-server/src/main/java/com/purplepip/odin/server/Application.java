package com.purplepip.odin.server;

import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.MidiSystemHelper;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.music.MeasureProvider;
import com.purplepip.odin.sequencer.SequenceBuilder;
import com.purplepip.odin.sequence.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

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

  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Autowired
  private MeasureProvider measureProvider;

  @Autowired
  private OdinSequencer sequencer;

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      new MidiSystemHelper().logInfo();

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

