package com.purplepip.odin.server;

import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.sequence.Ticks;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequencer.OdinSequencer;
import com.purplepip.odin.server.rest.PersistableSequenceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Odin Application.
 */
@SpringBootApplication
@ComponentScan({"com.purplepip.odin"})
public class Application {
  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Autowired
  private MeasureProvider measureProvider;

  @Autowired
  private OdinSequencer sequencer;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      new PersistableSequenceBuilder(sequencer.getProject())
          .addMetronome()
          .addPattern(Ticks.BEAT, 2)
          .withChannel(9).withNote(42).addPattern(Ticks.QUARTER, 61435)
          .addPattern(Ticks.EIGHTH, 127)
          .withNote(46).addPattern(Ticks.TWO_THIRDS, 7);
      sequencer.start();
      LOG.info("Odin Started.");
    };
  }
}

