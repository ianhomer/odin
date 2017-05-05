package com.purplepip.odin.series;

import static junit.framework.TestCase.assertTrue;

import com.purplepip.odin.sequence.SequenceRuntime;
import org.junit.Test;

/**
 * SequenceRuntime Test.
 */
public class SequenceRuntimeTest {
  @Test
  public void testSeries() {
    SequenceRuntime<Boolean> heartBeat = new HeartBeat();
    long currentTime = System.currentTimeMillis();
    long peekedTime = heartBeat.peek().getTime();
    long poppedTime = heartBeat.peek().getTime();
    assertTrue("Peeked time in the past : " + peekedTime + " < " + currentTime,
        peekedTime > currentTime);
    assertTrue("Peeked time too far in the future : " + peekedTime + " > "
            + currentTime + " + 1000",
        peekedTime < currentTime + 1000);

    assertTrue("Popped time in the past : " + poppedTime + " < " + currentTime,
        peekedTime > currentTime);
    assertTrue("Popped time too far in the future : " + poppedTime + " > "
            + currentTime + " + 1000",
        peekedTime < currentTime + 1000);

    // TODO : Implement such that popping takes the next off the series
  }
}
