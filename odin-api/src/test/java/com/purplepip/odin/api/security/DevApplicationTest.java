package com.purplepip.odin.api.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.sequencer.OdinSequencer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test coverage for dev mode.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class DevApplicationTest {
  @Autowired
  private OdinSequencer sequencer;

  @Test
  public void testContextLoads() {
    assertThat(sequencer).isNotNull();
  }
}
