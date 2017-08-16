package com.purplepip.odin.store.domain;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.common.OdinException;
import com.purplepip.odin.project.ProjectContainer;
import com.purplepip.odin.sequence.TimeUnit;
import com.purplepip.odin.sequence.layer.Layer;
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
public class PersistableLayoutTest {
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
  public void testLayoutEquality() throws OdinException {
    builder.addLayer("test-layer");
    PersistableLayer layer2 = new PersistableLayer();
    layer2.setName("test-layer");
    layer2.setLength(-1);
    PersistableTick tick = new PersistableTick();
    tick.setDenominator(1);
    tick.setNumerator(1);
    tick.setTimeUnit(TimeUnit.BEAT);
    layer2.setTick(tick);
    Layer layer1 = builder.getLayerByOrder(0);
    assertEquals(layer1, layer2);
  }
}
