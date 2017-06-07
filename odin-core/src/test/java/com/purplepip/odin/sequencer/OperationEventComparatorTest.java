package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.operations.NoteOnOperation;
import org.junit.Test;

/**
 * OperationEventComparator test.
 */
public class OperationEventComparatorTest {
  @Test
  public void testOperationEventComparator() {
    final Operation operation = new NoteOnOperation(0,0,0);
    final OperationEvent event1 = new OperationEvent(operation, 0);
    final OperationEvent event2 = new OperationEvent(operation, 1);
    final OperationEventComparator comparator = new OperationEventComparator();

    assertEquals(0, comparator.compare(null, null));
    assertEquals(1, comparator.compare(event1, null));
    assertEquals(-1, comparator.compare(null, event1));
    assertEquals(-1, comparator.compare(event1, event2));
    assertEquals(1, comparator.compare(event2, event1));
  }
}