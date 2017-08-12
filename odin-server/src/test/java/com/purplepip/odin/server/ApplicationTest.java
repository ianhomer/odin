package com.purplepip.odin.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.sequencer.OdinSequencer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Application test.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest
public class ApplicationTest {
  @Autowired
  private OdinSequencer sequencer;

  @Test
  public void testContextLoads() throws Exception {
    assertThat(sequencer).isNotNull();
    assertThat(sequencer.isStarted()).isTrue();
  }
}
