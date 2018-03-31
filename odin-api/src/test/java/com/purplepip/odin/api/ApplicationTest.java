package com.purplepip.odin.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.purplepip.odin.sequencer.OdinSequencer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Application test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {
  @Autowired
  private OdinSequencer sequencer;

  @Test
  public void testContextLoads() {
    assertThat(sequencer).isNotNull();
    assertThat(sequencer.isStarted()).isTrue();
  }
}
