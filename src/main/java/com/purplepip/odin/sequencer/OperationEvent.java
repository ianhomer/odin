package com.purplepip.odin.sequencer;

import javax.sound.midi.MidiMessage;

/**
 * Operation event that should fire at the given time
 */
public class OperationEvent {
    private Operation operation;
    private long time;

    public OperationEvent(Operation operation, long time) {
        this.operation = operation;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public Operation getOperation() {
        return operation;
    }

}
