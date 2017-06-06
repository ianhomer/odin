package com.purplepip.odin.sequence;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.purplepip.odin.common.OdinRuntimeException;
import org.junit.Before;
import org.junit.Test;

/**
 * DefaultTickConverter test.
 */
public class DefaultTickConverterTest {
  private MicrosecondPositionProvider provider = mock(MicrosecondPositionProvider.class);
  private Clock clock;

  /**
   * Execute before tests.
   */
  @Before
  public void before() {
    when(provider.getMicrosecondPosition()).thenReturn((long) 0);
    clock = new Clock(new StaticBeatsPerMinute(120),
        new DefaultMicrosecondPositionProvider(), 1000);
    clock.start();
  }

  @Test
  public void testMicrosecondToMillisecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        RuntimeTicks.MILLISECOND, RuntimeTicks.MICROSECOND, 0);
    assertEquals(1000, converter.convert(1));
  }

  @Test
  public void testBeatToHalf() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        RuntimeTicks.BEAT, RuntimeTicks.HALF, 0);
    assertEquals(2, converter.convert(1));
  }

  @Test
  public void testMillisecondToBeat() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        RuntimeTicks.MILLISECOND, RuntimeTicks.BEAT, 0);
    assertEquals(200, converter.convert(100000));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testMillisecondToMeasure() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        RuntimeTicks.MILLISECOND, RuntimeTicks.MEASURE, 0);
    converter.convert(1);
  }

  @Test
  public void testBeatToMicrosecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        RuntimeTicks.BEAT, RuntimeTicks.MICROSECOND, 0);
    assertEquals(500000,converter.convert(1));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testBeatToMeasure() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        RuntimeTicks.BEAT, RuntimeTicks.MEASURE, 0);
    converter.convert(1);
  }

}