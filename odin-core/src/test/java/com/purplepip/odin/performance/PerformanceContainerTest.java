package com.purplepip.odin.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.purplepip.odin.demo.SimplePerformance;
import com.purplepip.odin.sequencer.PerformanceBuilder;
import org.junit.jupiter.api.Test;

/**
 * Project container test.
 */
class PerformanceContainerTest {
  @Test
  void testSaveListener() {
    PerformanceSaveListener listener = mock(PerformanceSaveListener.class);
    DefaultPerformanceContainer container = new DefaultPerformanceContainer();
    Performance performance = new TransientPerformance();
    container.setPerformance(performance);
    container.addSaveListener(listener);
    container.save();
    container.save();
    verify(listener, times(2)).onPerformanceSave(performance);
    container.removeSaveListener(listener);
    container.save();
    verify(listener, times(2)).onPerformanceSave(performance);
  }

  @Test
  void testApplyListener() {
    PerformanceApplyListener listener = mock(PerformanceApplyListener.class);
    DefaultPerformanceContainer container = new DefaultPerformanceContainer();
    Performance performance = new TransientPerformance();
    container.setPerformance(performance);
    container.addApplyListener(listener);
    container.apply();
    container.apply();
    verify(listener, times(2)).onPerformanceApply(performance);
    container.removeApplyListener(listener);
    container.apply();
    verify(listener, times(2)).onPerformanceApply(performance);
  }

  @Test
  void testLoadListener() {
    PerformanceLoadListener listener = mock(PerformanceLoadListener.class);
    DefaultPerformanceContainer container = new DefaultPerformanceContainer();
    Performance performance = new TransientPerformance();
    container.setPerformance(performance);
    container.addLoadListener(listener);
    container.load();
    container.load();
    verify(listener, times(2)).onPerformanceLoad(container);
    container.removeLoadListener(listener);
    container.load();
    verify(listener, times(2)).onPerformanceLoad(container);
  }

  @Test
  void testIsEmpty() {
    DefaultPerformanceContainer container = new DefaultPerformanceContainer();
    container.setPerformance(new TransientPerformance());
    assertTrue("Performance should be empty", container.isEmpty());
    PerformanceBuilder builder = new PerformanceBuilder(container);
    builder.addMetronome();
    assertFalse("Performance should NOT be empty", container.isEmpty());
  }

  @Test
  void testMixin() {
    DefaultPerformanceContainer container = new DefaultPerformanceContainer();
    container.setPerformance(new TransientPerformance());
    container.mixin(new SimplePerformance());
    assertEquals(1, container.getSequenceStream().count());
  }
}
