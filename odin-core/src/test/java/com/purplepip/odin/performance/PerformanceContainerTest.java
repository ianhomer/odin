package com.purplepip.odin.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.purplepip.odin.demo.SimplePerformance;
import com.purplepip.odin.sequencer.PerformanceBuilder;
import org.junit.Test;

/**
 * Project container test.
 */
public class PerformanceContainerTest {
  @Test
  public void testSaveListener() {
    PerformanceSaveListener listener = mock(PerformanceSaveListener.class);
    PerformanceContainer container = new PerformanceContainer();
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
  public void testApplyListener() {
    PerformanceApplyListener listener = mock(PerformanceApplyListener.class);
    PerformanceContainer container = new PerformanceContainer();
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
  public void testLoadListener() {
    PerformanceLoadListener listener = mock(PerformanceLoadListener.class);
    PerformanceContainer container = new PerformanceContainer();
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
  public void testIsEmpty() {
    PerformanceContainer container = new PerformanceContainer();
    container.setPerformance(new TransientPerformance());
    assertTrue("Performance should be empty", container.isEmpty());
    PerformanceBuilder builder = new PerformanceBuilder(container);
    builder.addMetronome();
    assertFalse("Performance should NOT be empty", container.isEmpty());
  }

  @Test
  public void testMixin() {
    PerformanceContainer container = new PerformanceContainer();
    container.setPerformance(new TransientPerformance());
    container.mixin(new SimplePerformance());
    assertEquals(1, container.getSequenceStream().count());
  }
}
