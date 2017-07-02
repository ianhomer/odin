package com.purplepip.odin.sequence.tick;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.sequence.BeatClock;
import com.purplepip.odin.sequence.DefaultTickConverter;
import com.purplepip.odin.sequence.MicrosecondPositionProvider;
import com.purplepip.odin.sequence.StaticBeatsPerMinute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * DefaultTickConverter test.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultTickConverterTest {
  @Mock
  private MicrosecondPositionProvider provider;
  private BeatClock clock;

  /**
   * Execute before tests.
   */
  @Before
  public void before() {
    when(provider.getMicroseconds()).thenReturn((long) 0);
    clock = new BeatClock(new StaticBeatsPerMinute(120),
        provider, 1000);
    clock.start();
  }

  @Test
  public void testMicrosecondToMillisecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> RuntimeTicks.MILLISECOND, () -> RuntimeTicks.MICROSECOND, () -> 0);
    assertEquals(1000, converter.convert(1));
  }

  @Test
  public void testMillisecondToMillisecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> RuntimeTicks.MILLISECOND, () -> RuntimeTicks.MILLISECOND, () -> 0);
    assertEquals(1, converter.convert(1));
  }

  @Test
  public void testBeatToHalf() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> RuntimeTicks.BEAT, () -> RuntimeTicks.HALF, () -> 0);
    assertEquals(2, converter.convert(1));
  }

  @Test
  public void testMillisecondToBeat() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> RuntimeTicks.MILLISECOND, () -> RuntimeTicks.BEAT, () -> 0);
    assertEquals(200, converter.convert(100000));
  }

  @Test
  public void testMillisecondToBeatWithOffset() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> RuntimeTicks.MILLISECOND, () -> RuntimeTicks.BEAT, () -> 5000);
    assertEquals(18, converter.convert(4000));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testMillisecondToMeasure() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> RuntimeTicks.MILLISECOND, () -> RuntimeTicks.MEASURE, () -> 0);
    converter.convert(1);
  }

  @Test
  public void testBeatToMicrosecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> RuntimeTicks.BEAT, () -> RuntimeTicks.MICROSECOND, () -> 0);
    assertEquals(500000,converter.convert(1));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testBeatToMeasure() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> RuntimeTicks.BEAT, () -> RuntimeTicks.MEASURE, () -> 0);
    converter.convert(1);
  }

}