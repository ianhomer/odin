package com.purplepip.odin.server;

import com.purplepip.odin.midix.MidiDeviceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring Boot of Odin.
 */
@Configuration
public class OdinConfiguration {
  @Bean
  public MidiDeviceWrapper MidiDeviceWrapper() {
    return new MidiDeviceWrapper();
  }
}
