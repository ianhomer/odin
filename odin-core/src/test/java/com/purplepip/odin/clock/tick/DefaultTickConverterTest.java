package com.purplepip.odin.clock.tick;

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static com.purplepip.odin.clock.tick.Ticks.HALF;
import static com.purplepip.odin.clock.tick.Ticks.MEASURE;
import static com.purplepip.odin.clock.tick.Ticks.MICROSECOND;
import static com.purplepip.odin.clock.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.math.Wholes;
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
    clock = newPrecisionBeatClock(120, provider);
    clock.start();
  }

  @Test
  public void testMicrosecondToMillisecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> MICROSECOND, () -> Wholes.ZERO);
    assertEquals(Whole.valueOf(1000), converter.convert(Wholes.ONE));
  }

  @Test
  public void testMillisecondToMillisecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> MILLISECOND, () -> Wholes.ZERO);
    assertEquals(Wholes.ONE, converter.convert(Wholes.ONE));
  }

  @Test
  public void testBeatToHalf() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> BEAT, () -> HALF, () -> Wholes.ZERO);
    assertEquals(Wholes.TWO, converter.convert(Wholes.ONE));
  }

  @Test
  public void testMillisecondToBeat() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> BEAT, () -> Wholes.ZERO);
    assertEquals(Whole.valueOf(200), converter.convert(Whole.valueOf(100000)));
  }

  @Test
  public void testMillisecondToBeatWithOffset() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> BEAT, () -> Whole.valueOf(5000));
    assertEquals(Whole.valueOf(18), converter.convert(Whole.valueOf(4000)));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testMillisecondToMeasure() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> MEASURE, () -> Wholes.ZERO);
    converter.convert(Wholes.ONE);
  }

  @Test
  public void testBeatToMicrosecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> BEAT, () -> MICROSECOND, () -> Wholes.ZERO);
    assertEquals(Whole.valueOf(500000), converter.convert(Wholes.ONE));
  }

  @Test(expected = OdinRuntimeException.class)
  public void testBeatToMeasure() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> BEAT, () -> MEASURE, () -> Wholes.ZERO);
    converter.convert(Wholes.ONE);
  }

}