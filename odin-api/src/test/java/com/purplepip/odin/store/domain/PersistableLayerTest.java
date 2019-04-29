package com.purplepip.odin.store.domain;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.clock.tick.TimeUnit;
import com.purplepip.odin.creation.layer.Layer;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.performance.DefaultPerformanceContainer;
import com.purplepip.odin.store.PersistablePerformanceBuilder;
import com.purplepip.odin.store.StoreTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Performance repository test.
 */
@ExtendWith(SpringExtension.class)
@StoreTest
public class PersistableLayerTest {
  private PersistablePerformanceBuilder builder;

  /**
   * Set up.
   */
  @BeforeEach
  public void setUp() {
    PersistablePerformance performance = new PersistablePerformance();
    builder = new PersistablePerformanceBuilder(new DefaultPerformanceContainer(performance));
  }

  @Test
  public void testLayerEquality() {
    builder.addLayer("test-layer");
    PersistableLayer layer2 = new PersistableLayer();
    layer2.setName("test-layer");
    layer2.setLength(Wholes.MINUS_ONE);
    layer2.setOffset(Wholes.ZERO);
    PersistableTick tick = new PersistableTick();
    tick.setDenominator(1);
    tick.setNumerator(1);
    tick.setTimeUnit(TimeUnit.BEAT);
    layer2.setTick(tick);
    Layer layer1 = builder.getLayerByOrder(0);
    assertEquals(layer2, layer1);
  }
}
