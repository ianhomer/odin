package com.purplepip.odin.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.sequencer.OdinSequencer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test coverage for dev mode.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("dev")
class DevApplicationTest {
  @Autowired
  private OdinSequencer sequencer;

  @Test
  void testContextLoads() {
    assertThat(sequencer).isNotNull();
  }
}
