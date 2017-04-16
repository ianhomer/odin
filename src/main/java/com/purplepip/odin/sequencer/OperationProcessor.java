package com.purplepip.odin.sequencer;

import com.purplepip.odin.OdinException;

/**
 * Operation Processor that is responsible for firing the operations when the time is right.
 */
public interface OperationProcessor extends Runnable {
    void send(Operation operation, long time) throws OdinException;
    void stop();
}
