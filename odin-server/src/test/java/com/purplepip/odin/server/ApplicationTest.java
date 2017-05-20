package com.purplepip.odin.server;

import com.purplepip.odin.sequencer.OdinSequencer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Application test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {
  @Autowired
  private OdinSequencer sequencer;

  @Test
  public void contextLoads() throws Exception {
    assertThat(sequencer).isNotNull();
    assertThat(sequencer.isStarted()).isTrue();
  }
}
