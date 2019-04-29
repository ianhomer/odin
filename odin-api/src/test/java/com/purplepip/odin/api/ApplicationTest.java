package com.purplepip.odin.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.sequencer.OdinSequencer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Application test.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ApplicationTest {
  @Autowired
  private OdinSequencer sequencer;

  @Test
  void testContextLoads() {
    assertThat(sequencer).isNotNull();
    assertThat(sequencer.isRunning()).isTrue();
  }
}
