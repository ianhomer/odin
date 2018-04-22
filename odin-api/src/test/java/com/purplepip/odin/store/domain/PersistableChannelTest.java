package com.purplepip.odin.store.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.creation.channel.Channel;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.store.PersistablePerformanceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Performance repository test.
 */
@RunWith(SpringRunner.class)
@Slf4j
public class PersistableChannelTest {
  private PersistablePerformanceBuilder builder;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    PersistablePerformance performance = new PersistablePerformance();
    builder = new PersistablePerformanceBuilder(new DefaultPerformanceContainer(performance));
  }

  @Test
  public void testChannelEquality() throws OdinException {
    builder.withChannel(0).changeProgramTo("test1");
    Channel programChange0 = builder.getChannelByOrder(0);
    builder.withChannel(0).changeProgramTo("test1");
    Channel programChange1 = builder.getChannelByOrder(1);
    assertThat(programChange0, equalTo(programChange1));
  }
}
