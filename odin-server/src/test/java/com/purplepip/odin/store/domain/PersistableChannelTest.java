package com.purplepip.odin.store.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequencer.Channel;
import com.purplepip.odin.store.PersistableProjectBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Project repository test.
 */
@RunWith(SpringRunner.class)
@Slf4j
public class PersistableChannelTest {
  private PersistableProjectBuilder builder;

  /**
   * Set up.
   */
  @Before
  public void setUp() {
    PersistableProject project = new PersistableProject();
    builder = new PersistableProjectBuilder(new ProjectContainer(project));
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
