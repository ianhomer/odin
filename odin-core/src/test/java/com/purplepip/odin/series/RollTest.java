package com.purplepip.odin.series;

import static junit.framework.TestCase.assertTrue;

import com.purplepip.odin.math.Real;
import com.purplepip.odin.math.Wholes;
import com.purplepip.odin.roll.Roll;
import org.junit.jupiter.api.Test;

/**
 * Roll Test.
 */
class RollTest {
  @Test
  void testSeries() {
    Roll heartBeat = new HeartBeat();
    long currentTime = System.currentTimeMillis();
    Real peekedTime = heartBeat.peek().getTime();
    Real poppedTime = heartBeat.peek().getTime();
    assertTrue("Peeked time in the past : " + peekedTime + " < " + currentTime,
        peekedTime.ge(Wholes.valueOf(currentTime)));
    assertTrue("Peeked time too far in the future : " + peekedTime + " > "
            + currentTime + " + 5000",
        peekedTime.lt(Wholes.valueOf(currentTime + 5000)));

    assertTrue("Popped time in the past : " + poppedTime + " < " + currentTime,
        peekedTime.gt(Wholes.valueOf(currentTime)));
    assertTrue("Popped time too far in the future : " + poppedTime + " > "
            + currentTime + " + 5000",
        peekedTime.lt(Wholes.valueOf(currentTime + 5000)));

    // TODO : Implement such that popping takes the next off the series
  }
}
