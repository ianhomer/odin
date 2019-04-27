package com.purplepip.odin.clock.tick;

import static com.purplepip.odin.clock.PrecisionBeatClock.newPrecisionBeatClock;
import static com.purplepip.odin.clock.tick.Ticks.BEAT;
import static com.purplepip.odin.clock.tick.Ticks.HALF;
import static com.purplepip.odin.clock.tick.Ticks.MEASURE;
import static com.purplepip.odin.clock.tick.Ticks.MICROSECOND;
import static com.purplepip.odin.clock.tick.Ticks.MILLISECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.purplepip.odin.clock.BeatClock;
import com.purplepip.odin.clock.MicrosecondPositionProvider;
import com.purplepip.odin.common.OdinRuntimeException;
import com.purplepip.odin.math.Wholes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * DefaultTickConverter test.
 */
class DefaultTickConverterTest {
  private BeatClock clock;

  /**
   * Execute before tests.
   */
  @BeforeEach
  void setUp() {
    MicrosecondPositionProvider provider = mock(MicrosecondPositionProvider.class);
    when(provider.getMicroseconds()).thenReturn((long) 0);
    clock = newPrecisionBeatClock(120, provider);
    clock.start();
  }

  @Test
  void testMicrosecondToMillisecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> MICROSECOND, () -> Wholes.ZERO);
    assertEquals(Wholes.valueOf(1000), converter.convert(Wholes.ONE));
  }

  @Test
  void testMillisecondToMillisecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> MILLISECOND, () -> Wholes.ZERO);
    assertEquals(Wholes.ONE, converter.convert(Wholes.ONE));
  }

  @Test
  void testBeatToHalf() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> BEAT, () -> HALF, () -> Wholes.ZERO);
    assertEquals(Wholes.TWO, converter.convert(Wholes.ONE));
  }

  @Test
  void testMillisecondToBeat() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> BEAT, () -> Wholes.ZERO);
    assertEquals(Wholes.valueOf(200), converter.convert(Wholes.valueOf(100000)));
  }

  @Test
  void testMillisecondToBeatWithOffset() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> BEAT, () -> Wholes.valueOf(5000));
    assertEquals(Wholes.valueOf(18), converter.convert(Wholes.valueOf(4000)));
  }

  @Test
  void testMillisecondToMeasure() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> MILLISECOND, () -> MEASURE, () -> Wholes.ZERO);
    assertThrows(OdinRuntimeException.class, () ->
        converter.convert(Wholes.ONE)
    );
  }

  @Test
  void testBeatToMicrosecond() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> BEAT, () -> MICROSECOND, () -> Wholes.ZERO);
    assertEquals(Wholes.valueOf(500000), converter.convert(Wholes.ONE));
  }

  @Test
  void testBeatToMeasure() {
    DefaultTickConverter converter = new DefaultTickConverter(clock,
        () -> BEAT, () -> MEASURE, () -> Wholes.ZERO);
    assertThrows(OdinRuntimeException.class, () ->
        converter.convert(Wholes.ONE)
    );
  }

}