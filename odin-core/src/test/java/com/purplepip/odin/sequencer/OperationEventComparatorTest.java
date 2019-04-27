package com.purplepip.odin.sequencer;

import static org.junit.Assert.assertEquals;

import com.purplepip.odin.music.operations.NoteOnOperation;
import com.purplepip.odin.operation.Operation;
import org.junit.jupiter.api.Test;

/**
 * OperationEventComparator test.
 */
class OperationEventComparatorTest {
  @Test
  void testOperationEventComparator() {
    final Operation operation = new NoteOnOperation(0,0,0);
    final OperationEvent event1 = new OperationEvent(operation, 0);
    final OperationEvent event2 = new OperationEvent(operation, 1);
    final OperationEvent event3 = new OperationEvent(operation, 1);
    final OperationEventComparator comparator = new OperationEventComparator();

    assertEquals(-1, comparator.compare(event1, event2));
    assertEquals(1, comparator.compare(event2, event1));
    assertEquals(0, comparator.compare(event2, event3));
  }
}