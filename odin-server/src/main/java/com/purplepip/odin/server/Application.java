package com.purplepip.odin.server;

import com.purplepip.odin.midix.MidiDeviceWrapper;
import com.purplepip.odin.midix.SynthesizerHelper;
import com.purplepip.odin.sequence.measure.MeasureProvider;
import com.purplepip.odin.sequencer.OdinSequencer;
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

  @Autowired
  private MidiDeviceWrapper midiDeviceWrapper;

  @Autowired
  private MeasureProvider measureProvider;

  @Autowired
  private OdinSequencer sequencer;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /**
   * Commands to execute on start up.
   *
   * @param ctx spring application context
   * @return command line runner
   */
  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      SynthesizerHelper synthesizerHelper = null;
      if (midiDeviceWrapper.isSynthesizer()) {
        synthesizerHelper =
            new SynthesizerHelper(midiDeviceWrapper.getSynthesizer());
        synthesizerHelper.loadGervillSoundBank(
            "Timbres Of Heaven GM_GS_XG_SFX V 3.4 Final.sf2");
      }

      sequencer.start();
      LOG.info("Odin Started.");
    };
  }
}

