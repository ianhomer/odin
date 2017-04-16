package com.purplepip.odin.sequencer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * Comparator for a operation message.
 */
public class OperationEventComparator implements Comparator<OperationEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(OperationEventComparator.class);

    public int compare(OperationEvent x, OperationEvent y) {
        if (x == null) {
            LOG.warn("Operation event should not be null");
            if (y == null) {
                return 0;
            }
            return -1;
        } else if (y == null) {
            LOG.warn("Operation event should not be null");
            return 1;
        }
        if (x.getTime() < y.getTime()) {
            return -1;
        } else if (x.getTime() > y.getTime()) {
            return 1;
        }
        return 0;
    }
}
