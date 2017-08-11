package com.purplepip.odin.series;

import static junit.framework.TestCase.assertTrue;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Whole;
import com.purplepip.odin.sequence.Roll;
import org.junit.Test;

/**
 * Roll Test.
 */
public class RollTest {
  @Test
  public void testSeries() {
    Roll<Boolean> heartBeat = new HeartBeat();
    long currentTime = System.currentTimeMillis();
    Real peekedTime = heartBeat.peek().getTime();
    Real poppedTime = heartBeat.peek().getTime();
    assertTrue("Peeked time in the past : " + peekedTime + " < " + currentTime,
        peekedTime.gt(Whole.valueOf(currentTime)));
    assertTrue("Peeked time too far in the future : " + peekedTime + " > "
            + currentTime + " + 1000",
        peekedTime.lt(Whole.valueOf(currentTime + 1000)));

    assertTrue("Popped time in the past : " + poppedTime + " < " + currentTime,
        peekedTime.gt(Whole.valueOf(currentTime)));
    assertTrue("Popped time too far in the future : " + poppedTime + " > "
            + currentTime + " + 1000",
        peekedTime.lt(Whole.valueOf(currentTime + 1000)));

    // TODO : Implement such that popping takes the next off the series
  }
}
